package com.example.springcloudgatewaysimple;

import com.example.springcloudgatewaysimple.config.UriConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class SpringCloudGatewaySimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewaySimpleApplication.class, args);
    }

    @Value("${remote.home:http://httpbin.org}")
    private String host;

    @GetMapping("/test")
    public Mono<ResponseEntity<byte[]>> proxy(ProxyExchange<byte[]> proxy) throws Exception {
        return proxy.uri(host+ "/get").get();
    }
}
