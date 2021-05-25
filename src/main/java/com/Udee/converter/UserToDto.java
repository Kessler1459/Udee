package com.Udee.converter;


import com.Udee.models.User;
import com.Udee.models.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToDto implements Converter<User, UserDTO> {
    private final ModelMapper modelMapper;

    public UserToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO convert(User user) {
        return modelMapper.map(user,UserDTO.class);
    }
}
