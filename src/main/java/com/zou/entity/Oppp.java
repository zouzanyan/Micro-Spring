package com.zou.entity;

import com.zou.annotation.Autowired;
import com.zou.annotation.Component;
import com.zou.annotation.Qualifier;
import lombok.Data;

@Component
@Data
public class Oppp {
    private int id;
    private String name;
    @Autowired
    private User user;
}
