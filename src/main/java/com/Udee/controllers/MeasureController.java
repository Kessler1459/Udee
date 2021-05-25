package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Measure;
import com.Udee.models.dto.MeasureDTO;
import com.Udee.models.dto.UsageDTO;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api")
public class MeasureController {
    private final MeasureService measureService;
    private final ConversionService conversionService;

    @Autowired
    public MeasureController(MeasureService measureService, ConversionService conversionService) {
        this.measureService = measureService;
        this.conversionService = conversionService;
    }

    //TODO talvez alguna key o auth
    @PostMapping("/electricmeters/{meterSerial}/measures")
    public ResponseEntity<PostResponse> addMeasure(@PathVariable String meterSerial, @RequestBody Measure measure) {
        measure = measureService.addMeasure(meterSerial, measure);
        PostResponse res = new PostResponse(buildURL("measure", measure.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    //todo chequear que exception tira al no poner todos los spec params java.lang.IllegalArgumentException: expected 2 http params
    @GetMapping("/clients/residences/{residenceId}/usage")
    public ResponseEntity<UsageDTO> findUsageBetweenDates(
            @PathVariable Integer residenceId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(measureService.findUsageBetweenDates(residenceId, from, to));
    }

    @GetMapping("/clients/residences/{residenceId}/measures")
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
}