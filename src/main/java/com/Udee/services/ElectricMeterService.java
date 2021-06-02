package com.Udee.services;


import com.Udee.exceptions.notFound.ElectricMeterNotFoundException;
import com.Udee.models.ElectricMeter;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.repository.ElectricMeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ElectricMeterService {
    private final ElectricMeterRepository electricMeterRepository;
    private final ConversionService conversionService;
    private final ModelService modelService;

    @Autowired
    public ElectricMeterService(ElectricMeterRepository electricMeterRepository, ConversionService conversionService, ModelService modelService) {
        this.electricMeterRepository = electricMeterRepository;
        this.modelService = modelService;
        this.conversionService = conversionService;
    }


    public ElectricMeter addElectricMeter(ElectricMeter electricMeter){
        return electricMeterRepository.save(electricMeter);
    }

    public Page<ElectricMeter> findAll(Specification<ElectricMeter> spec, Pageable pageable) {
        return electricMeterRepository.findAll(spec,pageable);
    }

    public ElectricMeter findById(Integer elId){
        return electricMeterRepository.findById(elId).orElseThrow(ElectricMeterNotFoundException::new);
    }


    public ElectricMeterProjection findProjectionById(Integer elId){
        return electricMeterRepository.findProjectionById(elId).orElseThrow(ElectricMeterNotFoundException::new);
    }

    public ElectricMeter setModelToElectricMeter(Integer elId, Integer modelId) {
        ElectricMeter e=findById(elId);
        e.setModel(modelService.findById(modelId));
        return electricMeterRepository.save(e);
    }

    public ElectricMeter findOneBySerial(String serial) {
        return electricMeterRepository.findBySerial(serial).orElseThrow(ElectricMeterNotFoundException::new);
    }

    public void delete(Integer id) {
        try{
            electricMeterRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ElectricMeterNotFoundException();
        }
    }
}
