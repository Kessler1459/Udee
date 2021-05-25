package com.Udee.services;

import com.Udee.exceptions.BrandNotFoundException;
import com.Udee.models.Brand;
import com.Udee.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Page<Brand> findAll(Pageable pageable) {
        return brandRepository.findAllBy(pageable);
    }

    public Brand addBrand(Brand brand) {//talvez chequear unique
        return brandRepository.save(brand);
    }


    public List<Brand> findByName(String name){
        return brandRepository.findByNameIgnoreCase(name);
    }

    public Brand findOneByName(String name){
        return brandRepository.findOneByNameIgnoreCase(name).orElseThrow(BrandNotFoundException::new);
    }

    public Brand findById(Integer id){
        return brandRepository.findById(id).orElseThrow(BrandNotFoundException::new);
    }

}
