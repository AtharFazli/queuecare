package com.queuecare.service;

import com.queuecare.dto.ReservationForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Category;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.Service;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.enums.Role;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.repository.CategoryRepository;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import com.queuecare.repository.ServiceRepository;
import com.queuecare.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regresi konkurensi C28: banyak pelanggan memesan satu slot yang sama bersamaan. */
@SpringBootTest
class ReservationConcurrencyTest {

    private static final int PEMESAN = 12;
    private static final int KAPASITAS = 3;

    @Autowired private ReservationService reservationService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BusinessRepository businessRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BusinessScheduleRepository scheduleRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private QueueRepository queueRepository;

    private Business bisnis;
    private Service layanan;
    private User owner;
    private User pelanggan;
    private LocalDate tanggal;

    @Test
    void pemesananBersamaanTidakBocorDanTidakDuplicate() throws Exception {
        owner = userRepository.save(new User("Owner Konkurensi", "owner-konkurensi@queuecare.id", "hash", Role.OWNER));
        pelanggan = userRepository.save(new User("Pelanggan Konkurensi", "pelanggan-konkurensi@queuecare.id", "hash", Role.CUSTOMER));
        Category kategori = categoryRepository.findAllByOrderByNameAsc().get(0);

        bisnis = new Business();
        bisnis.setOwner(owner);
        bisnis.setName("Bisnis Uji Konkurensi");
        bisnis.setCategory(kategori);
        bisnis.setAddress("Alamat uji");
        bisnis.setStatus(BusinessStatus.ACTIVE);
        bisnis.setMaxCapacity(KAPASITAS);
        bisnis.setActiveStaffCount(1);
        bisnis = businessRepository.save(bisnis);

        layanan = new Service();
        layanan.setBusiness(bisnis);
        layanan.setName("Layanan Uji Konkurensi");
        layanan.setPrice(new BigDecimal("10000"));
        layanan.setEstimatedDurationMinutes(30);
        layanan.setActive(true);
        layanan = serviceRepository.save(layanan);

        tanggal = LocalDate.now().plusDays(1);
        BusinessSchedule jadwal = new BusinessSchedule();
        jadwal.setBusiness(bisnis);
        jadwal.setDayOfWeek(BusinessSchedule.namaHari(tanggal));
        jadwal.setOpenTime(LocalTime.of(9, 0));
        jadwal.setCloseTime(LocalTime.of(17, 0));
        jadwal.setClosed(false);
        scheduleRepository.save(jadwal);

        CountDownLatch mulai = new CountDownLatch(1);
        CountDownLatch selesai = new CountDownLatch(PEMESAN);
        ExecutorService kumpulan = Executors.newFixedThreadPool(PEMESAN);
        ConcurrentLinkedQueue<Object> hasil = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < PEMESAN; i++) {
            kumpulan.submit(() -> {
                try {
                    mulai.await();
                    ReservationForm form = new ReservationForm();
                    form.setTanggal(tanggal);
                    form.setJam(LocalTime.of(9, 0));
                    hasil.add(reservationService.buat(layanan.getId(), form, pelanggan));
                } catch (Throwable t) {
                    hasil.add(t);
                } finally {
                    selesai.countDown();
                }
            });
        }
        mulai.countDown();
        assertTrue(selesai.await(90, TimeUnit.SECONDS), "semua thread harus selesai");
        kumpulan.shutdown();

        List<Reservation> berhasil = hasil.stream().filter(Reservation.class::isInstance)
                .map(Reservation.class::cast).toList();
        List<Throwable> galat = hasil.stream().filter(Throwable.class::isInstance)
                .map(Throwable.class::cast).toList();
        List<Throwable> bocor = galat.stream().filter(t ->
                !(t instanceof IllegalArgumentException && "Slot ini sudah penuh.".equals(t.getMessage()))).toList();

        assertEquals(KAPASITAS, berhasil.size(), "sukses harus sama dengan kapasitas slot");
        assertTrue(bocor.isEmpty(), () -> "tidak boleh ada galat bocor: "
                + bocor.stream().map(t -> t.getClass().getSimpleName() + ": " + t.getMessage()).toList());

        Set<String> kode = berhasil.stream().map(Reservation::getReservationCode).collect(java.util.stream.Collectors.toSet());
        assertEquals(berhasil.size(), kode.size(), "kode reservasi harus unik");

        List<Queue> antrean = queueRepository.antreanBisnis(bisnis, tanggal);
        assertEquals(berhasil.size(), antrean.size(), "jumlah antrean sama dengan jumlah sukses");
        List<Integer> nomor = antrean.stream().map(Queue::getQueueNumber).toList();
        assertEquals(KAPASITAS, nomor.size());
        for (int i = 0; i < nomor.size(); i++) {
            assertEquals(i + 1, nomor.get(i), "nomor antrean harus unik dan berurutan");
        }
    }

    @AfterEach
    void bersihkan() {
        if (bisnis != null && tanggal != null) {
            queueRepository.antreanBisnis(bisnis, tanggal).forEach(queueRepository::delete);
        }
        reservationRepository.findAll().stream()
                .filter(r -> r.getBusiness() != null && bisnis != null && r.getBusiness().getId().equals(bisnis.getId()))
                .forEach(reservationRepository::delete);
        if (layanan != null) serviceRepository.deleteById(layanan.getId());
        scheduleRepository.findAll().stream()
                .filter(s -> bisnis != null && s.getBusiness().getId().equals(bisnis.getId()))
                .forEach(scheduleRepository::delete);
        if (bisnis != null) businessRepository.deleteById(bisnis.getId());
        if (pelanggan != null) userRepository.deleteById(pelanggan.getId());
        if (owner != null) userRepository.deleteById(owner.getId());
    }
}
