package com.Udee.converter;

import com.Udee.models.Residence;
import com.Udee.models.dto.ResidenceDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResidenceToDto implements Converter<Residence, ResidenceDTO> {
    private final ModelMapper modelMapper;

    public ResidenceToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ResidenceDTO convert(Residence residence) {
        return modelMapper.map(residence,ResidenceDTO.class);
    }
}
