package com.Udee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResponse {
    private String url;
    private String status;
}
