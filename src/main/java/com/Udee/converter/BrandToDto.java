package com.Udee.converter;

import com.Udee.models.Brand;
import com.Udee.models.dto.BrandDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BrandToDto implements Converter<Brand, BrandDTO> {
    private final ModelMapper modelMapper;

    public BrandToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDTO convert(Brand brand) {
        return modelMapper.map(brand,BrandDTO.class);
    }
}