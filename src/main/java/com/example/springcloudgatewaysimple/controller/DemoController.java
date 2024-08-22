package com.example.springcloudgatewaysimple.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ouyangcm
 * create 2024/8/9 14:48
 */
@RestController
public class DemoController {

    @RequestMapping("/demo/{serviceId}")
    public String demo(@PathVariable String serviceId){
        return "demo/" + serviceId;
    }
}
