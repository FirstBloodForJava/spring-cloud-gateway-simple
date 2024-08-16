package com.example.springcloudgatewaysimple.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * @author ouyangcm
 * create 2024/8/16 9:20
 */
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null) {
            log.info("custom global filter, address: " + remoteAddress.getAddress() + ", port: " + remoteAddress.getPort());
        }else {
            log.info("custom global filter, remoteAddress is null" );
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
