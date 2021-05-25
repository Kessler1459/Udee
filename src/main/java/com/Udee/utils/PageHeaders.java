package com.Udee.utils;

import org.springframework.http.HttpHeaders;


public class PageHeaders {
    public static HttpHeaders pageHeaders(Long totalElements,Integer totalPages) {
        HttpHeaders headers=new HttpHeaders();
        headers.add("X-totalElements",totalElements.toString());
        headers.add("X-totalPages",totalPages.toString());
        return headers;
    }
}
