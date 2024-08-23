package com.example.springcloudgatewaysimple.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author ouyangcm
 * create 2024/8/23 13:12
 */
@Component
public class CustomCorsFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomCorsFilterGatewayFilterFactory.Config> {

    public CustomCorsFilterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 处理预检请求
            if (HttpMethod.OPTIONS.equals(request.getMethod())) {
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeaders().getOrigin());
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, config.getAllowedMethods());
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, config.getAllowedHeaders());
                // 浏览器是(true)否允许跨域请求携带凭证信息
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, config.getAllowCredentials());
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }

            // 处理非预检请求
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeaders().getOrigin());
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, config.getAllowedMethods());
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, config.getAllowedHeaders());
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, config.getAllowCredentials());

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private String allowedOrigin = "*";
        private String allowedMethods = "GET, POST, PUT, DELETE, OPTIONS";
        private String allowedHeaders = "*";
        private String allowCredentials = "true";

        // Getters and Setters for these fields

        public String getAllowedOrigin() {
            return allowedOrigin;
        }

        public void setAllowedOrigin(String allowedOrigin) {
            this.allowedOrigin = allowedOrigin;
        }

        public String getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public String getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public String getAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(String allowCredentials) {
            this.allowCredentials = allowCredentials;
        }
    }
}
