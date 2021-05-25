package com.Udee.repository;

import com.Udee.models.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository <Brand,Integer> {

    Page<Brand> findAllBy(Pageable pageable);

    List<Brand> findByNameIgnoreCase(String name);
    Optional<Brand> findOneByNameIgnoreCase(String name);
}
