package com.Udee.converter;

import com.Udee.models.Bill;
import com.Udee.models.dto.BillDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BillToDto implements Converter<Bill, BillDTO> {
    private final ModelMapper modelMapper;

    public BillToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public BillDTO convert(Bill bill) {
        return modelMapper.map(bill,BillDTO.class);
    }
}