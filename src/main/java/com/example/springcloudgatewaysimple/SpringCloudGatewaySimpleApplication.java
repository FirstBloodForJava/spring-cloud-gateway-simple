package com.example.springcloudgatewaysimple;

import com.example.springcloudgatewaysimple.config.UriConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
public class SpringCloudGatewaySimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewaySimpleApplication.class, args);
    }

}
