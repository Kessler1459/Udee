package com.Udee.services;

import com.Udee.models.Measure;
import com.Udee.models.Residence;
import com.Udee.models.dto.MeasureDTO;
import com.Udee.models.dto.UsageDTO;
import com.Udee.repository.MeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeasureService {
    private final MeasureRepository measureRepository;
    private final ElectricMeterService electricMeterService;
    private final ResidenceService residenceService;

    @Autowired
    public MeasureService(MeasureRepository measureRepository, ElectricMeterService electricMeterService, ResidenceService residenceService) {
        this.measureRepository = measureRepository;
        this.electricMeterService = electricMeterService;
        this.residenceService = residenceService;
    }

    public Measure addMeasure(String meterSerial, Measure measure) {
        measure.setElectricMeter(electricMeterService.findOneBySerial(meterSerial));
        return measureRepository.save(measure);
    }


    public Page<Measure> findAll(Specification<Measure> spec, Pageable pageable) {
        return measureRepository.findAll(spec, pageable);
    }
    //todo trigger update total de bills con rate nuevo
    public UsageDTO findUsageBetweenDates(Integer residenceId, LocalDate from, LocalDate to) {
        Residence r = residenceService.findById(residenceId);
        List<Measure> measures = measureRepository.findAllByElectricMeterBetweenDates(r.getElectricMeter().getId(), from, to);
        final Integer usage = measures.get(measures.size() - 1).getUsage() - measures.get(0).getUsage();
        return new UsageDTO(usage, measures.get(0).getElectricMeter().getResidence().getRate().getPriceXKW() * usage);
    }
}
