package com.Udee.converter;

import com.Udee.models.ElectricMeter;
import com.Udee.models.dto.ElectricMeterDTO;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ElectricMeterToDto implements Converter<ElectricMeter, ElectricMeterDTO> {
    private final ModelMapper modelMapper;

    public ElectricMeterToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ElectricMeterDTO convert(ElectricMeter electricMeter) {
        return modelMapper.map(electricMeter,ElectricMeterDTO.class);
    }
}
