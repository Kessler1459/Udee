package com.Udee.controller;


import com.Udee.PostResponse;
import com.Udee.models.Brand;
import com.Udee.models.Model;
import com.Udee.models.dto.BrandDTO;
import com.Udee.models.dto.MessageDTO;
import com.Udee.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import static com.Udee.utils.ListMapper.listToDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;


public class BrandControllerTest {

    @Mock
    BrandService brandService;
    @Mock
    Specification specification;
    BrandController brandController;
    ModelMapper modelMapper;
    List<Brand> brandList;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        modelMapper = new ModelMapper();
        brandController = new BrandController(brandService, modelMapper);
        brandList = List.of(Brand.builder().id(1).name("marca1").build(), Brand.builder().id(2).name("marca2").build());
    }

    @Test
    public void testAddBrand() {
        Brand newBrand = Brand.builder().id(1).name("marca").build();
        when(brandService.addBrand(newBrand)).thenReturn(newBrand);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<PostResponse> result = brandController.addBrand(newBrand);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(brandService, times(1)).addBrand(newBrand);

    }

    @Test
    public void testFindAllHttp200() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Brand> page = new PageImpl<Brand>(brandList, pageable, 2);
        when(brandService.findAll(specification, pageable)).thenReturn(page);
        List<BrandDTO> dtoList = listToDto(modelMapper, page.getContent(), BrandDTO.class);
        ResponseEntity exp = ResponseEntity.status(HttpStatus.OK).body(dtoList);

        ResponseEntity result = brandController.findAll(specification, pageable);

        assertEquals(exp.getBody(), result.getBody());
        assertEquals(exp.getStatusCode(), result.getStatusCode());
        verify(brandService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindAllHttp204() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Brand> page = new PageImpl<Brand>(Collections.emptyList(), pageable, 0);
        when(brandService.findAll(specification, pageable)).thenReturn(page);
        ResponseEntity exp = ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());

        ResponseEntity result = brandController.findAll(specification, pageable);

        assertEquals(exp.getBody(), result.getBody());
        assertEquals(exp.getStatusCode(), result.getStatusCode());
        verify(brandService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindById(){
        Integer id=1;
        Brand b=Brand.builder().id(id).name("marca1").build();
        when(brandService.findById(anyInt())).thenReturn(b);

        ResponseEntity<Brand> response=brandController.findById(id);

        assertEquals(b,response.getBody());
        assertEquals(b.getId(),response.getBody().getId());
        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(brandService, times(1)).findById(id);
    }

    @Test
    public void testAddModelToBrand(){
        Brand b=new Brand(1,"marca1");
        Model m=Model.builder().id(2).build();
        b.addModel(m);
        when(brandService.addModelToBrand(1,2)).thenReturn(b);

        ResponseEntity<Brand> response=brandController.addModelToBrand(1,2);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(1,response.getBody().getId());
        assertEquals(2,response.getBody().getModels().stream().findFirst().get().getId());
    }

    @Test
    public void testDeleteBrand(){
        doNothing().when(brandService).delete(anyInt());
        ResponseEntity<MessageDTO> response=brandController.deleteBrand(anyInt());
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }


}
