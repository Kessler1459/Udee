package com.Udee.service;

import com.Udee.exception.notFound.BrandNotFoundException;
import com.Udee.exception.notFound.ElectricMeterNotFoundException;
import com.Udee.models.Brand;
import com.Udee.models.Model;
import com.Udee.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final ModelService modelService;

    @Autowired
    public BrandService(BrandRepository brandRepository, ModelService modelService) {
        this.brandRepository = brandRepository;
        this.modelService = modelService;
    }

    public Page<Brand> findAll(Specification<Brand> spec, Pageable pageable) {
        return brandRepository.findAll(spec,pageable);
    }

    public Brand addBrand(Brand brand) {//talvez chequear unique
        return brandRepository.save(brand);
    }

    public Brand findById(Integer id){
        return brandRepository.findById(id).orElseThrow(BrandNotFoundException::new);
    }

    public Brand addModelToBrand(Integer id, Integer modelId) {
        Brand b = findById(id);
        Model m= modelService.findById(modelId);
        b.addModel(m);
        return brandRepository.save(b);
    }

    public void delete(Integer id) {
        try{
            brandRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new ElectricMeterNotFoundException();
        }
    }
}
