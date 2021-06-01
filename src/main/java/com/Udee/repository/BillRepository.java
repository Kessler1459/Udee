package com.Udee.repository;

import com.Udee.models.Bill;
import com.Udee.models.projections.BillProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer>, JpaSpecificationExecutor<Bill> {

    Page<Bill> findAll(Specification<Bill> spec, Pageable pageable);

    Optional<BillProjection> findProjectionById(Integer id);


}
