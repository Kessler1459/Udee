package com.Udee.repository;

import com.Udee.models.Rate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate,Integer> {
    Optional<Rate> findByName(String name);
    Page<Rate> findByNameStartingWith(Pageable pageable,String name);
}
