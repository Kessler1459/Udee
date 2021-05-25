package com.Udee.controllers;

import com.Udee.models.Bill;
import com.Udee.models.dto.BillDTO;
import com.Udee.models.projections.BillProjection;
import com.Udee.services.BillService;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;

import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.NotNull;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BillController {

    private final BillService billService;
    private final ConversionService conversionService;

    @Autowired
    public BillController(BillService billService, ConversionService conversionService) {
        this.billService = billService;
        this.conversionService = conversionService;
    }

    @RequestMapping(value = {"/clients/{idUser}/bills", "/back-office/clients/{idUser}/bills"}, method = RequestMethod.GET)
    private ResponseEntity<List<BillDTO>> findAllByUser(
            Pageable pageable,
            @And({
                    @Spec(pathVars = "idUser", path = "user.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "isPaid", spec = NotNull.class)}) Specification<Bill> spec) {
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/back-office/residences/{idResidence}/bills")
    private ResponseEntity<List<BillDTO>> findAllByResidenceBack(
            Pageable pageable,
            @Join(path = "user", alias = "u")
            @Join(path = "u.residences", alias = "r")
            @And({
                    @Spec(pathVars = "idResidence", path = "r.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "isPaid", spec = NotNull.class)}) Specification<Bill> spec) {
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/clients/residences/{idResidence}/bills")
    private ResponseEntity<List<BillDTO>> findAllByResidence(
            Pageable pageable,
            @Join(path = "user", alias = "u")
            @Join(path = "u.residences", alias = "r")
            @And({
                    @Spec(pathVars = "idResidence", path = "r.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "isPaid", spec = NotNull.class)}) Specification<Bill> spec) {
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/back-office/bills/{id}")
    private ResponseEntity<BillProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(billService.findProjectedById(id));
    }

    private ResponseEntity<List<BillDTO>> getListResponseEntity(Pageable pageable, @And({
            @Spec(pathVars = "idResidence", path = "r.id", spec = Equal.class),
            @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
            @Spec(path = "payment", params = "isPaid", spec = NotNull.class)}) @Join(path = "user", alias = "u") Specification<Bill> spec) {
        Page<Bill> p = billService.findAllByResidence(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<BillDTO> dtoList = p.stream().map(bill -> conversionService.convert(bill, BillDTO.class)).collect(Collectors.toList());
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }


}
