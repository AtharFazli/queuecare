package com.queuecare.repository;

import com.queuecare.dto.TotalPerHari;
import com.queuecare.dto.TotalLayanan;
import com.queuecare.entity.Business;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.User;
import com.queuecare.enums.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** Reservasi beserta relasi yang dipakai halaman detail. */
    @Query("select r from Reservation r join fetch r.customer "
            + "join fetch r.business b join fetch b.category join fetch r.service "
            + "where r.id = :id")
    Optional<Reservation> findByIdLengkap(@Param("id") Long id);

    /** Riwayat milik satu Customer, terbaru lebih dulu. */
    @Query("select r from Reservation r "
            + "join fetch r.business b join fetch b.category join fetch r.service "
            + "where r.customer = :customer order by r.id desc")
    List<Reservation> riwayatCustomer(@Param("customer") User customer);

    long countByReservationDate(LocalDate reservationDate);

    /** Jumlah reservasi aktif pada satu slot, dipakai untuk menghitung kapasitas. */
    long countByBusinessAndReservationDateAndReservationTimeAndStatus(Business business,
                                                                     LocalDate reservationDate,
                                                                     LocalTime reservationTime,
                                                                     ReservationStatus status);

    // ===== angka laporan (semuanya query agregat) =====

    long countByBusinessAndReservationDate(Business business, LocalDate reservationDate);

    long countByBusinessAndReservationDateBetween(Business business, LocalDate dari, LocalDate sampai);

    long countByBusinessAndStatusAndReservationDateBetween(Business business, ReservationStatus status,
                                                           LocalDate dari, LocalDate sampai);

    /** Pelanggan unik dalam rentang tanggal, dihitung distinct di database. */
    @Query("select count(distinct r.customer.id) from Reservation r "
            + "where r.business = :business and r.reservationDate between :dari and :sampai")
    long hitungPelangganUnik(@Param("business") Business business,
                             @Param("dari") LocalDate dari,
                             @Param("sampai") LocalDate sampai);

    /** Layanan paling banyak dipilih, lima teratas. */
    @Query("select new com.queuecare.dto.TotalLayanan(s.name, count(r)) from Reservation r join r.service s "
            + "where r.business = :business and r.reservationDate between :dari and :sampai "
            + "group by s.name order by count(r) desc")
    List<TotalLayanan> layananTerpopuler(@Param("business") Business business,
                                         @Param("dari") LocalDate dari,
                                         @Param("sampai") LocalDate sampai,
                                         Pageable pageable);

    /** Reservasi per hari untuk laporan Admin. Tanggal kosong berarti tanpa batas. */
    @Query("select new com.queuecare.dto.TotalPerHari(r.reservationDate, count(r)) from Reservation r "
            + "where (:dari is null or r.reservationDate >= :dari) "
            + "and (:sampai is null or r.reservationDate <= :sampai) "
            + "group by r.reservationDate order by r.reservationDate")
    List<TotalPerHari> reservasiPerHari(@Param("dari") LocalDate dari, @Param("sampai") LocalDate sampai);

    /** Sepuluh aktivitas terbaru Admin. */
    @Query("select r from Reservation r join fetch r.customer "
            + "join fetch r.business b join fetch b.category join fetch r.service "
            + "order by r.createdAt desc")
    List<Reservation> aktivitasTerbaru(Pageable pageable);

    /** Daftar reservasi Admin dengan filter tanggal, bisnis, dan status. */
    @Query("select r from Reservation r join fetch r.customer "
            + "join fetch r.business b join fetch b.category join fetch r.service "
            + "where (:dari is null or r.reservationDate >= :dari) "
            + "and (:sampai is null or r.reservationDate <= :sampai) "
            + "and (:bisnisId is null or r.business.id = :bisnisId) "
            + "and (:status is null or r.status = :status) order by r.id desc")
    List<Reservation> cariUntukAdmin(@Param("dari") LocalDate dari,
                                     @Param("sampai") LocalDate sampai,
                                     @Param("bisnisId") Long bisnisId,
                                     @Param("status") ReservationStatus status,
                                     Pageable pageable);

    @Query("select count(r) from Reservation r "
            + "where (:dari is null or r.reservationDate >= :dari) "
            + "and (:sampai is null or r.reservationDate <= :sampai) "
            + "and (:bisnisId is null or r.business.id = :bisnisId) "
            + "and (:status is null or r.status = :status)")
    long hitungUntukAdmin(@Param("dari") LocalDate dari,
                          @Param("sampai") LocalDate sampai,
                          @Param("bisnisId") Long bisnisId,
                          @Param("status") ReservationStatus status);
}
