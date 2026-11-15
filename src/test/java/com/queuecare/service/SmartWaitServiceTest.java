package com.queuecare.service;

import com.queuecare.dto.HasilEstimasi;
import com.queuecare.enums.QueueStatus;
import com.queuecare.repository.QueueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Uji fungsi hitung murni SmartWait (R9). Sengaja tanpa database supaya kasus tepi
 * mudah diuji dan mudah dijelaskan.
 */
@ExtendWith(MockitoExtension.class)
class SmartWaitServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private SmartWaitService smartWaitService;

    @Test
    void contohMasterPlanningEmpatKaliLimaBelasDuaPetugas() {
        assertEquals(30, smartWaitService.hitungMenit(List.of(15, 15, 15, 15), 2));
    }

    @Test
    void petugasNolTidakBagiNol() {
        assertNull(smartWaitService.hitungMenit(List.of(15, 15), 0));

        HasilEstimasi hasil = smartWaitService.untuk(QueueStatus.WAITING, List.of(15, 15), 0);
        assertNull(hasil.menit());
        assertEquals("Estimasi belum tersedia", hasil.pesan());
    }

    @Test
    void tidakAdaPelangganDiDepan() {
        HasilEstimasi hasil = smartWaitService.untuk(QueueStatus.WAITING, List.of(), 2);
        assertNull(hasil.menit());
        assertEquals("Kamu berikutnya", hasil.pesan());
    }

    @Test
    void layananBedaDurasiTetapDijumlah() {
        // 30 + 45 + 60 = 135 menit, dibagi 2 petugas = 67,5 -> 68 menit.
        assertEquals(68, smartWaitService.hitungMenit(List.of(30, 45, 60), 2));
    }

    @Test
    void pembulatanKeAtas() {
        // 25 menit dibagi 2 petugas = 12,5 -> 13 menit.
        assertEquals(13, smartWaitService.hitungMenit(List.of(25), 2));
    }

    @Test
    void satuPetugas() {
        assertEquals(30, smartWaitService.hitungMenit(List.of(15, 15), 1));
    }

    @Test
    void statusCalledSudahDipanggil() {
        HasilEstimasi hasil = smartWaitService.untuk(QueueStatus.CALLED, List.of(15), 2);
        assertNull(hasil.menit());
        assertEquals("Kamu sudah dipanggil, silakan menuju lokasi", hasil.pesan());
    }

    @Test
    void statusInServiceDanFinalTidakAdaEstimasi() {
        for (QueueStatus status : List.of(QueueStatus.IN_SERVICE, QueueStatus.COMPLETED,
                QueueStatus.CANCELLED, QueueStatus.NO_SHOW)) {
            HasilEstimasi hasil = smartWaitService.untuk(status, List.of(15), 2);
            assertNull(hasil.menit(), "menit harus kosong untuk " + status);
            assertNull(hasil.pesan(), "pesan harus kosong untuk " + status);
        }
    }
}
