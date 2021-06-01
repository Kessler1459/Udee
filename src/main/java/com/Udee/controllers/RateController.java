package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Rate;
import com.Udee.services.RateService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api/back-office/rates")
public class RateController {
    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping
    public PostResponse addRate(@RequestBody Rate r) {
        r = rateService.addRate(r);
        return new PostResponse(buildURL("/api/back-office/rates", r.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
    }

    @GetMapping
    public ResponseEntity<List<Rate>> findAll(@And({
            @Spec(path = "name", spec = StartingWith.class),
            @Spec(path = "priceXKW", spec = Equal.class)
    }) Specification<Rate> spec, Pageable pageable) {
        Page<Rate> p = rateService.findAll(spec,pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        return ResponseEntity.status(p.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(p.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rate> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rateService.findById(id));
    }

    @PutMapping
    public ResponseEntity<Rate> updateRate(@RequestBody Rate r) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rateService.updateRate(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Integer id) {
        rateService.deleteRate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
