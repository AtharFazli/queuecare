package com.queuecare.service;

import com.queuecare.dto.ReservationForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Category;
import com.queuecare.entity.Queue;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Uji atomicity Fase 4: kegagalan saat membuat antrean tidak boleh meninggalkan reservasi,
 * karena reservasi dan antrean dibuat dalam satu transaksi (R13).
 */
@SpringBootTest
class ReservationAtomicTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private BusinessScheduleRepository scheduleRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @MockitoSpyBean
    private QueueRepository queueRepository;

    @Test
    void kegagalanSaatAntreanTidakMeninggalkanReservasi() {
        User owner = userRepository.findByEmail("owner@queuecare.id").orElseThrow();
        User customer = userRepository.save(
                new User("Pelanggan Uji", "uji-atomik@queuecare.id", "hash-uji", Role.CUSTOMER));
        // Sesi login untuk UserContext.currentUser().
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                customer.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));
        Category category = categoryRepository.findAllByOrderByNameAsc().get(0);

        Business business = new Business();
        business.setOwner(owner);
        business.setName("Bisnis Uji Atomik");
        business.setCategory(category);
        business.setAddress("Alamat uji");
        business.setStatus(BusinessStatus.ACTIVE);
        business.setMaxCapacity(2);
        business.setActiveStaffCount(1);
        business = businessRepository.save(business);

        Service service = new Service();
        service.setBusiness(business);
        service.setName("Layanan Uji");
        service.setPrice(new BigDecimal("10000"));
        service.setEstimatedDurationMinutes(30);
        service.setActive(true);
        service = serviceRepository.save(service);
        final Service layananTersimpan = service;

        LocalDate tanggal = LocalDate.now().plusDays(1);
        BusinessSchedule jadwal = new BusinessSchedule();
        jadwal.setBusiness(business);
        jadwal.setDayOfWeek(BusinessSchedule.namaHari(tanggal));
        jadwal.setOpenTime(LocalTime.of(9, 0));
        jadwal.setCloseTime(LocalTime.of(17, 0));
        jadwal.setClosed(false);
        scheduleRepository.save(jadwal);

        ReservationForm form = new ReservationForm();
        form.setTanggal(tanggal);
        form.setJam(LocalTime.of(9, 0));

        long jumlahSebelum = reservationRepository.count();

        // Simulasi kegagalan di tengah proses: penyimpanan antrean gagal.
        doThrow(new RuntimeException("simulasi gagal menyimpan antrean"))
                .when(queueRepository).save(any(Queue.class));

        assertThrows(RuntimeException.class,
                () -> reservationService.buat(layananTersimpan.getId(), form));

        assertEquals(jumlahSebelum, reservationRepository.count(),
                "reservasi harus ikut dibatalkan karena satu transaksi dengan antrean");

        // Memastikan alur benar-benar sampai menyimpan antrean, jadi reservasi sempat dibuat.
        verify(queueRepository, atLeastOnce()).save(any(Queue.class));
    }
}
