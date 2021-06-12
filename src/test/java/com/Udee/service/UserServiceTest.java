package com.Udee.service;

import com.Udee.exception.notFound.UserNotFoundException;
import com.Udee.models.User;
import com.Udee.models.projections.UserProjection;
import com.Udee.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.List;
import java.util.Optional;


public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private User u;

    @BeforeEach
    void setUp() {
        openMocks(this);
        userService=new UserService(userRepository);
        u = User.builder().id(1).email("asd@gmail.com").build();
    }

    @Test
    public void testFindByIdFound() {
        Integer id = 1;
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(u));

        User result = userService.findById(id);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Integer id = 1;
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(id));

        verify(userRepository, Mockito.times(1))
                .findById(id);
    }

    @Test
    public void testFindProjectedByIdFound() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        Integer id = 1;
        Mockito.when(userRepository.findProjectedById(anyInt()))
                .thenReturn(Optional.of(factory.createProjection(UserProjection.class, u)));

        UserProjection result = userService.findProjectedById(id);

        Mockito.verify(userRepository, Mockito.times(1))
                .findProjectedById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void testFindProjectedByIdNotFound() {
        Integer id = 1;
        Mockito.when(userRepository.findProjectedById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findProjectedById(id));

        Mockito.verify(userRepository, Mockito.times(1))
                .findProjectedById(id);
    }

    @Test
    public void testFindByEmailFound() {
        Mockito.when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(u));

        User result = userService.findByEmail(u.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(anyString());
        assertEquals(u.getEmail(), result.getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        Mockito.when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        User result = userService.findByEmail(u.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(anyString());
        assertNull(result);
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(u)).thenReturn(u);

        User result = userService.addUser(u);

        assertEquals(u.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> p = new PageImpl<>(List.of(u), pageable, 1);
        Specification<User> spec = mock(Specification.class);
        when(userRepository.findAll(spec, pageable)).thenReturn(p);

        Page<User> result = userService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
