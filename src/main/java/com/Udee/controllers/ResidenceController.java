package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Residence;
import com.Udee.models.dto.ResidenceDTO;
import com.Udee.models.projections.ResidenceProjection;
import com.Udee.services.ResidenceService;

import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/back-office/residences")
public class ResidenceController {
    private final ResidenceService residenceService;
    private final ConversionService conversionService;

    @Autowired
    public ResidenceController(ResidenceService residenceService, ConversionService conversionService) {
        this.residenceService = residenceService;
        this.conversionService = conversionService;
    }
    //todo a los no content agregar array en body
    @GetMapping
    public ResponseEntity<List<ResidenceDTO>> findAll(Pageable pageable, @And({
            @Spec(path = "electricMeter.serial", params = "electricMeter", spec = Like.class),
            @Spec(path = "address.street", params = "street", spec = Like.class),
            @Spec(path = "address.num", params = "number", spec = Like.class),
            @Spec(path = "rate.name", params = "rate", spec = Equal.class)}) Specification<Residence> spec) {
        final Page<Residence> p = residenceService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        final List<ResidenceDTO> list = p.stream().map(residence -> conversionService.convert(residence, ResidenceDTO.class)).collect(Collectors.toList());
        return ResponseEntity.status(p.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(list);
    }

    @PostMapping
    public ResponseEntity<PostResponse> addResidence(@RequestBody Residence r) {
        r = residenceService.addResidence(r);
        final PostResponse res = new PostResponse(buildURL("residences", r.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidenceProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(residenceService.findProjectionById(id));
    }

    @PutMapping("/{residenceId}/electricMeter/{meterId}")
    public ResponseEntity<ResidenceDTO> addElectricMeter(@PathVariable Integer residenceId, @PathVariable Integer meterId) {
        final Residence r = residenceService.addElectricMeter(residenceId, meterId);
        return ResponseEntity.ok(conversionService.convert(r, ResidenceDTO.class));
    }

    @PutMapping("/{residenceId}/rate/{rateId}")
    public ResponseEntity<ResidenceDTO> addRate(@PathVariable Integer residenceId, @PathVariable Integer rateId) {
        final Residence r = residenceService.addRate(residenceId, rateId);
        return ResponseEntity.ok(conversionService.convert(r, ResidenceDTO.class));
    }

    @PutMapping("/{residenceId}/user/{userId}")
    public ResponseEntity<ResidenceDTO> addUser(@PathVariable Integer residenceId, @PathVariable Integer userId) {
        final Residence r = residenceService.addUser(residenceId, userId);
        return ResponseEntity.ok(conversionService.convert(r, ResidenceDTO.class));
    }
}
