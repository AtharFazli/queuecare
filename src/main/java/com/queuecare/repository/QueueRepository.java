package com.queuecare.repository;

import com.queuecare.dto.TotalStatusAntrean;
import com.queuecare.entity.Business;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import com.queuecare.enums.QueueStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByReservation(Reservation reservation);

    /** Status antrean untuk sejumlah reservasi, dipakai daftar Admin. */
    @Query("select q from Queue q join fetch q.reservation r where r.id in :ids")
    List<Queue> findByReservationIdIn(@Param("ids") List<Long> ids);

    /** Antrean beserta reservasi, layanan, dan bisnis, supaya aman dipakai di luar transaksi. */
    @Query("select q from Queue q join fetch q.reservation r "
            + "join fetch r.service join fetch q.business where q.reservation = :reservation")
    Optional<Queue> findByReservationLengkap(@Param("reservation") Reservation reservation);

    /** Nomor antrean terbesar pada bisnis dan tanggal, dipakai untuk nomor berikutnya. */
    @Query("select max(q.queueNumber) from Queue q where q.business = :business and q.queueDate = :tanggal")
    Optional<Integer> maxNomor(@Param("business") Business business, @Param("tanggal") LocalDate tanggal);

    /** Antrean bisnis pada satu tanggal, urut nomor, lengkap dengan reservasi dan pelanggan. */
    @Query("select q from Queue q join fetch q.reservation r "
            + "join fetch r.customer join fetch r.service join fetch q.business "
            + "where q.business = :business and q.queueDate = :tanggal order by q.queueNumber asc")
    List<Queue> antreanBisnis(@Param("business") Business business, @Param("tanggal") LocalDate tanggal);

    /** Antrean beserta reservasi, pelanggan, layanan, dan bisnis untuk satu baris. */
    @Query("select q from Queue q join fetch q.reservation r "
            + "join fetch r.customer join fetch r.service join fetch q.business where q.id = :id")
    Optional<Queue> findByIdLengkap(@Param("id") Long id);

    /** Jumlah antrean yang masih aktif dengan nomor lebih kecil, dipakai untuk posisi antrean. */
    @Query("select count(q) from Queue q where q.business = :business and q.queueDate = :tanggal "
            + "and q.queueNumber < :nomor and q.status in :status")
    long countPosisi(@Param("business") Business business,
                     @Param("tanggal") LocalDate tanggal,
                     @Param("nomor") int nomor,
                     @Param("status") Set<QueueStatus> status);

    /** Mengunci baris antrean supaya dua klik bersamaan tidak membuat status ganda. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Queue q where q.id = :id")
    Optional<Queue> lockById(@Param("id") Long id);

    /** Antrean yang masih dihitung sebagai pelanggan di depan, beserta durasi layanannya. */
    @Query("select q from Queue q join fetch q.reservation r join fetch r.service join fetch q.business "
            + "where q.business = :business and q.queueDate = :tanggal "
            + "and q.queueNumber < :nomor and q.status in :status order by q.queueNumber asc")
    List<Queue> antreanDepan(@Param("business") Business business,
                             @Param("tanggal") LocalDate tanggal,
                             @Param("nomor") int nomor,
                             @Param("status") Set<QueueStatus> status);

    // ===== angka laporan (semuanya query agregat) =====

    long countByBusinessAndStatus(Business business, QueueStatus status);

    /** Jumlah antrean per status dengan rentang reservation_date. */
    @Query("select count(q) from Queue q join q.reservation r "
            + "where q.business = :business and q.status = :status "
            + "and r.reservationDate between :dari and :sampai")
    long hitungPerStatus(@Param("business") Business business,
                         @Param("status") QueueStatus status,
                         @Param("dari") LocalDate dari,
                         @Param("sampai") LocalDate sampai);

    /** Ringkasan status antrean seluruh platform. */
    @Query("select new com.queuecare.dto.TotalStatusAntrean(q.status, count(q)) from Queue q group by q.status")
    List<TotalStatusAntrean> ringkasanStatusAntrean();
}
