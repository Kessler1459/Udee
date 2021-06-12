package com.Udee.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class PageHeadersTest {

    @Test
    void testPageHeaders() {
        Long totalElements=2l;
        Integer totalPages=1;
        HttpHeaders result = PageHeaders.pageHeaders(totalElements, totalPages);

        Assertions.assertEquals("2", result.getFirst("X-totalElements"));
        Assertions.assertEquals("1", result.getFirst("X-totalPages"));
    }
}

