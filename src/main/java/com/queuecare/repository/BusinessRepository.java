package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Category;
import com.queuecare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    List<Business> findByOwnerOrderByIdAsc(User owner);

    List<Business> findAllByOrderByIdAsc();

    boolean existsByCategory(Category category);
}
