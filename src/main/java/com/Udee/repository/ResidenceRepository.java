package com.Udee.repository;

import com.Udee.models.Residence;
import com.Udee.models.projections.ResidenceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidenceRepository extends JpaRepository<Residence,Integer> {
    Optional<ResidenceProjection> getById(Integer id);

    Page<Residence> findAll(Specification<Residence> spec, Pageable pageable);
}
