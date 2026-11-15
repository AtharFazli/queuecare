package com.queuecare.service;

import com.queuecare.dto.HasilEstimasi;
import com.queuecare.dto.QueueStatusDto;
import com.queuecare.entity.Business;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.User;
import com.queuecare.enums.QueueStatus;
import com.queuecare.enums.ReservationStatus;
import com.queuecare.exception.TidakDitemukanException;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mesin status antrean. Tabel transisi, peran yang boleh, dan penguncian baris
 * dipakai supaya dua klik bersamaan tidak membuat status ganda atau loncat (R7).
 */
@Service
public class QueueService {

    /** Status yang masih dihitung sebagai "masih antre" pada posisi. */
    private static final Set<QueueStatus> STATUS_AKTIF = SmartWaitService.STATUS_DI_HITUNG;

    /** Status yang sudah tidak bisa diubah lagi. */
    private static final Set<QueueStatus> STATUS_FINAL =
            EnumSet.of(QueueStatus.COMPLETED, QueueStatus.CANCELLED, QueueStatus.NO_SHOW);

    /** Satu-satunya jalur status yang diizinkan (R7). */
    private static final Map<QueueStatus, Set<QueueStatus>> TRANSISI = Map.of(
            QueueStatus.WAITING, EnumSet.of(QueueStatus.CALLED, QueueStatus.CANCELLED),
            QueueStatus.CALLED, EnumSet.of(QueueStatus.IN_SERVICE, QueueStatus.NO_SHOW, QueueStatus.CANCELLED),
            QueueStatus.IN_SERVICE, EnumSet.of(QueueStatus.COMPLETED));

    private static final Map<QueueStatus, String> KETERANGAN = Map.of(
            QueueStatus.WAITING, "Menunggu dipanggil.",
            QueueStatus.CALLED, "Sudah dipanggil, siap dilayani.",
            QueueStatus.IN_SERVICE, "Sedang dilayani.",
            QueueStatus.COMPLETED, "Selesai dilayani.",
            QueueStatus.CANCELLED, "Reservasi dibatalkan.",
            QueueStatus.NO_SHOW, "Tidak hadir saat dipanggil.");

    private final QueueRepository queueRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionTemplate transactionTemplate;
    private final SmartWaitService smartWaitService;

    public QueueService(QueueRepository queueRepository,
                        ReservationRepository reservationRepository,
                        TransactionTemplate transactionTemplate,
                        SmartWaitService smartWaitService) {
        this.queueRepository = queueRepository;
        this.reservationRepository = reservationRepository;
        this.transactionTemplate = transactionTemplate;
        this.smartWaitService = smartWaitService;
    }

    // ===== pembacaan =====

    public List<Queue> antreanBisnis(Business business, LocalDate tanggal) {
        return queueRepository.antreanBisnis(business, tanggal);
    }

    /** Antrean untuk halaman Owner. Bukan milik bisnisnya berarti 403 (KNF-05). */
    public Queue milikBisnis(Long id, Business business) {
        Queue queue = queueRepository.findByIdLengkap(id)
                .orElseThrow(() -> new TidakDitemukanException("Antrean tidak ditemukan."));
        if (!queue.getBusiness().getId().equals(business.getId())) {
            throw new AccessDeniedException("Kamu tidak punya hak akses ke antrean ini.");
        }
        return queue;
    }

    /** Antrean untuk halaman Customer. Bukan miliknya berarti 404 (KNF-05). */
    public Queue milikCustomer(Long id, User customer) {
        Queue queue = queueRepository.findByIdLengkap(id)
                .orElseThrow(() -> new TidakDitemukanException("Antrean tidak ditemukan."));
        Reservation reservation = queue.getReservation();
        if (reservation == null || !reservation.getCustomer().getId().equals(customer.getId())) {
            throw new TidakDitemukanException("Antrean tidak ditemukan.");
        }
        return queue;
    }

    /** Jumlah pelanggan yang masih di depan antrean ini. */
    public int orangDepan(Queue queue) {
        return (int) queueRepository.countPosisi(queue.getBusiness(), queue.getQueueDate(),
                queue.getQueueNumber(), STATUS_AKTIF);
    }

    /** Posisi antrean: berapa yang masih aktif di depannya, lalu ditambah satu. */
    public int posisi(Queue queue) {
        return orangDepan(queue) + 1;
    }

    public String keterangan(QueueStatus status) {
        return KETERANGAN.get(status);
    }

    public boolean sudahFinal(Queue queue) {
        return STATUS_FINAL.contains(queue.getStatus());
    }

    public QueueStatusDto statusDto(Queue queue) {
        HasilEstimasi estimasi = smartWaitService.untuk(queue);
        return new QueueStatusDto(queue.getStatus().name(), posisi(queue), keterangan(queue.getStatus()),
                estimasi.menit(), estimasi.pesan(), orangDepan(queue));
    }

    // ===== perubahan status =====

    public Queue panggil(Long id, Business business) {
        return ubah(id, business, QueueStatus.CALLED, "Panggil");
    }

    public Queue mulai(Long id, Business business) {
        return ubah(id, business, QueueStatus.IN_SERVICE, "Mulai");
    }

    public Queue selesai(Long id, Business business) {
        return ubah(id, business, QueueStatus.COMPLETED, "Selesai");
    }

    public Queue tidakHadir(Long id, Business business) {
        return ubah(id, business, QueueStatus.NO_SHOW, "Tidak hadir");
    }

    /** Batalkan antrean milik Owner, dengan status reservasi ikut berubah. */
    public Queue batalkan(Long id, Business business) {
        return ubah(id, business, QueueStatus.CANCELLED, "Batalkan");
    }

    /**
     * Semua perubahan status lewat satu pintu: kunci baris antrean, cek kepemilikan,
     * cek transisi yang diizinkan, isi waktu, dan selaraskan status reservasi.
     */
    private Queue ubah(Long id, Business business, QueueStatus target, String namaAksi) {
        return transactionTemplate.execute(status -> {
            Queue queue = queueRepository.lockById(id)
                    .orElseThrow(() -> new TidakDitemukanException("Antrean tidak ditemukan."));
            if (!queue.getBusiness().getId().equals(business.getId())) {
                throw new AccessDeniedException("Kamu tidak punya hak akses ke antrean ini.");
            }
            QueueStatus asal = queue.getStatus();
            Set<QueueStatus> boleh = TRANSISI.getOrDefault(asal, Set.of());
            if (!boleh.contains(target)) {
                throw new IllegalStateException(
                        "Antrean berstatus " + asal + " tidak bisa diubah menjadi " + target
                                + ". Status " + asal + " hanya bisa menjadi "
                                + (boleh.isEmpty() ? "tidak bisa diubah lagi" : boleh) + ".");
            }
            queue.setStatus(target);
            if (target == QueueStatus.CALLED) {
                queue.setCalledAt(LocalDateTime.now());
            }
            if (target == QueueStatus.IN_SERVICE) {
                queue.setStartedAt(LocalDateTime.now());
            }
            if (target == QueueStatus.COMPLETED) {
                queue.setCompletedAt(LocalDateTime.now());
            }
            queueRepository.save(queue);

            Reservation reservation = queue.getReservation();
            if (reservation != null) {
                if (target == QueueStatus.COMPLETED) {
                    reservation.setStatus(ReservationStatus.COMPLETED);
                } else if (target == QueueStatus.CANCELLED || target == QueueStatus.NO_SHOW) {
                    reservation.setStatus(ReservationStatus.CANCELLED);
                }
                reservationRepository.save(reservation);
            }
            return queue;
        });
    }
}
