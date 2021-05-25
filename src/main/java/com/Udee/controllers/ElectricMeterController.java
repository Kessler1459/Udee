package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.ElectricMeter;
import com.Udee.models.dto.ElectricMeterDTO;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.services.ElectricMeterService;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/electricmeters")
public class ElectricMeterController {
    private final ElectricMeterService electricMeterService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ElectricMeterController(ElectricMeterService electricMeterService, PasswordEncoder passwordEncoder) {
        this.electricMeterService = electricMeterService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addElectricMeter(@RequestBody ElectricMeter electricMeter, @RequestHeader("Authorization") String pass) {
        electricMeter.setPass(passwordEncoder.encode(pass)); //todo talvez autogenerar esta pass
        electricMeter = electricMeterService.addElectricMeter(electricMeter);
        PostResponse p = new PostResponse(
                buildURL("electricmeters", electricMeter.getId().toString()),
                HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping
    public ResponseEntity<List<ElectricMeterProjection>> findAll(Pageable pageable) {
        final Page<ElectricMeterProjection> p = electricMeterService.findAll(pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        return ResponseEntity.status(p.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(p.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectricMeterProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(electricMeterService.findProjectionById(id));
    }

    @GetMapping(params = {"serial"})
    public ResponseEntity<List<ElectricMeterProjection>> findBySerial(Pageable pageable, @RequestParam String serial) {
        final Page<ElectricMeterProjection> p = electricMeterService.findBySerial(pageable, serial);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        return ResponseEntity.status(p.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(p.getContent());
    }

    @PutMapping("/{elId}/brand/{brandId}")
    public ResponseEntity<ElectricMeterDTO> setBrandToElectricMeter(@PathVariable Integer elId, @PathVariable Integer brandId) {
        ElectricMeterDTO electricMeterDTO = electricMeterService.setBrandToElectricMeter(elId, brandId);
        return ResponseEntity.ok(electricMeterDTO);
    }

}
