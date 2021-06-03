package com.Udee.repository;

import com.Udee.models.Measure;
import com.Udee.models.projections.UserRank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, BigInteger>, JpaSpecificationExecutor<Measure> {
    Page<Measure> findAll(Specification<Measure> spec, Pageable pageable);

    @Query(value = "SELECT m.* FROM measures m where m.electric_meter_id=? and (m.datetime between ? and ?) order by m.datetime asc", nativeQuery = true)
    List<Measure> findAllByElectricMeterBetweenDates(Integer meterId,LocalDate from, LocalDate to);

    @Query(value = "SELECT m.* FROM measures m join residences res on res.electric_meter_id=m.electric_meter_id where res.user_id=? and (m.datetime between ? and ?) order by m.datetime asc", nativeQuery = true)
    List<Measure> findAllByUserBetweenDates(Integer clientId,LocalDate from, LocalDate to);


    @Query(value ="SELECT  u.name as name,u.last_name as lastName, u.user_type_enum as userType,u.id as id,u.dni as dni,u.email as email, SUM(me.usage) AS sum " +
            "FROM users u " +
            "JOIN residences res ON res.user_id=u.id " +
            "JOIN measures me ON me.electric_meter_id=res.electric_meter_id " +
            "WHERE DATETIME BETWEEN ? AND ? " +
            "GROUP BY u.id " +
            "ORDER BY sum DESC LIMIT 10", nativeQuery = true)
    List<UserRank> findRankBetweenDates(LocalDate from, LocalDate to);
}
