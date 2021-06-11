package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.exception.WrongCredentialsException;
import com.Udee.models.User;
import com.Udee.models.UserType;
import com.Udee.models.dto.LoginResponse;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.dto.UserLoginDTO;
import com.Udee.models.projections.UserProjection;
import com.Udee.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Collections;
import java.util.List;
import static com.Udee.utils.Constants.JWT_SECRET;
import static com.Udee.utils.ListMapper.listToDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Specification specification;
    private UserController userController;
    private List<User> userList;
    private User user;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        modelMapper = new ModelMapper();
        userController = new UserController(userService, modelMapper,objectMapper , passwordEncoder);
        userList = List.of(User.builder().id(1).dni(234234525).userType(UserType.CLIENT).email("asd@gmail.com").pass("passasdafg").build(),
                User.builder().id(2).dni(234245525).userType(UserType.CLIENT).email("pepe@gmail.com").pass("dfhghdfgd").build());
        user = User.builder().id(1).dni(234234525).userType(UserType.CLIENT).email("asd@gmail.com").pass("aasdfgdfgf").build();
    }

    @Test
    public void testAddUser() {
        String pass = "contraseña123";
        User u = User.builder().id(1).dni(234234525).userType(UserType.CLIENT).email("asd@gmail.com").pass(pass).build();
        when(userService.addUser(any(User.class))).thenReturn(u);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        PostResponse pr = new PostResponse("http://localhost/api/back-office/users/1", HttpStatus.CREATED.getReasonPhrase());

        ResponseEntity<PostResponse> result = userController.addUser(u);

        assertNotEquals(pass, u.getPass());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(pr, result.getBody());
    }

    @Test
    public void testFindAllUsersHttp200() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = new PageImpl<>(userList, pageable, 2);
        List<UserDTO> dtoList = listToDto(modelMapper, page.getContent(), UserDTO.class);
        when(userService.findAll(specification, pageable)).thenReturn(page);

        ResponseEntity<List<UserDTO>> result = userController.findAll(pageable, specification);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dtoList, result.getBody());
    }

    @Test
    public void testFindAllUsersHttp204() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(userService.findAll(specification, pageable)).thenReturn(page);

        ResponseEntity<List<UserDTO>> result = userController.findAll(pageable, specification);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals(Collections.emptyList(), result.getBody());
    }

    @Test
    public void testFindById() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        UserProjection projection = factory.createProjection(UserProjection.class, user);
        when(userService.findProjectedById(anyInt())).thenReturn(projection);

        ResponseEntity<UserProjection> result = userController.findById(1);

        assertEquals(projection, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testLoginHttpOK() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserController spy = spy(userController);
        doReturn("token").when(spy).generateToken(modelMapper.map(user, UserDTO.class), "CLIENT");

        ResponseEntity<LoginResponse> result = assertDoesNotThrow(() -> spy.login(new UserLoginDTO("asd@gmail.com", "passjejejaja")));

        assertEquals("token", result.getBody().getToken());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testLoginHttpUnauthorizedBadEmail() {
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        UserController spy = spy(userController);
        doReturn("token").when(spy).generateToken(modelMapper.map(user, UserDTO.class), "CLIENT");

        assertThrows(WrongCredentialsException.class, () -> spy.login(new UserLoginDTO("asd@gmail.com", "passjejejaja")));
        verify(userService,times(1)).findByEmail(anyString());
    }

    @Test
    public void testLoginHttpUnauthorizedBadPass() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        UserController spy = spy(userController);
        doReturn("token").when(spy).generateToken(modelMapper.map(user, UserDTO.class), "CLIENT");

        assertThrows(WrongCredentialsException.class, () -> spy.login(new UserLoginDTO("asd@gmail.com", "passjejejaja")));
        verify(userService,times(1)).findByEmail(anyString());
    }

    @Test
    public void testLoginHttpUnauthorizedBadBoth() {
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserController spy = spy(userController);
        doReturn("token").when(spy).generateToken(modelMapper.map(user, UserDTO.class), "CLIENT");

        assertThrows(WrongCredentialsException.class, () -> spy.login(new UserLoginDTO("asd@gmail.com", "passjejejaja")));
        verify(userService,times(1)).findByEmail(anyString());
    }

    @Test
    public void testGenerateToken(){
        JwtParser jwtParser=Jwts.parser();
        UserDTO dto=modelMapper.map(user, UserDTO.class);
        String auth="CLIENT";
        try(MockedStatic<AuthorityUtils> utils=mockStatic(AuthorityUtils.class)){
            when(AuthorityUtils.commaSeparatedStringToAuthorityList(auth)).thenReturn(List.of(new SimpleGrantedAuthority(auth)));
        }

        String result=userController.generateToken(dto,auth);

        Claims claims=jwtParser.setSigningKey(JWT_SECRET.getBytes()).parseClaimsJws(result).getBody();
        assertEquals(List.of(auth),claims.get("authorities"));
        assertEquals(user.getEmail(),claims.getSubject());
        assertEquals("JWT",claims.getId());
    }

    @Test
    public void testGenerateTokenThrowsException() throws JsonProcessingException {
        UserDTO dto=modelMapper.map(user, UserDTO.class);
        String auth="CLIENT";
        when(objectMapper.writeValueAsString(dto)).thenThrow(JsonProcessingException.class);
        try(MockedStatic<AuthorityUtils> utils=mockStatic(AuthorityUtils.class)){
            when(AuthorityUtils.commaSeparatedStringToAuthorityList(auth)).thenReturn(List.of(new SimpleGrantedAuthority(auth)));
        }

        String result=userController.generateToken(dto,auth);

        assertNull(result);
    }
}
