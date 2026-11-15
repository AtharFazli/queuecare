package com.queuecare.service;

import com.queuecare.dto.BusinessForm;
import com.queuecare.dto.CapacityForm;
import com.queuecare.dto.ScheduleForm;
import com.queuecare.dto.ServiceForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Category;
import com.queuecare.entity.Service;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.repository.CategoryRepository;
import com.queuecare.repository.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Annotation Spring dipakai lengkap karena entity layanan juga bernama Service.
@org.springframework.stereotype.Service
@Transactional
public class BusinessService {

    private static final List<String> HARI = BusinessSchedule.HARI;

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final UserContext userContext;

    public BusinessService(BusinessRepository businessRepository,
                           ServiceRepository serviceRepository,
                           BusinessScheduleRepository scheduleRepository,
                           CategoryRepository categoryRepository,
                           UserContext userContext) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.categoryRepository = categoryRepository;
        this.userContext = userContext;
    }

    // ===== bisnis (Owner) =====

    public Optional<Business> bisnisSaya() {
        return businessRepository.findByOwner(userContext.currentUser());
    }

    /** Owner cukup punya satu bisnis, dibuat dengan status PENDING (K3). */
    public Business buatBisnis(BusinessForm form) {
        User owner = userContext.currentUser();
        if (businessRepository.findByOwner(owner).isPresent()) {
            throw new IllegalStateException("Kamu sudah punya bisnis. Ubah data bisnis yang ada.");
        }
        Business business = new Business();
        business.setOwner(owner);
        business.setStatus(BusinessStatus.PENDING);
        isiBisnis(business, form);
        return businessRepository.save(business);
    }

    public Business ubahBisnis(Long id, BusinessForm form) {
        Business business = bisnisSaya(id);
        isiBisnis(business, form);
        return businessRepository.save(business);
    }

    private void isiBisnis(Business business, BusinessForm form) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan."));
        business.setName(form.getName());
        business.setCategory(category);
        business.setDescription(form.getDescription());
        business.setAddress(form.getAddress());
        business.setPhone(form.getPhone());
        business.setMaxCapacity(form.getMaxCapacity());
        business.setActiveStaffCount(form.getActiveStaffCount());
    }

    /** Membaca bisnis milik sendiri, menolak kalau bukan punya (KNF-05). */
    public Business bisnisSaya(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bisnis tidak ditemukan."));
        userContext.pastikanPemilik(business.getOwner());
        return business;
    }

    // ===== layanan =====

    public List<Service> layananSaya() {
        return bisnisSaya().map(serviceRepository::findByBusinessOrderByIdAsc).orElse(List.of());
    }

    public Service buatLayanan(ServiceForm form) {
        Service service = new Service();
        service.setBusiness(bisnisAtauGagal());
        isiLayanan(service, form);
        return serviceRepository.save(service);
    }

    public Service ubahLayanan(Long id, ServiceForm form) {
        Service service = layananSaya(id);
        isiLayanan(service, form);
        return serviceRepository.save(service);
    }

    private void isiLayanan(Service service, ServiceForm form) {
        service.setName(form.getName());
        service.setDescription(form.getDescription());
        service.setPrice(form.getPrice());
        service.setEstimatedDurationMinutes(form.getEstimatedDurationMinutes());
        service.setActive(form.isActive());
    }

    public Service layananSaya(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Layanan tidak ditemukan."));
        userContext.pastikanPemilik(service.getBusiness().getOwner());
        return service;
    }

    /** Layanan dinonaktifkan, bukan dihapus. */
    public void ubahStatusLayanan(Long id, boolean aktif) {
        Service service = layananSaya(id);
        service.setActive(aktif);
        serviceRepository.save(service);
    }

    // ===== jadwal operasional =====

    public List<BusinessSchedule> jadwalSaya() {
        return bisnisSaya().map(scheduleRepository::findByBusinessOrderByIdAsc).orElse(List.of());
    }

    public BusinessSchedule simpanJadwal(Long id, ScheduleForm form) {
        if (!HARI.contains(form.getDayOfWeek())) {
            throw new IllegalArgumentException("Hari tidak valid.");
        }
        if (!form.isClosed() && !form.getOpenTime().isBefore(form.getCloseTime())) {
            throw new IllegalArgumentException("Jam buka harus lebih awal dari jam tutup.");
        }
        BusinessSchedule schedule;
        if (id == null) {
            schedule = new BusinessSchedule();
            schedule.setBusiness(bisnisAtauGagal());
        } else {
            schedule = jadwalSaya(id);
        }
        schedule.setDayOfWeek(form.getDayOfWeek());
        schedule.setOpenTime(form.getOpenTime());
        schedule.setCloseTime(form.getCloseTime());
        schedule.setClosed(form.isClosed());
        return scheduleRepository.save(schedule);
    }

    public BusinessSchedule jadwalSaya(Long id) {
        BusinessSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jadwal tidak ditemukan."));
        userContext.pastikanPemilik(schedule.getBusiness().getOwner());
        return schedule;
    }

    public void hapusJadwal(Long id) {
        scheduleRepository.delete(jadwalSaya(id));
    }

    // ===== kapasitas dan petugas =====

    public Business ubahKapasitas(CapacityForm form) {
        Business business = bisnisAtauGagal();
        business.setMaxCapacity(form.getMaxCapacity());
        business.setActiveStaffCount(form.getActiveStaffCount());
        return businessRepository.save(business);
    }

    private Business bisnisAtauGagal() {
        return bisnisSaya().orElseThrow(() ->
                new IllegalStateException("Buat bisnis dulu sebelum mengisi data ini."));
    }

    // ===== admin =====

    public List<Business> semuaBisnis() {
        return businessRepository.findAllByOrderByIdAsc();
    }

    /** Admin mengubah status bisnis, bukan pemiliknya (R12). */
    public void ubahStatusBisnis(Long id, BusinessStatus status) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bisnis tidak ditemukan."));
        business.setStatus(status);
        businessRepository.save(business);
    }
}
