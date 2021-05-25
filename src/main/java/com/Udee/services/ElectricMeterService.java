package com.Udee.services;


import com.Udee.exceptions.ElectricMeterNotFoundException;
import com.Udee.models.Brand;
import com.Udee.models.ElectricMeter;
import com.Udee.models.dto.ElectricMeterDTO;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.repository.ElectricMeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ElectricMeterService {
    private final ElectricMeterRepository electricMeterRepository;
    private final BrandService brandService;
    private final ConversionService conversionService;

    @Autowired
    public ElectricMeterService(ElectricMeterRepository electricMeterRepository, BrandService brandService, ConversionService conversionService) {
        this.electricMeterRepository = electricMeterRepository;
        this.brandService = brandService;
        this.conversionService = conversionService;
    }


    public ElectricMeter addElectricMeter(ElectricMeter electricMeter){
        if (electricMeter.getBrand()!=null) {
            Brand b= brandService.findOneByName(electricMeter.getBrand().getName());
            electricMeter.setBrand(b);
        }
        return electricMeterRepository.save(electricMeter);
    }

    public Page<ElectricMeterProjection> findAll(Pageable pageable) {
        return electricMeterRepository.findAllProjected(pageable);
    }

    public ElectricMeter findById(Integer elId){
        return electricMeterRepository.findById(elId).orElseThrow(ElectricMeterNotFoundException::new);
    }


    public ElectricMeterProjection findProjectionById(Integer elId){
        return electricMeterRepository.findProjectionById(elId).orElseThrow(ElectricMeterNotFoundException::new);
    }

    public ElectricMeterDTO setBrandToElectricMeter(Integer elId, Integer brandId) {
        ElectricMeter e=findById(elId);
        Brand b= brandService.findById(brandId);
        e.setBrand(b);
        return conversionService.convert(electricMeterRepository.save(e), ElectricMeterDTO.class);
    }

    public ElectricMeter findOneBySerial(String serial) {
        return electricMeterRepository.findBySerial(serial).orElseThrow(ElectricMeterNotFoundException::new);
    }

    public Page<ElectricMeterProjection> findBySerial(Pageable pageable,String serial){
        return electricMeterRepository.findBySerialStartingWith(pageable,serial);
    }
}
