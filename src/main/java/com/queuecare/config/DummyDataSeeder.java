package com.queuecare.config;

import com.queuecare.dto.ReservationForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Category;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.Service;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.enums.QueueStatus;
import com.queuecare.enums.Role;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.repository.CategoryRepository;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import com.queuecare.repository.ServiceRepository;
import com.queuecare.repository.UserRepository;
import com.queuecare.service.QueueService;
import com.queuecare.service.ReservationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Data dummy untuk demo. Bisnis dibuat bila belum ada, antrean dibuat bila tabel
 * reservasi masih kosong, sehingga aman dijalankan berulang. Semua data dummy
 * bertanda "(Data dummy)".
 */
@Component
@Order(2)
public class DummyDataSeeder implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;
    private final ReservationService reservationService;
    private final QueueService queueService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-dummy:false}")
    private boolean seedDummy;

    public DummyDataSeeder(BusinessRepository businessRepository,
                            ServiceRepository serviceRepository,
                            BusinessScheduleRepository scheduleRepository,
                            CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            ReservationRepository reservationRepository,
                            QueueRepository queueRepository,
                            ReservationService reservationService,
                            QueueService queueService,
                            PasswordEncoder passwordEncoder) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.reservationService = reservationService;
        this.queueService = queueService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedDummy) {
            return;
        }
        User owner1 = userRepository.findByEmail("owner@queuecare.id").orElse(null);
        User owner2 = userRepository.findByEmail("owner2@queuecare.id").orElse(null);
        if (owner1 == null || owner2 == null) {
            return;
        }
        if (businessRepository.count() == 0) {
            seedBisnis(owner1, owner2);
        }
        if (reservationRepository.count() == 0) {
            seedAntrean();
        }
    }

    // ===== bisnis, layanan, jadwal =====

    private void seedBisnis(User owner1, User owner2) {
        Category barbershop = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(c -> c.getName().equals("Barbershop")).findFirst().orElseThrow();
        Category salon = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(c -> c.getName().equals("Salon")).findFirst().orElseThrow();
        Category servis = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(c -> c.getName().equals("Servis Laptop")).findFirst().orElseThrow();
        Category klinik = categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(c -> c.getName().equals("Klinik Kecantikan")).findFirst().orElseThrow();

        Business b1 = bisnis(owner1, barbershop, "Barbershop Senja", BusinessStatus.ACTIVE,
                "Jl. Kemangi", "0812-0000-0001", 6, 2,
                "Potong rambut pria dan anak, cuci rambut, serta treatment rambut. (Data dummy)");
        Business b2 = bisnis(owner1, barbershop, "Pangkas Rambut Griya", BusinessStatus.INACTIVE,
                "Jl. Melati", "0812-0000-0002", 4, 1,
                "Layanan cukur klasik dengan harga bersahabat. (Data dummy)");
        Business b3 = bisnis(owner1, salon, "Salon Lestari", BusinessStatus.ACTIVE,
                "Jl. Kenanga", "0812-0000-0003", 8, 3,
                "Salon lengkap: gunting, catok, coloring, dan perawatan rambut. (Data dummy)");
        Business b4 = bisnis(owner2, salon, "Salon Aurora", BusinessStatus.ACTIVE,
                "Jl. Anggrek", "0812-0000-0004", 10, 4,
                "Perawatan rambut wanita dan tata rias pengantin. (Data dummy)");
        Business b5 = bisnis(owner2, servis, "Servis Laptop Jaya", BusinessStatus.ACTIVE,
                "Jl. Cempaka", "0812-0000-0005", 5, 2,
                "Perbaikan laptop, ganti SSD, bersih-bersih keyboard, dan instal ulang. (Data dummy)");
        Business b6 = bisnis(owner2, klinik, "Klinik Cantik Sehat", BusinessStatus.PENDING,
                "Jl. Flamboyan", "0812-0000-0006", 7, 3,
                "Konsultasi kulit dan perawatan wajah dasar. (Data dummy)");

        layani(b1, "Cukur rambut", "Potong rambut sesuai keinginan.", "45000", 30, true);
        layani(b1, "Cukur + cuci", "Potong rambut dan cuci rambut.", "65000", 45, true);
        layani(b1, "Cukur anak", "Potong rambut untuk anak.", "35000", 25, false);
        layani(b2, "Cukur klasik", "Potong rambut model klasik.", "40000", 30, true);
        layani(b2, "Cukur + cukur jenggot", "Potong rambut dan rapikan jenggot.", "55000", 40, true);
        layani(b3, "Gunting rambut", "Gunting dan styling dasar.", "70000", 45, true);
        layani(b3, "Catok", "Catok lurus atau keriting.", "90000", 60, true);
        layani(b3, "Coloring", "Pewarnaan rambut satu warna.", "250000", 120, true);
        layani(b3, "Perawatan rambut", "Hair mask dan serum.", "120000", 50, true);
        layani(b4, "Gunting + styling", "Gunting dan styling acara.", "110000", 60, true);
        layani(b4, "Tata rias pengantin", "Tata rias lengkap pengantin.", "850000", 180, true);
        layani(b4, "Perawatan facial", "Facial dasar 60 menit.", "150000", 60, false);
        layani(b5, "Perbaikan hardware", "Diagnosis dan perbaikan komponen.", "150000", 90, true);
        layani(b5, "Ganti SSD", "Pemasangan SSD dan pindah data.", "200000", 60, true);
        layani(b5, "Instal ulang", "Instal ulang sistem operasi.", "180000", 75, true);
        layani(b5, "Bersih keyboard", "Bongkar dan bersihkan keyboard.", "90000", 30, true);
        layani(b6, "Konsultasi kulit", "Konsultasi kondisi kulit wajah.", "120000", 30, true);
        layani(b6, "Facial dasar", "Facial pembersihan 60 menit.", "220000", 60, true);
        layani(b6, "Peeling ringan", "Peeling untuk sel kulit mati.", "300000", 45, true);

        for (Business business : List.of(b1, b2, b3, b4, b5, b6)) {
            buatJadwal(business);
        }
    }

    // ===== antrean dummy =====

    /** Dua Customer dummy, lalu delapan antrean dua bisnis ACTIVE untuk hari ini dan besok. */
    private void seedAntrean() {
        User dummy1 = pelanggan("Dummy Satu (Data dummy)", "dummy1@queuecare.id");
        User dummy2 = pelanggan("Dummy Dua (Data dummy)", "dummy2@queuecare.id");

        Business senja = cariBisnis("Barbershop Senja");
        Business lestari = cariBisnis("Salon Lestari");
        Service cukur = cariLayanan(senja, "Cukur rambut");
        Service gunting = cariLayanan(lestari, "Gunting rambut");
        if (cukur == null || gunting == null) {
            return;
        }

        LocalDate hariIni = LocalDate.now();
        LocalDate besok = hariIni.plusDays(1);
        LocalTime jamBesok = LocalTime.of(10, 0);

        // Gelombang pertama: hari ini kalau masih ada slot, kalau libur pakai hari buka berikutnya.
        LocalDate hariA = hariIni;
        LocalTime jamA = slotBerikut(senja, hariIni);
        if (jamA == null) {
            hariA = hariBuka(senja, besok);
            jamA = hariA == null ? null : jamBesok;
        }
        if (hariA != null && jamA != null) {
            antrean(senja, cukur, dummy1, hariA, jamA, QueueStatus.WAITING);
            antrean(senja, cukur, dummy2, hariA, jamA, QueueStatus.CALLED);
            antrean(senja, cukur, dummy1, hariA, jamA, QueueStatus.IN_SERVICE);
            antrean(senja, cukur, dummy2, hariA, jamA, QueueStatus.COMPLETED);
            antrean(senja, cukur, dummy1, hariA, jamA, QueueStatus.NO_SHOW);
            antrean(senja, cukur, dummy2, hariA, jamA, QueueStatus.CANCELLED);
            // Satu antrean WAITING di belakang, supaya estimasi SmartWait punya angka nyata.
            antrean(senja, cukur, dummy2, hariA, jamA, QueueStatus.WAITING);
        }

        // Gelombang kedua: hari berikutnya setelah gelombang pertama.
        LocalDate mulaiB = hariA == null ? besok : hariA.plusDays(1);
        LocalDate hariB = jamValid(senja, mulaiB, jamBesok)
                ? mulaiB : hariBuka(senja, mulaiB.plusDays(1));
        if (hariB != null) {
            antrean(senja, cukur, dummy1, hariB, jamBesok, QueueStatus.WAITING);
            antrean(lestari, gunting, dummy2, hariB, jamBesok, QueueStatus.COMPLETED);
        }
    }

    /** Satu antrean dibuat lewat service reservasi, lalu statusnya dibawa ke target. */
    private void antrean(Business business, Service service, User customer,
                         LocalDate tanggal, LocalTime jam, QueueStatus target) {
        ReservationForm form = new ReservationForm();
        form.setTanggal(tanggal);
        form.setJam(jam);
        form.setCatatan("Antrean (Data dummy)");
        Reservation reservation = reservationService.buat(service.getId(), form, customer);
        Queue queue = queueRepository.findByReservation(reservation).orElseThrow();
        Long id = queue.getId();
        if (target == QueueStatus.CALLED || target == QueueStatus.IN_SERVICE
                || target == QueueStatus.NO_SHOW || target == QueueStatus.COMPLETED) {
            queueService.panggil(id, business);
        }
        // NO_SHOW hanya bisa dari CALLED, jadi "Mulai" tidak dipanggil untuk jalur ini.
        if (target == QueueStatus.IN_SERVICE || target == QueueStatus.COMPLETED) {
            queueService.mulai(id, business);
        }
        if (target == QueueStatus.NO_SHOW) {
            queueService.tidakHadir(id, business);
        }
        if (target == QueueStatus.COMPLETED) {
            queueService.selesai(id, business);
        }
        if (target == QueueStatus.CANCELLED) {
            queueService.batalkan(id, business);
        }
    }

    private User pelanggan(String nama, String email) {
        return userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(new User(nama, email, passwordEncoder.encode("customer123"), Role.CUSTOMER)));
    }

    private Business cariBisnis(String nama) {
        return businessRepository.findAll().stream()
                .filter(b -> b.getName().equals(nama)).findFirst().orElse(null);
    }

    private Service cariLayanan(Business business, String nama) {
        if (business == null) {
            return null;
        }
        return serviceRepository.findByBusinessAndActiveTrueOrderByIdAsc(business).stream()
                .filter(s -> s.getName().equals(nama)).findFirst().orElse(null);
    }

    /** Slot 30 menit berikutnya untuk hari ini, atau null kalau hari ini sudah habis. */
    private LocalTime slotBerikut(Business business, LocalDate tanggal) {
        BusinessSchedule jadwal = scheduleRepository
                .findByBusinessAndDayOfWeek(business, BusinessSchedule.namaHari(tanggal)).orElse(null);
        if (jadwal == null || jadwal.isClosed()
                || jadwal.getOpenTime() == null || jadwal.getCloseTime() == null) {
            return null;
        }
        LocalTime sekarang = LocalTime.now();
        for (LocalTime t = jadwal.getOpenTime();
             !t.plusMinutes(30).isAfter(jadwal.getCloseTime());
             t = t.plusMinutes(30)) {
            if (tanggal.equals(LocalDate.now()) && !t.isAfter(sekarang)) {
                continue;
            }
            return t;
        }
        return null;
    }

    private boolean jamValid(Business business, LocalDate tanggal, LocalTime jam) {
        BusinessSchedule jadwal = scheduleRepository
                .findByBusinessAndDayOfWeek(business, BusinessSchedule.namaHari(tanggal)).orElse(null);
        return jadwal != null && !jadwal.isClosed()
                && jadwal.getOpenTime() != null && jadwal.getCloseTime() != null
                && !jam.isBefore(jadwal.getOpenTime())
                && !jam.plusMinutes(30).isAfter(jadwal.getCloseTime());
    }

    /** Hari buka pertama pada atau setelah `mulai` yang bisa dipakai pada jam 10:00. */
    private LocalDate hariBuka(Business business, LocalDate mulai) {
        for (int i = 0; i < 7; i++) {
            LocalDate t = mulai.plusDays(i);
            if (jamValid(business, t, LocalTime.of(10, 0))) {
                return t;
            }
        }
        return null;
    }

    private Business bisnis(User owner, Category category, String nama, BusinessStatus status,
                            String alamat, String telepon, int kapasitas, int petugas,
                            String deskripsi) {
        Business business = new Business();
        business.setOwner(owner);
        business.setName(nama);
        business.setCategory(category);
        business.setDescription(deskripsi);
        business.setAddress(alamat);
        business.setPhone(telepon);
        business.setStatus(status);
        business.setMaxCapacity(kapasitas);
        business.setActiveStaffCount(petugas);
        return businessRepository.save(business);
    }

    private void layani(Business business, String nama, String deskripsi, String harga,
                        int durasi, boolean aktif) {
        Service service = new Service();
        service.setBusiness(business);
        service.setName(nama);
        service.setDescription(deskripsi);
        service.setPrice(new BigDecimal(harga));
        service.setEstimatedDurationMinutes(durasi);
        service.setActive(aktif);
        serviceRepository.save(service);
    }

    /** Jadwal Senin sampai Sabtu, Minggu libur. */
    private void buatJadwal(Business business) {
        String[] hari = {"SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU"};
        for (String h : hari) {
            jadwal(business, h, LocalTime.of(9, 0), LocalTime.of(17, 0), false);
        }
        jadwal(business, "MINGGU", null, null, true);
    }

    private void jadwal(Business business, String hari, LocalTime buka, LocalTime tutup, boolean libur) {
        BusinessSchedule schedule = new BusinessSchedule();
        schedule.setBusiness(business);
        schedule.setDayOfWeek(hari);
        schedule.setOpenTime(buka);
        schedule.setCloseTime(tutup);
        schedule.setClosed(libur);
        scheduleRepository.save(schedule);
    }
}
