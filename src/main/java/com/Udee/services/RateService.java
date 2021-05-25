package com.Udee.services;

import com.Udee.exceptions.RateNotFoundException;
import com.Udee.models.Rate;
import com.Udee.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateService {
    private RateRepository rateRepository;

    @Autowired
    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public Rate addRate(Rate r) {
        return rateRepository.save(r);
    }

    public Page<Rate> findAll(Pageable pageable) {
        return rateRepository.findAll(pageable);
    }

    public Rate findById(Integer id) {
        return rateRepository.findById(id).orElseThrow(RateNotFoundException::new);
    }

    public void deleteRate(Integer id) {
        rateRepository.deleteById(id);
    }

    public Rate findByName(String name) {
        return rateRepository.findByName(name).orElseThrow(RateNotFoundException::new);
    }

    public Page<Rate> findAllByName(Pageable pageable, String name) {
        return rateRepository.findByNameStartingWith(pageable, name);
    }
}
