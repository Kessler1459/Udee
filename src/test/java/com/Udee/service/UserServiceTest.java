package com.Udee.service;

import com.Udee.repository.UserRepository;
import com.Udee.services.UserService;
import com.Udee.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void findById(){
        //given  requisitos
        Integer id=1;

        //when   situaciones
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(TestUtils.aUser()));

        userService.findById(id);
        //then
        Mockito.verify(userRepository,Mockito.times(1))
                .findById(id);
    }
}
