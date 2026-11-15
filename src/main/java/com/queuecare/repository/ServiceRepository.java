package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByBusinessOrderByIdAsc(Business business);
}
