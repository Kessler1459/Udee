package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.Residence;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.dto.ResidenceDTO;
import com.Udee.models.projections.ResidenceProjection;
import com.Udee.service.ResidenceService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.Udee.utils.ListMapper.listToDto;
import java.net.URI;
import java.util.List;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api/back-office/residences")
public class ResidenceController {
    private final ResidenceService residenceService;
    private final ModelMapper modelMapper;

    @Autowired
    public ResidenceController(ResidenceService residenceService,  ModelMapper modelMapper) {
        this.residenceService = residenceService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<ResidenceDTO>> findAll(Pageable pageable, @And({
            @Spec(path = "electricMeter.serial", params = "electricMeter", spec = Like.class),
            @Spec(path = "address.street", params = "street", spec = Like.class),
            @Spec(path = "address.num", params = "number", spec = Like.class),
            @Spec(path = "rate.name", params = "rate", spec = Equal.class)}) Specification<Residence> spec) {
        final Page<Residence> p = residenceService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        final List<ResidenceDTO> list = listToDto(modelMapper,p.getContent(), ResidenceDTO.class);
        return ResponseEntity.status(p.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(list);
    }

    @PostMapping
    public ResponseEntity<PostResponse> addResidence(@RequestBody Residence r) {
        r = residenceService.addResidence(r);
        final PostResponse res = new PostResponse(buildURL("api/back-office/residences", r.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidenceProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(residenceService.findProjectionById(id));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<MessageDTO> deleteResidence(@PathVariable Integer id){
        residenceService.delete(id);
        return ResponseEntity.ok(new MessageDTO("Residence has been deleted"));
    }

    @PutMapping("/{residenceId}/electricMeter/{meterId}")
    public ResponseEntity<ResidenceDTO> addElectricMeter(@PathVariable Integer residenceId, @PathVariable Integer meterId) {
        final Residence r = residenceService.addElectricMeter(residenceId, meterId);
        return ResponseEntity.ok(modelMapper.map(r, ResidenceDTO.class));
    }

    @PutMapping("/{residenceId}/rate/{rateId}")
    public ResponseEntity<ResidenceDTO> addRate(@PathVariable Integer residenceId, @PathVariable Integer rateId) {
        final Residence r = residenceService.addRate(residenceId, rateId);
        return ResponseEntity.ok(modelMapper.map(r, ResidenceDTO.class));
    }

    @PutMapping("/{residenceId}/user/{userId}")
    public ResponseEntity<ResidenceDTO> addUser(@PathVariable Integer residenceId, @PathVariable Integer userId) {
        final Residence r = residenceService.addUser(residenceId, userId);
        return ResponseEntity.ok(modelMapper.map(r, ResidenceDTO.class));
    }
}
