package com.Udee.services;

import com.Udee.exceptions.notFound.ResidenceNotFoundException;
import com.Udee.models.ElectricMeter;
import com.Udee.models.Rate;
import com.Udee.models.Residence;
import com.Udee.models.User;
import com.Udee.models.projections.ResidenceProjection;
import com.Udee.repository.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ResidenceService {
    private final ResidenceRepository residenceRepository;
    private final ElectricMeterService electricMeterService;
    private final RateService rateService;
    private final UserService userService;

    @Autowired
    public ResidenceService(ResidenceRepository residenceRepository, ElectricMeterService electricMeterService, RateService rateService, UserService userService) {
        this.residenceRepository = residenceRepository;
        this.electricMeterService = electricMeterService;
        this.rateService = rateService;
        this.userService = userService;
    }

    public Page<Residence> findAll(Specification<Residence> spec, Pageable pageable) {
        return residenceRepository.findAll(spec,pageable);
    }

    public Residence addResidence(Residence residence) {
        residence.getAddress().setResidence(residence);
        return residenceRepository.save(residence);
    }

    public ResidenceProjection findProjectionById(Integer id) {
        return residenceRepository.getById(id).orElseThrow(ResidenceNotFoundException::new);
    }
    public Residence findById(Integer id) {
        return residenceRepository.findById(id).orElseThrow(ResidenceNotFoundException::new);
    }

    public Residence addElectricMeter(Integer residenceId, Integer meterId) {
        ElectricMeter e = electricMeterService.findById(meterId);
        if (e.getResidence()!=null){
            throw new HttpClientErrorException(HttpStatus.CONFLICT,"Meter it is in a residence already");
        }
        Residence r = findById(residenceId);
        r.setElectricMeter(e);
        return residenceRepository.save(r);
    }

    public Residence addRate(Integer residenceId, Integer rateId) {
        Rate rate = rateService.findById(rateId);
        Residence r = findById(residenceId);
        r.setRate(rate);
        return residenceRepository.save(r);
    }

    public Residence addUser(Integer residenceId, Integer userId) {
        User user = userService.findById(userId);
        Residence r = findById(residenceId);
        r.setUser(user);
        return residenceRepository.save(r);
    }

    public void delete(Integer id) {
        try{
            residenceRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new ResidenceNotFoundException();
        }
    }
}
