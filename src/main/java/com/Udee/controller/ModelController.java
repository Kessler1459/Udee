package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.Model;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.dto.ModelDTO;
import com.Udee.service.ModelService;
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
@RequestMapping("/api/back-office/electricmeters/brands/models")
public class ModelController {
    private final ModelService modelService;
    private final ModelMapper modelMapper;

    @Autowired
    public ModelController(ModelService modelService, ModelMapper modelMapper) {
        this.modelService = modelService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addModel(@RequestBody Model model){
        Model m=modelService.addModel(model);
        PostResponse p = new PostResponse(
                buildURL("api/back-office/electricmeters/models", m.getId().toString()),
                HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping
    public ResponseEntity<List<ModelDTO>> findAll(
            @And({
                    @Spec(path = "name", spec = StartingWith.class),
                    @Spec(path = "brand.name",params = "brand", spec = StartingWith.class)
            }) Specification<Model> spec, Pageable pageable) {
        Page<Model> p = modelService.findAll(spec,pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<ModelDTO> dtoList = listToDto(modelMapper,p.getContent(), ModelDTO.class);
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelDTO> findById(@PathVariable Integer id){
        return ResponseEntity.ok(modelMapper.map(modelService.findById(id), ModelDTO.class));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<MessageDTO> deleteModel(@PathVariable Integer id){
        modelService.delete(id);
        return ResponseEntity.ok(new MessageDTO("Model has been deleted"));
    }

}
