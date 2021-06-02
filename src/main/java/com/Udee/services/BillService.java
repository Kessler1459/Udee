package com.Udee.services;

import com.Udee.exceptions.notFound.BillNotFoundException;
import com.Udee.models.Bill;
import com.Udee.models.projections.BillProjection;
import com.Udee.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BillService {
    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Page<Bill> findAll(Specification<Bill> spec, Pageable pageable) {
        return billRepository.findAll(spec, pageable);
    }

    public Bill findById(Integer id) {
        return billRepository.findById(id).orElseThrow(BillNotFoundException::new);
    }

    public BillProjection findProjectedById(Integer id) {
        return billRepository.findProjectionById(id).orElseThrow(BillNotFoundException::new);
    }



}
