package com.Udee.services;

import com.Udee.models.Measure;
import com.Udee.models.Residence;
import com.Udee.models.User;
import com.Udee.models.dto.UsageDTO;
import com.Udee.repository.MeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeasureService {
    private final MeasureRepository measureRepository;
    private final ElectricMeterService electricMeterService;
    private final ResidenceService residenceService;
    private final UserService userService;

    @Autowired
    public MeasureService(MeasureRepository measureRepository, ElectricMeterService electricMeterService, ResidenceService residenceService, UserService userService) {
        this.measureRepository = measureRepository;
        this.electricMeterService = electricMeterService;
        this.residenceService = residenceService;
        this.userService = userService;
    }

    public Measure addMeasure(String meterSerial, Measure measure) {
        measure.setElectricMeter(electricMeterService.findOneBySerial(meterSerial));
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


}
