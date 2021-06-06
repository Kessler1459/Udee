package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.Brand;
import com.Udee.models.dto.BrandDTO;
import com.Udee.models.dto.MessageDTO;
import com.Udee.service.BrandService;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/back-office/electricmeters/brands")
public class BrandController {
    private final BrandService brandService;
    private final ModelMapper modelMapper;

    @Autowired
    public BrandController(BrandService brandService, ModelMapper modelMapper) {
        this.brandService = brandService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addBrand(@RequestBody Brand brand) {
        final Brand b = brandService.addBrand(brand);
        final PostResponse p = new PostResponse(buildURL("api/back-office/electricmeters/brands", b.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping
    public ResponseEntity<List<BrandDTO>> findAll(@And({
            @Spec(path = "name", spec = StartingWith.class),
            @Spec(path = "model.name", params = "model", spec = StartingWith.class)
    }) Specification<Brand> spec, Pageable pageable) {
        Page<Brand> list = brandService.findAll(spec, pageable);
        checkPages(list.getTotalPages(), pageable.getPageNumber());
        List<BrandDTO> dtoList = listToDto(modelMapper,list.getContent(), BrandDTO.class);
        return ResponseEntity.status(list.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(list.getTotalElements(), list.getTotalPages()))
                .body(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.findById(id));
    }

    @PutMapping("/{id}/models/{modelId}")
    public ResponseEntity<Brand> addModelToBrand(@PathVariable Integer id,@PathVariable Integer modelId){
        return ResponseEntity.ok(brandService.addModelToBrand(id,modelId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteBrand(@PathVariable Integer id){
        brandService.delete(id);
        return ResponseEntity.ok(new MessageDTO("Brand has been deleted"));
    }
}
