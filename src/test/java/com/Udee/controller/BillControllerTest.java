package com.Udee.controller;


import com.Udee.models.Bill;
import com.Udee.models.Residence;
import com.Udee.models.User;
import com.Udee.models.UserType;
import com.Udee.models.dto.BillDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.projections.BillProjection;
import com.Udee.service.BillService;
import com.Udee.service.ResidenceService;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static com.Udee.utils.ListMapper.listToDto;

public class BillControllerTest {

    @Mock
    BillService billService;

    @Mock
    ResidenceService residenceService;
    @Mock
    Specification spec;
    @Mock
    Authentication auth;
    Pageable pageable;
    ModelMapper modelMapper;
    BillController billController;
    List<Bill> billList;
    BillController spyController;

    @BeforeEach
    void setUp() {
        openMocks(this);
        pageable = PageRequest.of(0, 10);
        modelMapper = new ModelMapper();
        billController = new BillController(billService, residenceService, modelMapper);
        spyController = spy(billController);
        billList = List.of(Bill.builder().id(1).date(LocalDate.of(2021, 1, 1)).usage(20).build()
                , Bill.builder().id(1).date(LocalDate.of(2021, 2, 1)).usage(100).build());
    }

    @Test
    public void testFindAllByUserWithAccessHttp200() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        List<BillDTO> dtoList = listToDto(modelMapper, billList, BillDTO.class);
        doReturn(ResponseEntity.ok(dtoList)).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByUser(spec, pageable, 1, auth));

        assertEquals(r.getStatusCode(), HttpStatus.OK);
        assertEquals(r.getBody(), dtoList);
    }

    @Test
    public void testFindAllByUserWithAccessHttp204() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        doReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList())).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByUser(spec, pageable, 1, auth));
        assertEquals(r.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(r.getBody(), Collections.emptyList());
    }

    @Test
    public void testFindAllByUserWithoutAccess() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(2).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        doReturn(ResponseEntity.ok("")).when(spyController).getListResponseEntity(pageable, spec);

        assertThrows(AccessDeniedException.class, () -> spyController.findAllByUser(spec, pageable, 1, auth));
    }

    @Test
    public void testFindAllByResidenceWithAccessHttp200() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        when(residenceService.findById(1)).thenReturn(
                Residence.builder().user(
                        User.builder().id(1).build()).build());
        List<BillDTO> dtoList = listToDto(modelMapper, billList, BillDTO.class);
        doReturn(ResponseEntity.ok(dtoList)).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByResidence(pageable, auth, 1, spec));

        assertEquals(r.getStatusCode(), HttpStatus.OK);
        assertEquals(r.getBody(), dtoList);
    }

    @Test
    public void testFindAllByResidenceWithAccessHttp204() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        when(residenceService.findById(1)).thenReturn(
                Residence.builder().user(
                        User.builder().id(1).build()).build());
        doReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList())).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByResidence(pageable, auth, 1, spec));

        assertEquals(r.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(r.getBody(), Collections.emptyList());
    }

    @Test
    public void testFindAllByResidenceWithoutAccess() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.CLIENT).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        when(residenceService.findById(1)).thenReturn(
                Residence.builder().user(
                        User.builder().id(2).build()).build());
        doReturn(ResponseEntity.ok("")).when(spyController).getListResponseEntity(pageable, spec);

        assertThrows(AccessDeniedException.class, () -> spyController.findAllByResidence(pageable, auth, 1, spec));
    }

    @Test
    public void testFindAllByUserBackHttp200() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.EMPLOYEE).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        List<BillDTO> dtoList = listToDto(modelMapper, billList, BillDTO.class);
        doReturn(ResponseEntity.ok(dtoList)).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByUserBack(pageable, spec));

        assertEquals(r.getStatusCode(), HttpStatus.OK);
        assertEquals(r.getBody(), dtoList);
    }

    @Test
    public void testFindAllByUserBackHttp204() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.EMPLOYEE).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        doReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList())).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByUserBack(pageable, spec));

        assertEquals(r.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(r.getBody(), Collections.emptyList());
    }

    @Test
    public void testFindAllByResidenceBackHttp200() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.EMPLOYEE).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        when(residenceService.findById(1)).thenReturn(
                Residence.builder().user(
                        User.builder().id(1).build()).build());
        List<BillDTO> dtoList = listToDto(modelMapper, billList, BillDTO.class);
        doReturn(ResponseEntity.ok(dtoList)).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByResidenceBack(pageable, spec));

        assertEquals(r.getStatusCode(), HttpStatus.OK);
        assertEquals(r.getBody(), dtoList);
    }

    @Test
    public void testFindAllByResidenceBackHttp204() {
        when(auth.getPrincipal()).thenReturn(
                UserDTO.builder()
                        .userType(UserType.EMPLOYEE).id(1).dni(231423423).email("asd@gmail.com").name("pepe").lastName("dasdasf").build()
        );
        when(residenceService.findById(1)).thenReturn(Residence.builder().user(
                User.builder().id(1).build()).build());
        doReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList())).when(spyController).getListResponseEntity(pageable, spec);

        ResponseEntity r = assertDoesNotThrow(() -> spyController.findAllByResidenceBack(pageable, spec));

        assertEquals(r.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(r.getBody(), Collections.emptyList());
    }

    @Test
    public void testGetListResponseEntityFilled() {
        Page<Bill> page = new PageImpl<>(billList, pageable, 2);
        List<BillDTO> dtoList = listToDto(modelMapper, page.getContent(), BillDTO.class);
        when(billService.findAll(spec, pageable)).thenReturn(page);

        ResponseEntity response = billController.getListResponseEntity(pageable, spec);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(response.getBody(), dtoList);
        Assertions.assertEquals(response.getHeaders().get("X-totalElements").stream().findFirst().get(), "2");
        Assertions.assertEquals(response.getHeaders().get("X-totalPages").stream().findFirst().get(), "1");
    }

    @Test
    public void testGetListResponseEntityEmpty() {
        Page<Bill> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        List<BillDTO> dtoList = listToDto(modelMapper, page.getContent(), BillDTO.class);
        when(billService.findAll(spec, pageable)).thenReturn(page);

        ResponseEntity response = billController.getListResponseEntity(pageable, spec);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(response.getBody(), dtoList);
        assertEquals(response.getHeaders().get("X-totalElements").stream().findFirst().get(), "0");
        assertEquals(response.getHeaders().get("X-totalPages").stream().findFirst().get(), "0");
    }

    @Test
    public void testFindById() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        BillProjection projection = factory.createProjection(BillProjection.class);
        Integer billId = 1;
        projection.setId(billId);
        when(billService.findProjectedById(billId)).thenReturn(projection);

        ResponseEntity<BillProjection> r = billController.findById(billId);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(r.getBody(), projection);
        assertEquals(r.getBody().getId(), projection.getId());
    }

    @Test
    public void testCheckOwnerAuth() {
        Integer userId = 1;
        Integer authId = 1;

        assertDoesNotThrow(() -> billController.checkOwner(userId, authId));

    }

    @Test
    public void testCheckOwnerThrows() {
        Integer userId = 1;
        Integer authId = 2;

        assertThrows(AccessDeniedException.class, () -> billController.checkOwner(userId, authId));
    }
}















