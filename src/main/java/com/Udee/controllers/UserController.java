package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.exceptions.WrongCredentialsException;
import com.Udee.models.User;
import com.Udee.models.dto.BillDTO;
import com.Udee.models.dto.LoginResponse;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.dto.UserLoginDTO;
import com.Udee.models.projections.UserProjection;
import com.Udee.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static com.Udee.utils.ListMapper.listToDto;
import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.Constants.JWT_SECRET;
import static com.Udee.utils.EntityUrlBuilder.buildURL;
import static com.Udee.utils.PageHeaders.pageHeaders;


@RestController
@RequestMapping(value = "/api")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/back-office/users")
    public ResponseEntity<PostResponse> addUser(@RequestBody User user) {
        user.setPass(passwordEncoder.encode(user.getPass()));
        user = userService.addUser(user);
        PostResponse p = new PostResponse(buildURL("api/back-office/users", user.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(p.getUrl())).body(p);
    }

    @GetMapping("back-office/clients")
    public ResponseEntity<List<UserDTO>> findAll(Pageable pageable, @And({
            @Spec(path = "name", spec = Like.class),
            @Spec(path = "lastName", spec = Like.class),
            @Spec(path = "email", spec = Like.class)}) Specification<User> spec) {
        final Page<User> p = userService.findAll(spec, pageable);
        checkPages(p.getTotalPages(), pageable.getPageNumber());
        final List<UserDTO> list = listToDto(modelMapper,p.getContent(), UserDTO.class);
        return ResponseEntity.status(list.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .headers(pageHeaders(p.getTotalElements(), p.getTotalPages()))
                .body(list);
    }

    @GetMapping("/back-office/clients/{id}")
    public ResponseEntity<UserProjection> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findProjectedById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDTO userDTO) {
        User user = userService.findByEmail(userDTO.getEmail());
        if (user == null || !(passwordEncoder.matches(userDTO.getPass().trim(), user.getPass()))){
            throw new WrongCredentialsException("Bad user credentials");
        }
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(new LoginResponse(this.generateToken(dto, user.getUserType().getType())));
    }

    private String generateToken(UserDTO userDto, String authority) {
        try {
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
            return Jwts
                    .builder()
                    .setId("JWT")
                    .setSubject(userDto.getEmail())
                    .claim("user", objectMapper.writeValueAsString(userDto))
                    .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 100000000))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes()).compact();
        } catch (Exception e) {
            return "dummy";
        }
    }
}
