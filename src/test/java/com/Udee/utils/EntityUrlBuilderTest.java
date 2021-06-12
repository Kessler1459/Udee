package com.Udee.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class EntityUrlBuilderTest {

    @Test
    void testBuildURL() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String result = EntityUrlBuilder.buildURL("entity", "1");

        Assertions.assertEquals("http://localhost/entity/1", result);
    }
}

