package com.Udee.services;

import com.Udee.exceptions.notFound.RateNotFoundException;
import com.Udee.models.Rate;
import com.Udee.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RateService {
    private final RateRepository rateRepository;

    @Autowired
    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public Rate addRate(Rate r) {
        return rateRepository.save(r);
    }

    public Page<Rate> findAll(Specification<Rate> spec, Pageable pageable) {
        return rateRepository.findAll(spec,pageable);
    }

    public Rate findById(Integer id) {
        return rateRepository.findById(id).orElseThrow(RateNotFoundException::new);
    }

    public void deleteRate(Integer id) {
        try {
            rateRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new RateNotFoundException();
        }
    }


    public Rate updateRate( Rate r) {
        return rateRepository.save(r);
    }
}
