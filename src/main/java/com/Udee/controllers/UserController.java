package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.User;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.projections.UserProjection;
import com.Udee.services.UserService;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;
import static com.Udee.utils.EntityUrlBuilder.buildURL;

import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "api/")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, ConversionService conversionService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.conversionService = conversionService;
    }

    @PostMapping(value = {"clients", "back-office"})
    public ResponseEntity<PostResponse> addUser(@RequestBody User user, @RequestHeader("Authorization") String pass) {
        user.setPass(passwordEncoder.encode(pass));
        user = userService.addUser(user);
        PostResponse p = new PostResponse(buildURL("users", user.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping("back-office/clients")
    public ResponseEntity<List<UserDTO>> findAll(Pageable pageable, @And({
            @Spec(path = "name", spec = Like.class),
            @Spec(path = "lastName", spec = Like.class),
            @Spec(path = "email", spec = Like.class)}) Specification<User> spec) {
        final Page<User> p = userService.findAll(spec,pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        final List<UserDTO> list=p.stream().map(user -> conversionService.convert(user,UserDTO.class)).collect(Collectors.toList());
        return ResponseEntity.status(list.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(list);
    }

    //todo agregar editar endpointD

    @GetMapping("/back-office/clients/{id}")
    public ResponseEntity<UserProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findProjectedById(id));
    }


}
