package com.Udee.repository;

import com.Udee.models.ElectricMeter;
import com.Udee.models.projections.ElectricMeterProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectricMeterRepository extends JpaRepository<ElectricMeter,Integer> {
    //@Query(value = "SELECT b.id,b.name,E.id AS id,E.model AS model,E.serial AS SERIAL FROM electric_meters E JOIN brands b ON b.id=brand_id WHERE E.serial=?1",nativeQuery = true)
    Optional<ElectricMeter> findBySerial(String serial);

    @Query("SELECT E.brand as brand,E.id as id,E.model as model,E.serial as serial from ElectricMeter E")
    Page<ElectricMeterProjection> findAllProjected(Pageable pageable);

    Optional<ElectricMeterProjection> findProjectionById(Integer id);

    Page<ElectricMeterProjection> findBySerialStartingWith(Pageable pageable,String serial);


}
