package com.Udee.converter;

import com.Udee.models.Payment;
import com.Udee.models.dto.PaymentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentToDto implements Converter<Payment, PaymentDTO> {
    private final ModelMapper modelMapper;

    public PaymentToDto(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PaymentDTO convert(Payment payment) {
        return modelMapper.map(payment,PaymentDTO.class);
    }
}
