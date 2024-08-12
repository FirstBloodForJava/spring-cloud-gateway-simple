package com.example.springcloudgatewaysimple.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Configuration;

/**
 * @author ouyangcm
 * create 2024/8/9 16:08
 */
@Configuration
public class BeanConfig {

    /**
     * 将/get请求转发到http://httpbin.org:80/get
     * @param builder
     * @return
     */
    //@Bean
    public RouteLocator myRoutes1(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri("http://httpbin.org:80"))
                .build();
    }

    /**
     * 超时时间多久?
     * @param builder
     * @return
     */
    //@Bean
    public RouteLocator myRoutes2(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p
                        .host("*.circuitbreaker.com")
                        .filters(f -> f.circuitBreaker(config -> config.setName("mycmd")))
                        .uri("http://httpbin.org:80")).
                build();
    }

    //@Bean
    public RouteLocator myRoutes3(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p
                        .host("*.circuitbreaker.com")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("mycmd")
                                .setFallbackUri("forward:/demo")))
                        .uri("http://httpbin.org:80"))
                .build();
    }

    //@Bean
    public RouteLocator myRoutes4(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        String httpUri = uriConfiguration.getHttpbin();
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri(httpUri))
                .route(p -> p
                        .host("*.circuitbreaker.com")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("mycmd")
                                        .setFallbackUri("forward:/demo")))
                        .uri(httpUri))
                .build();
    }

}
