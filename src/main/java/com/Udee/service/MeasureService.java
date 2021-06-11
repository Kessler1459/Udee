package com.Udee.service;

import com.Udee.exception.notFound.MeasureNotFoundException;
import com.Udee.models.Measure;
import com.Udee.models.Residence;
import com.Udee.models.User;
import com.Udee.models.dto.UsageDTO;
import com.Udee.models.projections.UserRank;
import com.Udee.repository.MeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
public class MeasureService {
    private final MeasureRepository measureRepository;
    private final ResidenceService residenceService;
    private final UserService userService;

    @Autowired
    public MeasureService(MeasureRepository measureRepository, ResidenceService residenceService, UserService userService) {
        this.measureRepository = measureRepository;
        this.residenceService = residenceService;
        this.userService = userService;
    }

    public Measure addMeasure(Measure measure) {
        return measureRepository.save(measure);
    }


    public Page<Measure> findAll(Specification<Measure> spec, Pageable pageable) {
        return measureRepository.findAll(spec, pageable);
    }

    public UsageDTO findUsageBetweenDatesByResidence(Integer residenceId, LocalDate from, LocalDate to) {
        Residence r = residenceService.findById(residenceId);
        List<Measure> measures = measureRepository.findAllByElectricMeterBetweenDates(r.getElectricMeter().getId(), from, to);
        return getUsageDTO(measures);
    }

    public UsageDTO findUsageBetweenDatesByClient(Integer clientId, LocalDate from, LocalDate to) {
        User u = userService.findById(clientId);
        List<Measure> measures = measureRepository.findAllByUserBetweenDates(clientId, from, to);
        return getUsageDTO(measures);
    }

    private UsageDTO getUsageDTO(List<Measure> measures) {
        int usage = 0;
        float price = 0;
        for (Measure m : measures) {
            usage += m.getUsage();
            price += m.getPrice();
        }
        return new UsageDTO(usage, price);
    }


    public List<UserRank> findRankBetweenDates(LocalDate from, LocalDate to) {
        return measureRepository.findRankBetweenDates(from, to);
    }

    public Measure findById(Integer id) {
        return measureRepository.findById(BigInteger.valueOf(id)).orElseThrow(MeasureNotFoundException::new);
    }
}
