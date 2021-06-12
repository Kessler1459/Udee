package com.Udee.utils;

import com.Udee.models.Measure;
import com.Udee.models.dto.MeasureDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

class ListMapperTest {

    @Test
    void testListToDto() {
        List<MeasureDTO> result = ListMapper.listToDto(new ModelMapper(),List.of(new Measure()), MeasureDTO.class);

        Assertions.assertEquals(1, result.size());
    }
}

