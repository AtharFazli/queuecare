package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Queue;
import com.queuecare.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByReservation(Reservation reservation);

    /** Nomor antrean terbesar pada bisnis dan tanggal, dipakai untuk nomor berikutnya. */
    @Query("select max(q.queueNumber) from Queue q where q.business = :business and q.queueDate = :tanggal")
    Optional<Integer> maxNomor(@Param("business") Business business, @Param("tanggal") LocalDate tanggal);
}
