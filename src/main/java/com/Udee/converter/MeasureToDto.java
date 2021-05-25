package com.Udee.converter;

import com.Udee.models.Measure;
import com.Udee.models.dto.MeasureDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MeasureToDto implements Converter<Measure, MeasureDTO> {
    private final ModelMapper modelMapper;

    public MeasureToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MeasureDTO convert(Measure measure) {
        return modelMapper.map(measure,MeasureDTO.class);
    }
}
