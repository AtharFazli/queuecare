package com.queuecare.service;

import com.queuecare.dto.HasilEstimasi;
import com.queuecare.entity.Business;
import com.queuecare.entity.Queue;
import com.queuecare.enums.QueueStatus;
import com.queuecare.repository.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * SmartWait: perkiraan waktu tunggu dari aturan, bukan AI (R9).
 * Rumus: pembulatan ke atas dari total durasi layanan pelanggan di depan
 * dibagi jumlah petugas aktif bisnis.
 */
@Service
public class SmartWaitService {

    /** Status yang masih dihitung sebagai pelanggan di depan (sama dengan aturan posisi). */
    public static final Set<QueueStatus> STATUS_DI_HITUNG =
            EnumSet.of(QueueStatus.WAITING, QueueStatus.CALLED, QueueStatus.IN_SERVICE);

    private final QueueRepository queueRepository;

    public SmartWaitService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    /**
     * Fungsi murni, tanpa database: total durasi pelanggan di depan dibagi petugas,
     * dibulatkan ke atas. Mengembalikan null bila petugas 0 supaya tidak ada pembagian nol.
     */
    public Integer hitungMenit(List<Integer> durasiDepan, int petugas) {
        if (petugas <= 0) {
            return null;
        }
        int total = 0;
        for (int durasi : durasiDepan) {
            total += durasi;
        }
        return (int) Math.ceil((double) total / petugas);
    }

    /** Keputusan tampilan, murni dari status antrean, daftar durasi di depan, dan petugas. */
    public HasilEstimasi untuk(QueueStatus status, List<Integer> durasiDepan, int petugas) {
        if (status == QueueStatus.CALLED) {
            return new HasilEstimasi(null, "Kamu sudah dipanggil, silakan menuju lokasi");
        }
        if (status == QueueStatus.IN_SERVICE || status == QueueStatus.COMPLETED
                || status == QueueStatus.CANCELLED || status == QueueStatus.NO_SHOW) {
            return new HasilEstimasi(null, null);
        }
        if (petugas <= 0) {
            return new HasilEstimasi(null, "Estimasi belum tersedia");
        }
        if (durasiDepan.isEmpty()) {
            return new HasilEstimasi(null, "Kamu berikutnya");
        }
        return new HasilEstimasi(hitungMenit(durasiDepan, petugas), null);
    }

    /** Pembungkus: mengambil durasi layanan pelanggan di depan dari repository, lalu memakai fungsi murni. */
    @Transactional(readOnly = true)
    public HasilEstimasi untuk(Queue queue) {
        Business business = queue.getBusiness();
        List<Integer> durasi = queueRepository
                .antreanDepan(business, queue.getQueueDate(), queue.getQueueNumber(), STATUS_DI_HITUNG)
                .stream()
                .map(q -> q.getReservation().getService().getEstimatedDurationMinutes())
                .toList();
        return untuk(queue.getStatus(), durasi, business.getActiveStaffCount());
    }
}
