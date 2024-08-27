package com.example.springcloudgatewaysimple.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author ouyangcm
 * create 2024/8/26 15:30
 */
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {

    private static final String MATCH_TRAILING_SLASH = "matchTrailingSlash";

    public MyRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        // 获取配置判断是否route
        return exchange -> {
            // 获取请求的信息
            ServerHttpRequest request = exchange.getRequest();
            // 后续做判断 请求是否和配置的信息匹配
            return false;
        };
    }

    public static class Config {
        // 把配置过滤的信息配置在这里

        private List<String> patterns = new ArrayList<>();

        //
        private boolean matchTrailingSlash = true;

        public List<String> getPatterns() {
            return patterns;
        }

        public MyRoutePredicateFactory.Config setPatterns(List<String> patterns) {
            this.patterns = patterns;
            return this;
        }

        public boolean isMatchTrailingSlash() {
            return matchTrailingSlash;
        }

        public MyRoutePredicateFactory.Config setMatchTrailingSlash(boolean matchTrailingSlash) {
            this.matchTrailingSlash = matchTrailingSlash;
            return this;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("patterns", patterns)
                    .append(MATCH_TRAILING_SLASH, matchTrailingSlash).toString();
        }
    }

}
