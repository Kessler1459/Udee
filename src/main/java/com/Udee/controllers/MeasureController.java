package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.exceptions.WrongCredentialsException;
import com.Udee.models.ElectricMeter;
import com.Udee.models.Measure;
import com.Udee.models.dto.MeasureDTO;
import com.Udee.models.dto.MeasureRDTO;
import com.Udee.models.dto.UsageDTO;
import com.Udee.models.projections.UserRank;
import com.Udee.services.ElectricMeterService;
import com.Udee.services.MeasureService;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api")
public class MeasureController {
    private final MeasureService measureService;
    private final ElectricMeterService electricMeterService;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MeasureController(MeasureService measureService, ElectricMeterService electricMeterService, ConversionService conversionService, PasswordEncoder passwordEncoder) {
        this.measureService = measureService;
        this.electricMeterService = electricMeterService;
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/back-office/measures")
    public ResponseEntity<PostResponse> addMeasure(@RequestBody MeasureRDTO measure) {
        Measure m = Measure.builder().measure((int) measure.getValue()).dateTime(LocalDateTime.parse(measure.getDate())).build();
        ElectricMeter meter = electricMeterService.findOneBySerial(measure.getSerialNumber().trim());
        if (meter == null || !(passwordEncoder.matches(measure.getPassword().trim(), meter.getPass()))) {
            throw new WrongCredentialsException("Bad meter credentials");
        }
        m.setElectricMeter(meter);
        m = measureService.addMeasure(m);
        PostResponse res = new PostResponse(buildURL("/api/back-office/measures", m.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/web/residences/{residenceId}/usage")
    public ResponseEntity<UsageDTO> findUsageBetweenDatesByResidence(
            @PathVariable Integer residenceId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(measureService.findUsageBetweenDatesByResidence(residenceId, from, to));
    }

    @GetMapping("/web/clients/{clientId}/usage")
    public ResponseEntity<UsageDTO> findUsageBetweenDatesByClient(
            @PathVariable Integer clientId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(measureService.findUsageBetweenDatesByClient(clientId, from, to));
    }

    @GetMapping("/web/residences/{residenceId}/measures")
    public ResponseEntity<List<MeasureDTO>> findMeasuresBetweenDates(
            Pageable pageable,
            @Join(path = "electricMeter", alias = "e")
            @Join(path = "e.residence", alias = "r")
            @And({
                    @Spec(pathVars = "residenceId", path = "r.id", spec = Equal.class),
                    @Spec(path = "dateTime", params = {"from", "to"}, spec = Between.class)}) Specification<Measure> spec) {
        Page<Measure> p = measureService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<MeasureDTO> dtoList = p.stream().map(measure -> conversionService.convert(measure, MeasureDTO.class)).collect(Collectors.toList());
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }

    @GetMapping("/back-office/usage")
    public ResponseEntity<List<UserRank>> getTopTenUsers(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        final List<UserRank> userRanks = measureService.findRankBetweenDates(from, to);
        return ResponseEntity.status(userRanks.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT).body(userRanks);
    }
}