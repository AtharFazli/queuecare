package com.queuecare.config;

import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Category;
import com.queuecare.entity.Service;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.enums.Role;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.repository.CategoryRepository;
import com.queuecare.repository.ServiceRepository;
import com.queuecare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Data dummy untuk demo. Hanya jalan bila app.seed-dummy=true dan belum ada bisnis,
 * sehingga aman dijalankan berulang. Semua data dummy bertanda "(Data dummy)".
 */
@Component
@Order(2)
public class DummyDataSeeder implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-dummy:false}")
    private boolean seedDummy;

    public DummyDataSeeder(BusinessRepository businessRepository,
                            ServiceRepository serviceRepository,
                            BusinessScheduleRepository scheduleRepository,
                            CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedDummy) {
            return;
        }
        User owner1 = userRepository.findByEmail("owner@queuecare.id").orElse(null);
        User owner2 = userRepository.findByEmail("owner2@queuecare.id").orElse(null);
        if (owner1 == null || owner2 == null || businessRepository.count() > 0) {
            return;
        }

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
