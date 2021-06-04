package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.ElectricMeter;
import com.Udee.models.dto.ElectricMeterDTO;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.services.ElectricMeterService;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api/back-office/electricmeters")
public class ElectricMeterController {
    private final ElectricMeterService electricMeterService;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ElectricMeterController(ElectricMeterService electricMeterService, ConversionService conversionService, PasswordEncoder passwordEncoder) {
        this.electricMeterService = electricMeterService;
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addElectricMeter(@RequestBody ElectricMeter electricMeter) {
        electricMeter.setPass(passwordEncoder.encode(electricMeter.getPass()));
        electricMeter = electricMeterService.addElectricMeter(electricMeter);
        PostResponse p = new PostResponse(
                buildURL("api/back-office/electricmeters", electricMeter.getId().toString()),
                HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    //todo revisar findbyid con no content bills
    @GetMapping
    public ResponseEntity<List<ElectricMeterDTO>> findAll(
            @Join(path = "model", alias = "m", type = JoinType.LEFT)
            @Join(path = "m.brand", alias = "br")
            @And({
                    @Spec(path = "serial", spec = StartingWith.class),
                    @Spec(path = "m.name", params = "model", spec = StartingWith.class),
                    @Spec(path = "br.name", params = "brand", spec = StartingWith.class)
            }) Specification<ElectricMeter> spec, Pageable pageable) {
        Page<ElectricMeter> p = electricMeterService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<ElectricMeterDTO> dtoList = p.stream().map(meter -> conversionService.convert(meter, ElectricMeterDTO.class)).collect(Collectors.toList());
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectricMeterProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(electricMeterService.findProjectionById(id));
    }

    @PutMapping("/{elId}/model/{modelId}")
    public ResponseEntity<ElectricMeterDTO> setModelToElectricMeter(@PathVariable Integer elId, @PathVariable Integer modelId) {
        ElectricMeter electricMeter = electricMeterService.setModelToElectricMeter(elId, modelId);
        return ResponseEntity.ok(conversionService.convert(electricMeter, ElectricMeterDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteMeter(@PathVariable Integer id) {
        electricMeterService.delete(id);
        return ResponseEntity.ok(new MessageDTO("Meter has been deleted"));
    }

}
