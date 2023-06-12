package com.zou.entity;

import com.zou.annotation.Component;
import com.zou.annotation.Value;
import lombok.Data;

@Component("")
@Data
public class User {
    private int id = 2;
    @Value("23")
    private byte name;
}


