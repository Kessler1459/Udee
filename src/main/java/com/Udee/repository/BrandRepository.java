package com.Udee.repository;

import com.Udee.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository <Brand,Integer>, JpaSpecificationExecutor<Brand> {

}
