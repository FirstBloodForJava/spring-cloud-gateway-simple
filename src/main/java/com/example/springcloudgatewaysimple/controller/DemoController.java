package com.example.springcloudgatewaysimple.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ouyangcm
 * create 2024/8/9 14:48
 */
@RestController
public class DemoController {

    @GetMapping("/demo")
    public static String demo(){
        return "demo";
    }
}
