package com.Udee.converter;

import com.Udee.models.Model;
import com.Udee.models.dto.ModelDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ModelToDto implements Converter<Model, ModelDTO> {
    private final ModelMapper modelMapper;

    public ModelToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDTO convert(Model model) {
        return modelMapper.map(model,ModelDTO.class);
    }
}
