package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.Bill;
import com.Udee.models.Payment;
import com.Udee.models.dto.PaymentDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.service.BillService;
import com.Udee.service.PaymentService;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
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
import org.springframework.web.bind.annotation.*;
import static com.Udee.utils.ListMapper.listToDto;
import java.net.URI;
import java.util.List;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    private final BillService billService;

    @Autowired
    public PaymentController(PaymentService paymentService, ModelMapper modelMapper, BillService billService) {
        this.paymentService = paymentService;
        this.modelMapper = modelMapper;
        this.billService = billService;
    }

    @PostMapping("/web/bills/{billId}/payments")
    public ResponseEntity<PostResponse> addPayment(@PathVariable Integer billId, @RequestBody Payment payment, Authentication auth) {
        Bill b=billService.findById(billId);
        if (!b.getUser().getId().equals(((UserDTO)auth.getPrincipal()).getId())){
            throw new AccessDeniedException("Not owned by this user");
        }
        payment = paymentService.addPayment(b, payment);
        PostResponse res = new PostResponse(buildURL("payments", payment.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/back-office/payments/{id}")
    public ResponseEntity<PaymentDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(modelMapper.map(paymentService.findById(id), PaymentDTO.class));
    }

    @GetMapping("/back-office/payments")
    public ResponseEntity<List<PaymentDTO>> findAll(
            @Join(path = "bill", alias = "b")
            @Join(path = "b.user", alias = "u")
            @And({
                    @Spec(path = "amount", params = {"min", "max"}, spec = Between.class),
                    @Spec(path = "date", params = {"from", "to"}, spec = Between.class),
                    @Spec(path = "u.name",params = "name", spec = StartingWith.class),
                    @Spec(path = "lastName", spec = StartingWith.class)
            }) Specification<Payment> spec, Pageable pagination
    ) {
        Page<Payment> p = paymentService.findAll(spec, pagination);
        checkPages(p.getTotalPages(), pagination.getPageNumber());
        List<PaymentDTO> dtoList= listToDto(modelMapper,p.getContent(), PaymentDTO.class);
        return ResponseEntity.status(dtoList.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(dtoList);
    }
}
