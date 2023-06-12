package com.zou;

import com.zou.annotation.Component;
import com.zou.core.AnnotationConfigApplicationContext;


public class Test {
    private int id;
    private String name = "ok";
}

class Test2 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext a = new AnnotationConfigApplicationContext("com.zou.entity");
//        System.out.println(a.getBean("qwe"));
//        System.out.println(a.getBeanDefinitionNames());
        System.out.println(a.getBeans());
    }
}