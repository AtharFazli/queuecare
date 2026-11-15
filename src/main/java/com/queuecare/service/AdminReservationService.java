package com.queuecare.service;

import com.queuecare.dto.BarisReservasi;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.enums.QueueStatus;
import com.queuecare.enums.ReservationStatus;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Daftar reservasi Admin dengan filter dan paginasi 20 per halaman (R12). */
@Service
public class AdminReservationService {

    private static final int UKURAN_HALAMAN = 20;

    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;

    public AdminReservationService(ReservationRepository reservationRepository,
                                   QueueRepository queueRepository) {
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
    }

    public Page<BarisReservasi> daftar(LocalDate dari, LocalDate sampai, Long bisnisId,
                                       ReservationStatus status, int halaman) {
        PageRequest page = PageRequest.of(Math.max(halaman - 1, 0), UKURAN_HALAMAN,
                Sort.by("id").descending());
        List<Reservation> isi = reservationRepository.cariUntukAdmin(dari, sampai, bisnisId, status, page);
        long total = reservationRepository.hitungUntukAdmin(dari, sampai, bisnisId, status);

        Map<Long, QueueStatus> statusAntrean = statusAntrean(isi);
        List<BarisReservasi> baris = isi.stream()
                .map(r -> new BarisReservasi(
                        r.getReservationCode(),
                        r.getCustomer().getName(),
                        r.getBusiness().getName(),
                        r.getService().getName(),
                        r.getReservationDate(),
                        r.getReservationTime(),
                        r.getStatus().name(),
                        statusAntrean.get(r.getId()) == null ? "-" : statusAntrean.get(r.getId()).name()))
                .collect(Collectors.toList());
        return new PageImpl<>(baris, page, total);
    }

    /** Status antrean diambil sekali query untuk semua baris di halaman ini. */
    private Map<Long, QueueStatus> statusAntrean(List<Reservation> isi) {
        if (isi.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = isi.stream().map(Reservation::getId).toList();
        return queueRepository.findByReservationIdIn(ids).stream()
                .collect(Collectors.toMap(q -> q.getReservation().getId(), Queue::getStatus));
    }
}
