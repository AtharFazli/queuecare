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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regresi konkurensi lintas bisnis: dua bisnis berbeda, tanggal sama, banyak thread
 * memesan bersamaan di kedua bisnis. Memastikan kunci baris bisnis tidak saling
 * mengunci bisnis lain dan nomor antrean unik per bisnis.
 */
@SpringBootTest
class ReservationCrossBusinessConcurrencyTest {

    private static final int PEMESAN_PER_BISNIS = 8;
    private static final int KAPASITAS = 4;

    @Autowired private ReservationService reservationService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BusinessRepository businessRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BusinessScheduleRepository scheduleRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private QueueRepository queueRepository;

    @Test
    void pemesananLintasBisnisTidakBocorDanTidakDuplicate() throws Exception {
        User owner = userRepository.save(new User("Owner Lintas", "owner-lintas@queuecare.id", "hash", Role.OWNER));
        User pelanggan = userRepository.save(new User("Pelanggan Lintas", "pelanggan-lintas@queuecare.id", "hash", Role.CUSTOMER));
        Category kategori = categoryRepository.findAllByOrderByNameAsc().get(0);
        LocalDate tanggal = LocalDate.now().plusDays(1);

        Business bisnisA = buatBisnis(owner, kategori, "Bisnis A Lintas", tanggal);
        Business bisnisB = buatBisnis(owner, kategori, "Bisnis B Lintas", tanggal);
        Service layananA = buatLayanan(bisnisA, "Layanan A Lintas");
        Service layananB = buatLayanan(bisnisB, "Layanan B Lintas");

        List<Service> layanan = List.of(layananA, layananB);
        CountDownLatch mulai = new CountDownLatch(1);
        int total = PEMESAN_PER_BISNIS * 2;
        CountDownLatch selesai = new CountDownLatch(total);
        ExecutorService kumpulan = Executors.newFixedThreadPool(total);
        ConcurrentLinkedQueue<Object> hasil = new ConcurrentLinkedQueue<>();
        for (Service layananSatu : layanan) {
            for (int i = 0; i < PEMESAN_PER_BISNIS; i++) {
                kumpulan.submit(() -> {
                    try {
                        mulai.await();
                        ReservationForm form = new ReservationForm();
                        form.setTanggal(tanggal);
                        form.setJam(LocalTime.of(9, 0));
                        hasil.add(reservationService.buat(layananSatu.getId(), form, pelanggan));
                    } catch (Throwable t) {
                        hasil.add(t);
                    } finally {
                        selesai.countDown();
                    }
                });
            }
        }
        mulai.countDown();
        assertTrue(selesai.await(120, TimeUnit.SECONDS), "semua thread harus selesai");
        kumpulan.shutdown();

        List<Reservation> berhasil = hasil.stream().filter(Reservation.class::isInstance)
                .map(Reservation.class::cast).toList();
        List<Throwable> galat = hasil.stream().filter(Throwable.class::isInstance)
                .map(Throwable.class::cast).toList();
        List<Throwable> bocor = galat.stream().filter(t ->
                !(t instanceof IllegalArgumentException && "Slot ini sudah penuh.".equals(t.getMessage()))).toList();

        assertEquals(KAPASITAS * 2, berhasil.size(), "sukses harus sama dengan kapasitas kedua bisnis");
        assertTrue(bocor.isEmpty(), () -> "tidak boleh ada galat bocor: "
                + bocor.stream().map(t -> t.getClass().getSimpleName() + ": " + t.getMessage()).toList());

        Set<String> kode = berhasil.stream().map(Reservation::getReservationCode).collect(Collectors.toSet());
        assertEquals(berhasil.size(), kode.size(), "kode reservasi harus unik");

        for (Business bisnis : List.of(bisnisA, bisnisB)) {
            List<Queue> antrean = queueRepository.antreanBisnis(bisnis, tanggal);
            assertEquals(KAPASITAS, antrean.size(), "antrean per bisnis = kapasitas bisnis");
            List<Integer> nomor = antrean.stream().map(Queue::getQueueNumber).toList();
            for (int i = 0; i < nomor.size(); i++) {
                assertEquals(i + 1, nomor.get(i), "nomor antrean unik dan berurutan per bisnis");
            }
        }
    }

    private Business buatBisnis(User owner, Category kategori, String nama, LocalDate tanggal) {
        Business bisnis = new Business();
        bisnis.setOwner(owner);
        bisnis.setName(nama);
        bisnis.setCategory(kategori);
        bisnis.setAddress("Alamat uji");
        bisnis.setStatus(BusinessStatus.ACTIVE);
        bisnis.setMaxCapacity(KAPASITAS);
        bisnis.setActiveStaffCount(1);
        bisnis = businessRepository.save(bisnis);

        BusinessSchedule jadwal = new BusinessSchedule();
        jadwal.setBusiness(bisnis);
        jadwal.setDayOfWeek(BusinessSchedule.namaHari(tanggal));
        jadwal.setOpenTime(LocalTime.of(9, 0));
        jadwal.setCloseTime(LocalTime.of(17, 0));
        jadwal.setClosed(false);
        scheduleRepository.save(jadwal);
        return bisnis;
    }

    private Service buatLayanan(Business bisnis, String nama) {
        Service layanan = new Service();
        layanan.setBusiness(bisnis);
        layanan.setName(nama);
        layanan.setPrice(new BigDecimal("10000"));
        layanan.setEstimatedDurationMinutes(30);
        layanan.setActive(true);
        return serviceRepository.save(layanan);
    }
}
