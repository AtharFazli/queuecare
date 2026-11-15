package com.queuecare.service;

import com.queuecare.dto.ReservationForm;
import com.queuecare.dto.Slot;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.Service;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.enums.QueueStatus;
import com.queuecare.enums.ReservationStatus;
import com.queuecare.exception.TidakDitemukanException;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.repository.QueueRepository;
import com.queuecare.repository.ReservationRepository;
import com.queuecare.repository.ServiceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ReservationService {

    private static final int DURASI_SLOT_MENIT = 30;
    private static final int MAKS_HARI_DEPAN = 14;
    private static final int MAKS_COBA = 3;

    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final BusinessScheduleRepository scheduleRepository;
    private final UserContext userContext;
    private final TransactionTemplate transactionTemplate;

    public ReservationService(ReservationRepository reservationRepository,
                              QueueRepository queueRepository,
                              ServiceRepository serviceRepository,
                              BusinessRepository businessRepository,
                              BusinessScheduleRepository scheduleRepository,
                              UserContext userContext,
                              TransactionTemplate transactionTemplate) {
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.serviceRepository = serviceRepository;
        this.businessRepository = businessRepository;
        this.scheduleRepository = scheduleRepository;
        this.userContext = userContext;
        this.transactionTemplate = transactionTemplate;
    }

    // ===== halaman form =====

    /** Layanan untuk form reservasi. Menolak layanan nonaktif dan bisnis non-ACTIVE. */
    public Service layananAktif(Long serviceId) {
        Service service = serviceRepository.findByIdWithBusiness(serviceId)
                .orElseThrow(() -> new TidakDitemukanException("Layanan tidak ditemukan."));
        if (!service.isActive()) {
            throw new IllegalArgumentException("Layanan ini sedang tidak aktif.");
        }
        if (service.getBusiness().getStatus() != BusinessStatus.ACTIVE) {
            throw new IllegalArgumentException("Bisnis sedang tidak aktif.");
        }
        return service;
    }

    /** Daftar slot 30 menit untuk tanggal tertentu. Tanggal kosong menghasilkan daftar kosong. */
    public List<Slot> slotUntuk(Business business, LocalDate tanggal) {
        List<Slot> daftar = new ArrayList<>();
        if (tanggal == null) {
            return daftar;
        }
        BusinessSchedule jadwal = scheduleRepository
                .findByBusinessAndDayOfWeek(business, BusinessSchedule.namaHari(tanggal))
                .orElse(null);
        if (jadwal == null || jadwal.isClosed()
                || jadwal.getOpenTime() == null || jadwal.getCloseTime() == null) {
            return daftar;
        }
        LocalDate hariIni = LocalDate.now();
        LocalTime sekarang = LocalTime.now();
        for (LocalTime t = jadwal.getOpenTime();
             !t.plusMinutes(DURASI_SLOT_MENIT).isAfter(jadwal.getCloseTime());
             t = t.plusMinutes(DURASI_SLOT_MENIT)) {
            long terisi = reservationRepository
                    .countByBusinessAndReservationDateAndReservationTimeAndStatus(
                            business, tanggal, t, ReservationStatus.CONFIRMED);
            boolean penuh = terisi >= business.getMaxCapacity();
            boolean lewat = tanggal.equals(hariIni) && !t.isAfter(sekarang);
            daftar.add(new Slot(t, penuh, lewat));
        }
        return daftar;
    }

    // ===== pembuatan =====

    /**
     * Reservasi dan antrean dibuat dalam satu transaksi (R13). Bila kode atau nomor antrean
     * bentrok karena dua request bersamaan, transaksi diulang dari awal.
     */
    public Reservation buat(Long serviceId, ReservationForm form) {
        return buat(serviceId, form, userContext.currentUser());
    }

    /** Versi dengan pembeli eksplisit, dipakai seed dummy yang tidak punya sesi login. */
    public Reservation buat(Long serviceId, ReservationForm form, User customer) {
        for (int coba = 1; coba <= MAKS_COBA; coba++) {
            try {
                return transactionTemplate.execute(status -> buatTransaksi(customer, serviceId, form));
            } catch (RuntimeException ex) {
                if (coba < MAKS_COBA && karenaUnik(ex)) {
                    continue;
                }
                throw ex;
            }
        }
        throw new IllegalStateException("Reservasi gagal dibuat.");
    }

    private Reservation buatTransaksi(User customer, Long serviceId, ReservationForm form) {
        Service service = layananAktif(serviceId);
        Business business = service.getBusiness();
        LocalDate tanggal = form.getTanggal();
        LocalTime jam = form.getJam();
        if (tanggal == null || jam == null) {
            throw new IllegalArgumentException("Tanggal dan jam wajib diisi.");
        }
        LocalDate hariIni = LocalDate.now();
        if (tanggal.isBefore(hariIni) || tanggal.isAfter(hariIni.plusDays(MAKS_HARI_DEPAN))) {
            throw new IllegalArgumentException("Tanggal hanya bisa hari ini sampai 14 hari ke depan.");
        }
        BusinessSchedule jadwal = scheduleRepository
                .findByBusinessAndDayOfWeek(business, BusinessSchedule.namaHari(tanggal))
                .orElseThrow(() -> new IllegalArgumentException("Bisnis tidak buka pada hari itu."));
        if (jadwal.isClosed()) {
            throw new IllegalArgumentException("Bisnis libur pada hari itu.");
        }
        if (jadwal.getOpenTime() == null || jadwal.getCloseTime() == null) {
            throw new IllegalArgumentException("Jam operasional belum diatur.");
        }
        if (!slotValid(jam, jadwal.getOpenTime(), jadwal.getCloseTime())) {
            throw new IllegalArgumentException("Jam bukan slot yang valid (kelipatan 30 menit).");
        }
        if (tanggal.equals(hariIni) && !jam.isAfter(LocalTime.now())) {
            throw new IllegalArgumentException("Slot ini sudah lewat.");
        }

        // Kunci baris bisnis supaya hitung kapasitas dan nomor antrean tidak berlomba (KNF-11).
        businessRepository.lockById(business.getId());

        long terisi = reservationRepository
                .countByBusinessAndReservationDateAndReservationTimeAndStatus(
                        business, tanggal, jam, ReservationStatus.CONFIRMED);
        if (terisi >= business.getMaxCapacity()) {
            throw new IllegalArgumentException("Slot ini sudah penuh.");
        }

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setBusiness(business);
        reservation.setService(service);
        reservation.setReservationDate(tanggal);
        reservation.setReservationTime(jam);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setNotes(form.getCatatan());
        reservation.setReservationCode(buatKode(tanggal));
        reservationRepository.save(reservation);

        Queue queue = new Queue();
        queue.setReservation(reservation);
        queue.setBusiness(business);
        queue.setQueueDate(tanggal);
        queue.setQueueNumber(nomorBerikut(business, tanggal));
        queue.setStatus(QueueStatus.WAITING);
        queueRepository.save(queue);

        return reservation;
    }

    private boolean slotValid(LocalTime jam, LocalTime buka, LocalTime tutup) {
        if (jam.isBefore(buka) || jam.plusMinutes(DURASI_SLOT_MENIT).isAfter(tutup)) {
            return false;
        }
        return buka.until(jam, ChronoUnit.MINUTES) % DURASI_SLOT_MENIT == 0;
    }

    /** Kode QC-YYYYMMDD-XXXX, urutan empat digit per tanggal. */
    private String buatKode(LocalDate tanggal) {
        long urutan = reservationRepository.countByReservationDate(tanggal) + 1;
        String tanggalFormat = tanggal.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "QC-" + tanggalFormat + "-" + String.format("%04d", urutan);
    }

    private int nomorBerikut(Business business, LocalDate tanggal) {
        return queueRepository.maxNomor(business, tanggal).orElse(0) + 1;
    }

    private boolean karenaUnik(Throwable ex) {
        while (ex != null) {
            if (ex instanceof DataIntegrityViolationException) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }

    // ===== riwayat dan detail =====

    public List<Reservation> riwayat() {
        return reservationRepository.riwayatCustomer(userContext.currentUser());
    }

    /** Reservasi milik Customer yang login, kalau bukan dianggap tidak ada (KNF-05). */
    public Reservation milikSaya(Long id) {
        User customer = userContext.currentUser();
        Reservation reservation = reservationRepository.findByIdLengkap(id)
                .orElseThrow(() -> new TidakDitemukanException("Reservasi tidak ditemukan."));
        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            throw new TidakDitemukanException("Reservasi tidak ditemukan.");
        }
        return reservation;
    }

    public Queue antrean(Reservation reservation) {
        return queueRepository.findByReservation(reservation).orElse(null);
    }

    // ===== pembatalan =====

    /** Batalkan selama antrean masih WAITING. Reservasi dan antrean berubah dalam satu transaksi. */
    public void batalkan(Long id) {
        User customer = userContext.currentUser();
        transactionTemplate.executeWithoutResult(status -> {
            Reservation reservation = reservationRepository.findByIdLengkap(id)
                    .orElseThrow(() -> new TidakDitemukanException("Reservasi tidak ditemukan."));
            if (!reservation.getCustomer().getId().equals(customer.getId())) {
                throw new TidakDitemukanException("Reservasi tidak ditemukan.");
            }
            Queue queue = queueRepository.findByReservation(reservation)
                    .orElseThrow(() -> new IllegalStateException("Antrean tidak ditemukan."));
            if (queue.getStatus() != QueueStatus.WAITING) {
                throw new IllegalStateException("Antrean sudah tidak bisa dibatalkan.");
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            queue.setStatus(QueueStatus.CANCELLED);
            reservationRepository.save(reservation);
            queueRepository.save(queue);
        });
    }
}
