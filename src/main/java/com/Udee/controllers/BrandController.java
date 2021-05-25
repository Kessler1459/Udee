package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Brand;
import com.Udee.services.BrandService;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addBrand(@RequestBody Brand brand) {
        final Brand b = brandService.addBrand(brand);
        final PostResponse p = new PostResponse(buildURL("brands", b.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping
    public ResponseEntity<List<Brand>> findAll(Pageable pageable) {
        Page<Brand> list = brandService.findAll(pageable);
        checkPages(list.getTotalPages(), pageable.getPageNumber());
        return ResponseEntity.status(list.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(list.getTotalElements(), list.getTotalPages()))
                .body(list.getContent());
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<Brand>> findByName(@RequestParam String name) {
        List<Brand> list = brandService.findByName(name);
        return ResponseEntity.status(list.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.findById(id));
    }
}
