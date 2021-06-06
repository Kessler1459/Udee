package com.Udee.controller;

import com.Udee.models.Bill;
import com.Udee.models.Residence;
import com.Udee.models.dto.BillDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.projections.BillProjection;
import com.Udee.service.BillService;
import com.Udee.service.ResidenceService;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.NotNull;
import net.kaczmarzyk.spring.data.jpa.domain.Null;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.ListMapper.listToDto;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api")
public class BillController {

    private final BillService billService;
    private final ResidenceService residenceService;
    private final ModelMapper modelMapper;

    @Autowired
    public BillController(BillService billService,  ResidenceService residenceService, ModelMapper modelMapper) {
        this.billService = billService;
        this.residenceService = residenceService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/web/clients/{idUser}/bills")
    public ResponseEntity<List<BillDTO>> findAllByUser(
            @And({
                    @Spec(pathVars = "idUser", path = "user.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "notPaid", spec = NotNull.class)}) Specification<Bill> spec,
            Pageable pageable,
            @PathVariable("idUser") Integer idUser,
            Authentication auth) {
        checkOwner(idUser, ((UserDTO) auth.getPrincipal()).getId());
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/web/residences/{idResidence}/bills")
    public ResponseEntity<List<BillDTO>> findAllByResidence(
            Pageable pageable,
            Authentication auth,
            @PathVariable("idResidence") Integer idResidence,
            @Join(path = "user", alias = "u")
            @Join(path = "u.residences", alias = "r")
            @And({
                    @Spec(pathVars = "idResidence", path = "r.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "notPaid", spec = NotNull.class)}) Specification<Bill> spec) {
        Residence r = residenceService.findById(idResidence);
        checkOwner(r.getUser().getId(), ((UserDTO) auth.getPrincipal()).getId());
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/back-office/clients/{idUser}/bills")
    public ResponseEntity<List<BillDTO>> findAllByUserBack(
            Pageable pageable,
            @And({
                    @Spec(pathVars = "idUser", path = "user.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "notPaid", spec = NotNull.class)}) Specification<Bill> spec) {
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/back-office/residences/{idResidence}/bills")
    public ResponseEntity<List<BillDTO>> findAllByResidenceBack(
            Pageable pageable,
            @Join(path = "user", alias = "u")
            @Join(path = "u.residences", alias = "r")
            @And({
                    @Spec(pathVars = "idResidence", path = "r.id", spec = Equal.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "payment", params = "notPaid", spec = Null.class)}) Specification<Bill> spec) {
        return getListResponseEntity(pageable, spec);
    }

    @GetMapping("/back-office/bills/{id}")
    public ResponseEntity<BillProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(billService.findProjectedById(id));
    }

    public ResponseEntity<List<BillDTO>> getListResponseEntity(Pageable pageable, Specification<Bill> spec) {
        Page<Bill> p = billService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        List<BillDTO> dtoList = listToDto(modelMapper,p.getContent(),BillDTO.class);
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }

    public void checkOwner(Integer userId, Integer authId) {
        if (!userId.equals(authId)) {
            throw new AccessDeniedException("Not owned by this user");
        }
    }

}
