package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessScheduleRepository extends JpaRepository<BusinessSchedule, Long> {

    List<BusinessSchedule> findByBusinessOrderByIdAsc(Business business);

    Optional<BusinessSchedule> findByBusinessAndDayOfWeek(Business business, String dayOfWeek);
}
