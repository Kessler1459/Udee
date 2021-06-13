package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.exception.WrongCredentialsException;
import com.Udee.models.ElectricMeter;
import com.Udee.models.Measure;
import com.Udee.models.Residence;
import com.Udee.models.dto.MeasureDTO;
import com.Udee.models.dto.MeasureRDTO;
import com.Udee.models.dto.UsageDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.projections.UserRank;
import com.Udee.service.ElectricMeterService;
import com.Udee.service.MeasureService;
import com.Udee.service.ResidenceService;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;
import static com.Udee.utils.ListMapper.listToDto;

@RestController
@RequestMapping("/api")
public class MeasureController {
    private final MeasureService measureService;
    private final ElectricMeterService electricMeterService;
    private final ResidenceService residenceService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MeasureController(MeasureService measureService, ElectricMeterService electricMeterService, ResidenceService residenceService, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.measureService = measureService;
        this.electricMeterService = electricMeterService;
        this.residenceService = residenceService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/measures")
    public ResponseEntity<PostResponse> addMeasure(@RequestBody MeasureRDTO measure) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Measure m = Measure.builder().measure((int) measure.getValue()).dateTime(LocalDateTime.parse(measure.getDate(),formatter)).build();
        ElectricMeter meter = electricMeterService.findOneBySerial(measure.getSerialNumber().trim());
        if (meter == null || !(passwordEncoder.matches(measure.getPassword().trim(), meter.getPass()))) {
            throw new WrongCredentialsException("Bad meter credentials");
        }
        m.setElectricMeter(meter);
        m = measureService.addMeasure(m);
        PostResponse res = new PostResponse(buildURL("api/measures", m.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/web/residences/{residenceId}/usage")
    public ResponseEntity<UsageDTO> findUsageBetweenDatesByResidence(
            @PathVariable Integer residenceId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        UserDTO owner = (UserDTO) auth.getPrincipal();
        Residence r = residenceService.findById(residenceId);
        checkOwner(r.getUser().getId(), owner.getId());
        return ResponseEntity.ok(measureService.findUsageBetweenDatesByResidence(residenceId, from, to));
    }

    @GetMapping("/web/clients/{clientId}/usage")
    @PreAuthorize("#clientId==#auth.principal.id")
    public ResponseEntity<UsageDTO> findUsageBetweenDatesByClient(
            @PathVariable Integer clientId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        return ResponseEntity.ok(measureService.findUsageBetweenDatesByClient(clientId, from, to));
    }

    @GetMapping("/web/residences/{residenceId}/measures")
    public ResponseEntity<List<MeasureDTO>> findMeasuresBetweenDates(
            @PathVariable("residenceId") Integer residenceId,
            Pageable pageable,
            @Join(path = "electricMeter", alias = "e")
            @Join(path = "e.residence", alias = "r")
            @And({
                    @Spec(pathVars = "residenceId", path = "r.id", spec = Equal.class),
                    @Spec(path = "dateTime", params = {"from", "to"}, spec = Between.class)}) Specification<Measure> spec,
            Authentication auth) {
        UserDTO owner = (UserDTO) auth.getPrincipal();
        Residence r = residenceService.findById(residenceId);
        checkOwner(r.getUser().getId(), owner.getId());
        Page<Measure> p = measureService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<MeasureDTO> dtoList = listToDto(modelMapper,p.getContent(),MeasureDTO.class);
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

    @GetMapping("/back-office/measures/{id}")
    public ResponseEntity<MeasureDTO> findMeasureById(@PathVariable Integer id){
        return ResponseEntity.ok(modelMapper.map(measureService.findById(id),MeasureDTO.class));
    }

    private void checkOwner(Integer userId, Integer authId) {
        if (!userId.equals(authId)) {
            throw new AccessDeniedException("Not owned by this user");
        }
    }
}