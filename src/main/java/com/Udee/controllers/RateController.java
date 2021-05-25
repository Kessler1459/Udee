package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Rate;
import com.Udee.services.RateService;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rates")
public class RateController {
    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping
    public PostResponse addRate(@RequestBody Rate r){
        r=rateService.addRate(r);
        return new PostResponse(buildURL("rates",r.getId().toString()),HttpStatus.CREATED.getReasonPhrase());
    }

    @GetMapping
    public ResponseEntity<List<Rate>> findAll(Pageable pageable){
        Page<Rate> p= rateService.findAll(pageable);
        checkPages(p.getTotalPages(),pageable.getPageNumber());
        return ResponseEntity.status(p.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(p.getContent());
    }

    @GetMapping("name")
    public ResponseEntity<List<Rate>> findAllByName(Pageable pageable,@RequestParam String name){
        Page<Rate> p= rateService.findAllByName(pageable,name);
        checkPages(p.getTotalPages(),pageable.getPageNumber());
        return ResponseEntity.status(p.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(p.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rate> findById(@PathVariable Integer id){
        return ResponseEntity.ok(rateService.findById(id));
    }
    //todo
    /*
    @PutMapping("/{id}")
    public ResponseEntity<Rate>
*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(@PathVariable Integer id){
        rateService.deleteRate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
