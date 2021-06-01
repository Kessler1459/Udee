package com.Udee.repository;

import com.Udee.models.ElectricMeter;
import com.Udee.models.projections.ElectricMeterProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectricMeterRepository extends JpaRepository<ElectricMeter,Integer> , JpaSpecificationExecutor<ElectricMeter> {

    Optional<ElectricMeter> findBySerial(String serial);

    @Query("SELECT E.model as model,E.id as id,E.serial as serial from ElectricMeter E")
    Page<ElectricMeterProjection> findAllProjected(Pageable pageable);

    Optional<ElectricMeterProjection> findProjectionById(Integer id);

    Page<ElectricMeterProjection> findBySerialStartingWith(Pageable pageable,String serial);


}
