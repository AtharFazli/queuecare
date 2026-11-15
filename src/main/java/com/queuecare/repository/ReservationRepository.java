package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.User;
import com.queuecare.enums.ReservationStatus;
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
}
