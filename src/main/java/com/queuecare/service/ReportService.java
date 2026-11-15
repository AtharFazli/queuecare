package com.queuecare.service;

import com.queuecare.dto.AdminReport;
import com.queuecare.dto.AdminStats;
import com.queuecare.dto.OwnerReport;
import com.queuecare.dto.OwnerStats;
import com.queuecare.entity.Business;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.enums.QueueStatus;
import com.queuecare.enums.Role;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import com.queuecare.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Angka dashboard dan laporan. Semua angka berasal dari query agregat di repository,
 * bukan dari menghitung data yang dimuat di Java (R11, R12).
 */
@Service
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public ReportService(ReservationRepository reservationRepository,
                         QueueRepository queueRepository,
                         UserRepository userRepository,
                         BusinessRepository businessRepository) {
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    // ===== Owner =====

    /**
     * Dashboard Owner untuk bisnis aktif. "Reservasi hari ini" dan "selesai hari ini"
     * memakai reservation_date = hari ini; antrean menunggu dan sedang dilayani
     * dihitung seluruh antrean bisnis tanpa filter tanggal.
     */
    public OwnerStats statistikOwner(Business business) {
        LocalDate hariIni = LocalDate.now();
        return new OwnerStats(
                reservationRepository.countByBusinessAndReservationDate(business, hariIni),
                queueRepository.countByBusinessAndStatus(business, QueueStatus.WAITING),
                queueRepository.countByBusinessAndStatus(business, QueueStatus.CALLED)
                        + queueRepository.countByBusinessAndStatus(business, QueueStatus.IN_SERVICE),
                queueRepository.hitungPerStatus(business, QueueStatus.COMPLETED, hariIni, hariIni));
    }

    /** Laporan Owner dalam rentang reservation_date. */
    public OwnerReport laporanOwner(Business business, LocalDate dari, LocalDate sampai) {
        return new OwnerReport(
                dari,
                sampai,
                reservationRepository.countByBusinessAndReservationDateBetween(business, dari, sampai),
                reservationRepository.countByBusinessAndReservationDate(business, LocalDate.now()),
                queueRepository.hitungPerStatus(business, QueueStatus.COMPLETED, dari, sampai),
                queueRepository.hitungPerStatus(business, QueueStatus.CANCELLED, dari, sampai),
                queueRepository.hitungPerStatus(business, QueueStatus.NO_SHOW, dari, sampai),
                reservationRepository.hitungPelangganUnik(business, dari, sampai),
                reservationRepository.layananTerpopuler(business, dari, sampai, PageRequest.of(0, 5)));
    }

    // ===== Admin =====

    public AdminStats statistikAdmin() {
        return new AdminStats(
                userRepository.count(),
                userRepository.countByRole(Role.CUSTOMER),
                userRepository.countByRole(Role.OWNER),
                businessRepository.count(),
                businessRepository.countByStatus(BusinessStatus.ACTIVE),
                businessRepository.countByStatus(BusinessStatus.PENDING),
                businessRepository.countByStatus(BusinessStatus.INACTIVE),
                reservationRepository.count(),
                reservationRepository.aktivitasTerbaru(PageRequest.of(0, 10)));
    }

    public AdminReport laporanAdmin(LocalDate dari, LocalDate sampai) {
        return new AdminReport(
                dari,
                sampai,
                businessRepository.count(),
                userRepository.count(),
                reservationRepository.hitungUntukAdmin(dari, sampai, null, null),
                reservationRepository.reservasiPerHari(dari, sampai),
                queueRepository.ringkasanStatusAntrean());
    }
}
