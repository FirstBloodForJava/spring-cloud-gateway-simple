package com.example.springcloudgatewaysimple.filter.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ElapsedFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ElapsedFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("ElapsedFilter start");
        long start = System.currentTimeMillis();

        /*return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("ElapsedFilter end, time {}", System.currentTimeMillis() - start);
        }));*/

        return chain.filter(exchange).doFinally(signalType -> {
            // 客户端断开连接也能记录
            logger.info("singleType: {}", signalType);
            logger.info("ElapsedFilter end, time {}", System.currentTimeMillis() - start);
        }).then(Mono.fromRunnable(() -> {
            logger.info("then");
        }));

    }

    @Override
    public int getOrder() {
        return -2;
    }
}
