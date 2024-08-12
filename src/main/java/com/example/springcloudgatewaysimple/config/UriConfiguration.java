package com.example.springcloudgatewaysimple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ouyangcm
 * create 2024/8/12 9:31
 */
@ConfigurationProperties
public class UriConfiguration {

    private String httpbin = "http://httpbin.org:80";

    public String getHttpbin() {
        return httpbin;
    }

    public void setHttpbin(String httpbin) {
        this.httpbin = httpbin;
    }
}
