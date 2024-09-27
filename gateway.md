# gateway

不同版本文档：https://docs.spring.io/spring-cloud-gateway/docs/

当前文档版本：https://docs.spring.io/spring-cloud-gateway/docs/3.1.0/reference/html/

~~~pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

~~~

gateway的特性：

1. 基于Spring框架和SpringBoot构建；
2. 能够匹配任何请求属性上的路由；
3. Predicates和filters可以基于路由定制；
4. 集成断路器；
5. 集成SpringCloud发现客户端；
6. 简单编写Predicates和filters；
7. 请求速率限制；
8. 路劲重写。

![image-20240812101839801](http://47.101.155.205/image-20240812101839801.png)

## 1.构建一个spring-cloud-gateway项目

https://start.spring.io/

根据所需选择依赖

![image-20240812103904652](http://47.101.155.205/image-20240812103904652.png)

可能有一些同步库的依赖不能使用(例如redis)，因为这个是基于SpringBoot2.x版本、SpringWebFlux、Project Reactor构建的。

~~~bash
# 下载依赖的源码
mvn dependency:resolve -Dclassifier=sources

~~~





## 2.名词解释

1. Route：网关的基本构建块。由id(String)、目的uri(URI)、集合predicate(PredicateDefinition)、集合filters(FilterDefinition)，predicate执行结果为true则route匹配。
2. Predicate：java.util.function.Predicate<T>函数式接口。泛型为ServerWebExchange。这可以匹配http请求中的任何东西，例如请求头或参数。
3. Filter：特定工厂建造的GatewayFilter的实现Bean。你可以在这里修改request和response在发送请求之后或之后。



## 3.工作原理

![image-20240812111953557](http://47.101.155.205/image-20240812111953557.png)

http请求到gateway，Gateway Handlr Mapping确定请求和route匹配，则会把请求交给Gateway Web Handler处理。这个处理器按特定的filter链处理请求。过滤器可以在发送代理请求之前和之后处理。所有的pre Filter被执行完成，发出代理请求，在代理请求被处理之后，post Filter被执行。



## 4.配置Route Predicate Factories和Gateway Filter Factories



### 4.1.Shortcut Configuration

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: after_route
          uri: http://httpbin.org:80
          predicates:
            - Cookie=mycookie,mycookievalue
            
~~~

![image-20240812133032549](http://47.101.155.205/image-20240812133032549.png)



### 4.2.Fully Expanded Arguments

与快捷配置不同的地方在于predicate的配置

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: after_route
          uri: http://httpbin.org
          predicates:
            - name: Cookie
              args:
                name: mycookie
                regexp: mycookievalue

~~~



## 5.Route Predicate工厂

有许多内置的Predicate工厂。这些工厂是为了匹配Http请求的不同属性。可以将多个Predicate工厂与and组合在一起。

### 5.1.After

代理2024-08-12T13:42:13.485+08:00[Asia/Shanghai]之后的所有请求。时间式ZonedDateTime类型的格式。

~~~yml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: http://httpbin.org
        predicates:
        - After=2024-08-12T13:42:13.485+08:00[Asia/Shanghai]

~~~



~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: after_route
          uri: http://httpbin.org
          predicates:
            - name: After
              args:
                datetime: 2024-08-12T13:42:13.485+08:00[Asia/Shanghai]

~~~



### 5.2.Before

匹配时间之前的。

~~~yml
spring:
  cloud:
    gateway:
      routes:
      - id: before_route
        uri: http://httpbin.org
        predicates:
        - Before=2024-08-12T13:42:13.485+08:00[Asia/Shanghai]

~~~

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: before_route
          uri: http://httpbin.org
          predicates:
            - name: Before
              args:
                datetime: 2024-08-12T13:42:13.485+08:00[Asia/Shanghai]

~~~



### 5.3.Between

时间段

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: between_route
          uri: http://httpbin.org
          predicates:
            - Between=2024-08-12T14:11:33.959+08:00[Asia/Shanghai], 2024-08-13T14:11:33.959+08:00[Asia/Shanghai]

~~~

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: between_route
          uri: http://httpbin.org
          predicates:
            - name: Between
              args:
                datetime1: 2024-08-12T14:11:33.959+08:00[Asia/Shanghai]
                datetime2: 2024-08-13T14:11:33.959+08:00[Asia/Shanghai]

~~~



### 5.4.Cookie

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: cookie_route
          uri: http://httpbin.org
          predicates:
            - Cookie=chocolate, ch.p

~~~

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: cookie_route
          uri: http://httpbin.org
          predicates:
            - name: Cookie
              args:
                name: chocolate
                regexp : ch.p
                
~~~



### 5.5.Header

~~~yml
spring:
  cloud:
    gateway:
      routes:
      - id: header_route
        uri: http://httpbin.org
        predicates:
        - Header=X-Request-Id, \d+

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: header_route
          uri: http://httpbin.org
          predicates:
            - name: Header
              args:
                header: X-Request-Id
                regexp: \d+
                
~~~



### 5.6.Host

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: host_route
          uri: http://httpbin.org
          predicates:
            - Host=**.somehost.org,**.anotherhost.org,**.localhost.**

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: host_route
          uri: http://httpbin.org
          predicates:
            - name: Host
              args:
                patterns1: localhost.**
                patterns2: "**.anotherhost.org"
                patterns3: "**.somehost.org"

~~~



### 5.7.Method

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: method_route
          uri: http://httpbin.org
          predicates:
            - Method=GET,POST

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: method_route
          uri: http://httpbin.org
          predicates:
            - name: Method
              args:
                methods: GET
                methods1: POST

~~~



### 5.8.Path

//delay/\*\*和/delay/\*\*

//delya/**不会匹配到gateway的/delay/4请求，会匹配//delay/4请求。

/delay/1的匹配规则，如果为true，则/delay/1/会被匹配，false则不会

![image-20240826153418417](http://47.101.155.205/image-20240826153418417.png)

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path_route
          uri: http://httpbin.org
          predicates:
            - Path=/delay/{segment}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path_route
          uri: http://httpbin.org
          predicates:
            - name: Path
              args:
                patterns: /delay/{segment}
                matchTrailingSlash: false

~~~

![image-20240812145229828](http://47.101.155.205/image-20240812145229828.png)



### 5.9.Query

参数param，regexp，其中regexp可以没有，表示什么都可以

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: query_route
          uri: http://httpbin.org
          predicates:
            - Query=green, gree.
            
~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: query_route
          uri: http://httpbin.org
          predicates:
            - name: Query
              args:
                param: green
                regexp: gree.

~~~



### 5.10.RemoteAddr

localhost地址获取的RemoteAddr为0:0:0:0:0:0:0:1。

获取的为发起请求的客户端的ip地址，如果存在代理，则这里获取的地址可能不准确。

![image-20240812151214211](http://47.101.155.205/image-20240812151214211.png)

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: remoteaddr_route
          uri: http://httpbin.org
          predicates:
            - RemoteAddr=127.0.0.1

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: remoteaddr_route
          uri: http://httpbin.org
          predicates:
            - name: RemoteAddr
              args:
                sources: 127.0.0.1
                sources1: 192.168.8.8

~~~



#### 5.10.1修改获取RemoteAddr方式

XForwardedRemoteAddressResolver提供了两个方法

1. trustAll：总是获取请求头中X-Forwarded-For的第一个值，会存在风险，客户端可以修改X-Forwarded-For的初始值，该值也会被解析器接收。
2. maxTrustedIndex(数字)：在到达gateway之前经历可信代理的次数。



~~~java
RemoteAddressResolver resolver = XForwardedRemoteAddressResolver
            .maxTrustedIndex(1);
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("direct-route",
                        r -> r.remoteAddr("10.1.1.1", "10.10.1.1/24")
                                .uri("https://downstream1"))
                .route("proxied-route",
                        r -> r.remoteAddr(resolver, "10.10.1.1", "10.10.1.1/24")
                                .uri("https://downstream2"))
                .build();
}

~~~





### 5.11.Weight

组group1，20%的代理到https://weightlow.org，80%代理到http://httpbin.org

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: weight_high
          uri: http://httpbin.org/get
          predicates:
            - Weight=group1, 8
        - id: weight_low
          uri: https://weightlow.org
          predicates:
            - Weight=group1, 2

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: weight_high
          uri: http://httpbin.org
          predicates:
            - name: Weight
              args:
                group: group1
                weight: 8
        - id: weight_low
          uri: https://weightlow.org
          predicates:
            - name: Weight
              args:
                group: group1
                weight: 2

~~~



## 6.GatewayFilter 工厂

Route过滤器能修改进来的Http请求和响应。

### 6.1.AddRequestHeader

给匹配的request的请求头添加对应的key=value。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: http://httpbin.org
          filters:
            - AddRequestHeader=X-Request-red, blue
          predicates:
            - name: Path
              args:
                patterns: /delay/{int}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: http://httpbin.org
          filters:
            - name: AddRequestHeader
              args:
                name: X-Request-red
                value: blue-{int}
          predicates:
            - name: Path
              args:
                patterns: /delay/{int}

~~~



### 6.2.AddRequestParameter

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_parameter_route
          uri: http://httpbin.org
          predicates:
            - Host={segment}.myhost.org
          filters:
            - AddRequestParameter=foo, bar-{segment}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_parameter_route
          uri: http://httpbin.org
          predicates:
            - name: Host
              args:
                patterns1: {segment}.myhost.org
          filters:
            - name: AddRequestParameter
              args: 
                name: foo
                value: bar-{segment}

~~~



### 6.3.AddResponseHeader

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_response_header_route
          uri: http://httpbin.org
          predicates:
            - Host=localhost:8080
          filters:
            - AddResponseHeader=foo, bar-

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_response_header_route
          uri: http://httpbin.org
          predicates:
            - Host=localhost:8080
          filters:
            - name: AddResponseHeader
              args:
                name: foo
                value: localhost:8080

~~~



![image-20240812165833754](http://47.101.155.205/image-20240812165833754.png)



### 6.4.DedupeResponseHeader

参数有name(list)，strategy(默认RETAIN_FIRST、RETAIN_LAST、RETAIN_UNIQUE，可以不填)。

~~~yml
spring:
  cloud:
    gateway:
      routes:
        - id: dedupe_response_header_route
          uri: http://httpbin.org
          filters:
            - AddResponseHeader=Access-Control-Allow-Credentials, false
            - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
          predicates:
            - name: Path
              args:
                pattern: /**

~~~

这里有如果包含多个请求头key，key之间用空格分割，与其他不同。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: dedupe_response_header_route
          uri: http://httpbin.org
          filters:
            - name: AddResponseHeader
              args:
                name: Access-Control-Allow-Credentials
                value: 100
            - name: DedupeResponseHeader
              args:
                name: Access-Control-Allow-Credentials Access-Control-Allow-Origin
                strategy: RETAIN_FIRST
          predicates:
            - name: Path
              args:
                pattern: /**

~~~

说明AddResponseHeader添加的响应头比获取请求响应的头要早。

![image-20240813090204930](http://47.101.155.205/image-20240813090204930.png)

![image-20240813090411387](http://47.101.155.205/image-20240813090411387.png)

![image-20240813090509562](http://47.101.155.205/image-20240813090509562.png)



### 6.5.CircuitBreaker 

断路器应该是类似与JDBC模式，spring-cloud-gateway定义好了断路器的接口，只要引入相关的实现类(即Jdbc的驱动)，就可以实现断路器功能了。

Spring Cloud supports Resilience4J就是开箱即用。

断路器实现相关文档：https://cloud.spring.io/spring-cloud-circuitbreaker/reference/html/spring-cloud-circuitbreaker.html

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: circuitbreaker_route
          uri: http://httpbin.org
          predicates:
            - Path=/delay/{int}
          filters:
            - name: CircuitBreaker
              args:
                name: myCircuitBreaker
                fallbackUri: forward:/demo
            - RewritePath=/delay/4, /delay/5

~~~

~~~java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
	return builder.routes()
                .route("circuitbreaker_route",
                        r -> r.path("/delay/{int}")
                                .filters(f ->
                                        f.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/demo"))
                                                .rewritePath("/delay/4", "/delay/5")).
                                uri("http://httpbin.org"))
                .build();
}

~~~

把转发的请求拦截，在转发出去。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ingredients
          uri: http://httpbin.org
          predicates:
            - Path=/delay/**
          filters:
            - name: CircuitBreaker
              args:
                name: fetchIngredients
                fallbackUri: forward:/get
        - id: ingredients-fallback
          uri: http://httpbin.org
          predicates:
            - Path=/get

~~~



#### 6.5.1.配置状态码代表启用断路器

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ingredients
          uri: http://httpbin.org
          predicates:
            - Path=/delay/**
          filters:
            - name: CircuitBreaker
              args:
                name: fetchIngredients
                fallbackUri: forward:/get
                statusCodes:
                  - 404
                  - "NOT_FOUND"
        - id: ingredients-fallback
          uri: http://httpbin.org
          predicates:
            - Path=/get

~~~

~~~java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
	return builder.routes()
                .route("circuitbreaker_route",
                        r -> r.path("/delay/{int}")
                                .filters(f ->
                                        f.circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/demo").addStatusCode("INTERNAL_SERVER_ERROR"))
                                                .rewritePath("/delay/4", "/delay/5")).
                                uri("http://httpbin.org"))
                .build();
}

~~~



### 6.6.FallbackHeaders

熔断转发的请求中添加异常信息

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ingredients
          uri: http://httpbin.org
          predicates:
            - Path=/delay/**
          filters:
            - name: CircuitBreaker
              args:
                name: fetchIngredients
                fallbackUri: forward:/get
        - id: ingredients-fallback
          uri: http://httpbin.org
          predicates:
            - Path=/get
          filters:
            - name: FallbackHeaders
              args:
                executionExceptionTypeHeaderName: Test-Header

~~~

![image-20240813101642096](http://47.101.155.205/image-20240813101642096.png)

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ingredients
          uri: http://httpbin.org
          predicates:
            - Path=/delay/**
          filters:
            - name: CircuitBreaker
              args:
                name: fetchIngredients
                fallbackUri: forward:/get
        - id: ingredients-fallback
          uri: http://httpbin.org
          predicates:
            - Path=/get
          filters:
            - name: FallbackHeaders
              args:
                executionExceptionTypeHeaderName: Execution-Exception-Type
                executionExceptionMessageHeaderName: Execution-Exception-Message
                rootCauseExceptionTypeHeaderName: Root-Cause-Exception-Type
                rootCauseExceptionMessageHeaderName: Root-Cause-Exception-Message

~~~



![image-20240813101939821](http://47.101.155.205/image-20240813101939821.png)



### 6.7.MapRequestHeader

将到gateway的请求头中的key(fromHeader)的value携带到下游的请求头的key(toHeader)的value，如果fromHeader不存在，没有任何影响。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: map_request_header_route
          uri: http://httpbin.org
          filters:
            - MapRequestHeader=Blue, X-Request-Red
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: map_request_header_route
          uri: http://httpbin.org
          filters:
            - name: MapRequestHeader
              args:
                fromHeader: Blue
                toHeader: X-Request-Red
          predicates:
            - name: Path
              args:
                patterns: /get

~~~



![](http://47.101.155.205/image-20240813105448085.png)



### 6.8.PrefixPath

把代理的请求添加前缀发送到下游。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: prefixpath_route
          uri: http://httpbin.org
          filters:
            - PrefixPath=/delay
          predicates:
            - name: Path
              args:
                patterns: /*

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: prefixpath_route
          uri: http://httpbin.org
          filters:
            - name: PrefixPath
              args:
                prefix: /delay
          predicates:
            - name: Path
              args:
                patterns: /*

~~~



![image-20240813110408161](http://47.101.155.205/image-20240813110408161.png)



### 6.9.PreserveHostHeader

请求头的Host记录的是到达gateway之前的http发起客户端的host，而不是gateway发起http请求的host地址。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: preserve_host_route
          uri: http://httpbin.org
          filters:
            - PreserveHostHeader
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: preserve_host_route
          uri: http://httpbin.org
          filters:
            - name: PreserveHostHeader
          predicates:
            - name: Path
              args:
                patterns: /get

~~~





![image-20240813111347589](http://47.101.155.205/image-20240813111347589.png)



### 6.10.RequestRateLimiter

实现KeyResolver接口注入容器，通过request中的哪些属性来生成限流key，来限制请求速率(使用默认的策略，未开启不拒绝，请求响应403)。

~~~java
// 使用客户端ip地址来限流
@Bean
KeyResolver userKeyResolver() {
	return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

~~~



spring.cloud.gateway.filter.request-rate-limiter.deny-empty-key=true/false是否拒绝空的匹配

spring.cloud.gateway.filter.request-rate-limiter.empty-key-status-code拒绝的状态码

需要引入spring-boot-starter-data-redis-reactive依赖。

限速算法：https://en.wikipedia.org/wiki/Token_bucket

~~~pom
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>

~~~

redis-rate-limiter.replenishRate：表示每秒令牌桶中能容纳的令牌数。

redis-rate-limiter.burstCapacity：表示桶的最大容量。

redis-rate-limiter.requestedTokens：每个请求消耗的令牌数，默认1

出现配置速率和实际请求不符的原因是没有配置redis的连接信息。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: requestratelimiter_route
          uri: http://localhost:8080/
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 1
                redis-rate-limiter.burstCapacity: 60
                redis-rate-limiter.requestedTokens: 60
            - name: PrefixPath
              args:
                prefix: /demo
          predicates:
            - name: Path
              args:
                patterns: /get
      filter:
        request-rate-limiter:
          deny-empty-key: false

~~~

~~~java
@Bean
KeyResolver userKeyResolver() {
	return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
}

~~~

![image-20240813142105464](http://47.101.155.205/image-20240813142105464.png)



~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: requestratelimiter_route
          uri: http://localhost:8080/
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                redis-rate-limiter.requestedTokens: 1
            - name: PrefixPath
              args:
                prefix: /demo
          predicates:
            - name: Path
              args:
                patterns: /get
      filter:
        request-rate-limiter:
          deny-empty-key: false
  redis:
    database: 0
    cluster:
      nodes:
        - 192.168.120.110:26379
        - 192.168.120.110:27379
        - 192.168.120.111:26379
        - 192.168.120.111:27379
        - 192.168.120.112:26379
        - 192.168.120.112:27379
    password: epredis

~~~

![image-20240813144227463](http://47.101.155.205/image-20240813144227463.png)



#### 6.10.1.自定义方式

~~~yaml
spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            rate-limiter: "#{@myRateLimiter}"
            key-resolver: "#{@userKeyResolver}"

~~~



### 6.11.RedirectTo

重定向到http://httpbin.org/get地址，这里配置的uri路径不生效。

uri不换成这样uri: no://op，如果filters中的url是相对路径，则会映射到当前host+相对路径

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: prefixpath_route
          uri: https://example.org
          filters:
            - RedirectTo=302, http://httpbin.org/get
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240813150713446](http://47.101.155.205/image-20240813150713446.png)

![image-20240813151442642](http://47.101.155.205/image-20240813151442642.png)



~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: prefixpath_route
          uri: no://op
          filters:
            - name: RedirectTo
              args:
                status: 302
                url: demo/get
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240813153002214](http://47.101.155.205/image-20240813153002214.png)



### 6.12.RemoveRequestHeader

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: removerequestheader_route
          uri: http://httpbin.org
          filters:
            - RemoveRequestHeader=X-Request-Foo
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

展开配置仅支持name对应的value被移除，不支持多个key移除。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: removerequestheader_route
          uri: http://httpbin.org
          filters:
            - name: RemoveRequestHeader
              args:
                name: X-Request-Foo
          predicates:
            - name: Path
              args:
                patterns: /get

~~~



![image-20240814162431758](http://47.101.155.205/image-20240814162431758.png)





### 6.13.RemoveResponseHeader

移除响应头中任何key

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: removeresponseheader_route
          uri: http://httpbin.org
          filters:
            - RemoveResponseHeader=Date
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240814163503850](http://47.101.155.205/image-20240814163503850.png)

通过spring.cloud.gateway.default-filters这个配置过滤器，应用在所有的route上。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: defaultFilterRoute1
          uri: http://httpbin.org
          predicates:
            - name: Path
              args:
                patterns: /get
        - id: defaultFilterRoute2
          uri: http://httpbin.org
          predicates:
            - name: Path
              args:
                patterns: /delay/{int}
      default-filters:
        - name: RemoveResponseHeader
          args:
            name: Date
            
~~~





### 6.14.RemoveRequestParameter

移除请求参数

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: removerequestparameter_route
          uri: http://httpbin.org
          filters:
            - RemoveRequestParameter=user
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240814164454175](http://47.101.155.205/image-20240814164454175.png)

![image-20240814164509193](http://47.101.155.205/image-20240814164509193.png)



### 6.15.RewritePath

重写转发请求路径。

需要regexp(待匹配的正则表达式)和replacement(替换后的表达式)两个参数。

匹配/get/开头的字符串，将/get/后面部分捕获到segmengt捕获组中。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rewritepath_route
          uri: http://httpbin.org
          predicates:
            - Path=/get/**
          filters:
            - RewritePath=/get/?(?<segment>.*), /$\{segment}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rewritepath_route
          uri: http://httpbin.org
          predicates:
            - Path=/get/**
          filters:
            - name: RewritePath
              args:
                regexp: /get/?(?<segment>.*)
                replacement: /$\{segment}

~~~



![image-20240814172718935](http://47.101.155.205/image-20240814172718935.png)



### 6.16.RewriteLocationResponseHeader

支持stripVersionMode, locationHeaderName, hostValue, protocolsRegex四个参数，用来修改请求响应头的Location的value。

作用是将locationHeaderName对应的值的响应头重写，如果协议匹配，host值替换成hostValue的值。

1. stripVersionMode：有可选项NEVER_STRIP、AS_IN_REQUEST(默认)、ALWAYS_STRIP。请求的uri中以/v数字开始，决定是否把请求头的版本替换
   1. NEVER_STRIP：不管请求中是否有uri请求头的版本号从不移除；
   2. AS_IN_REQUEST：请求的uri中没有有版本号，才移除请求头中版本号；
   3. ALWAYS_STRIP：不管请求里是否有版本号，都移除请求头的value的版本号。
2. locationHeaderName：获取响应头key的name
3. hostValue：配置的hostValue，默认为请求头的Host的value
4. protocolsRegex：有效的正则表达式，用来匹配协议，如果协议不匹配，过滤器什么都不做，默认是http|https|ftp|ftps()。

~~~txt
替换的正则表达式
(?<=^(?:protocolsRegex)://)[^:/]+(?::\d+)?(?=/)
(?<=^(?:https?|ftps?)://)[^:/]+(?::\d+)?(?=/)
(?<=^(?:protocolsRegex)://)[^:/]+(?::\d+)?(?:/v\d+)?(?=/)
(?<=^(?:https?|ftps?)://)[^:/]+(?::\d+)?(?:/v\d+)?(?=/)
https?表示匹配http或https，因为?代表前面的表达式出现一次或零次

请求版本号校验表达式
^/v\\d+/.*

~~~





~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rewritelocationresponseheader_route
          uri: http://httpbin.org
          filters:
            - AddResponseHeader=TEST, http://localhost:8080/v1/demo/get
            - RewriteLocationResponseHeader=ALWAYS_STRIP , TEST, httpbin.org, http|https|ftp|ftps
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

localhost:8080替换成了httpbin.org，去掉了版本。

![image-20240815112620809](http://47.101.155.205/image-20240815112620809.png)



### 6.17.RewriteResponseHeader

支持参数：name, regexp, replacement。

响应头name的value，匹配regexp正则表达式的字符串部分都替换成replacemengt。

如果replacement需要用到$字符，需要用$\表示。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rewriteresponseheader_route
          uri: http://httpbin.org
          filters:
            - AddResponseHeader=X-Response-Red, password=1234&
            - RewriteResponseHeader=X-Response-Red, , password=[^&]+, password=***
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240815145609596](http://47.101.155.205/image-20240815145609596.png)



### 6.18.SaveSession

有使用SpringSecurity和SpringSession，将安全信息发送到下游请求。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
      - id: save_session
        uri: https://example.org
        predicates:
        - Path=/foo/**
        filters:
        - SaveSession

~~~





### 6.19.SecureHeaders

为了http响应头添加参数：https://blog.appcanary.com/2017/http-security-headers.html

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route
          uri: http://httpbin.org
          filters:
            - name: SecureHeaders
          predicates:
            - name: Path
              args:
                patterns: /get
      filter:
        secure-headers:
          disable: x-frame-options,strict-transport-security # 禁用key

~~~

![image-20240815153232147](http://47.101.155.205/image-20240815153232147.png)

![image-20240815153128019](http://47.101.155.205/image-20240815153128019.png)



### 6.20.SetPath

修改到下游的路径。

支持参数template。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: setpath_route
          uri: http://httpbin.org
          predicates:
            - Path=/get/{segment}
          filters:
            - SetPath=/{segment}

~~~

![image-20240815153739787](http://47.101.155.205/image-20240815153739787.png)



### 6.21.SetRequestHeader

支持参数name、value。

修改请求头的key的value，替换所有。

支持动态替换匹配Host或Path的遍历。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: setrequestheader_route
          uri: http://httpbin.org
          filters:
            - SetRequestHeader=X-Request-Red, Blue
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240815154131705](http://47.101.155.205/image-20240815154131705.png)



~~~yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setrequestheader_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - SetRequestHeader=foo, bar-{segment}

~~~





### 6.22.SetResponseHeader

支持参数name、value。

修改响应头的key的value，替换所有。

支持动态替换匹配Host或Path的遍历。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: setresponseheader_route
          uri: http://httpbin.org
          filters:
            - SetResponseHeader=Date, Blue
          predicates:
            - name: Path
              args:
                patterns: /get

~~~

![image-20240815154646877](http://47.101.155.205/image-20240815154646877.png)



### 6.23.SetStatus

支持参数：status。

必须是一个有效的状态，在org.springframework.http.HttpStatus枚举中。

可以是状态码，也可以是单个枚举属性名。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: setstatusstring_route
          uri: http://httpbin.org
          filters:
            - SetStatus=BAD_REQUEST
          predicates:
            - name: Path
              args:
                patterns: /get
        - id: setstatusint_route
          uri: http://httpbin.org
          filters:
            - SetStatus=401
          predicates:
            - name: Path
              args:
                patterns: /delay/1

~~~



![image-20240815155600898](http://47.101.155.205/image-20240815155600898.png)



可以通过配置spring.cloud.gateway.set-status.original-status-header-name=original-http-status，将代理请求响应的状态码放在响应头中，key为original-http-status

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: setstatusstring_route
          uri: http://httpbin.org
          filters:
            - SetStatus=BAD_REQUEST
          predicates:
            - name: Path
              args:
                patterns: /get
        - id: setstatusint_route
          uri: http://httpbin.org
          filters:
            - SetStatus=401
          predicates:
            - name: Path
              args:
                patterns: /delay/1
      set-status:
        original-status-header-name: original-http-status

~~~



![image-20240815155826805](http://47.101.155.205/image-20240815155826805.png)



### 6.24.StripPrefix

一个参数：parts

跳过几个/*，截取掉几个再转发请求。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nameRoot
          uri: http://httpbin.org
          predicates:
            - Path=/name/**
          filters:
            - StripPrefix=2

~~~

![image-20240815160407302](http://47.101.155.205/image-20240815160407302.png)



### 6.25.Retry

重试，支持以下参数：

1. retries：重试次数；
2. statuses：什么样的状态码应该重试(多个)；
3. methods：什么样的方法应该重试(多个)；
4. series：什么样的HttpStatus.Series应该重试(多个)；
5. exceptions：什么样的异常应该重试(多个)；
6. backoff：重试因子，控制重试间隔时间。

启用该过滤器的默认配置：

1. retries：3；
2. series：5xx；
3. methods：GET；
4. exceptions：IOException，TimeoutException；
5. backoff：空。



~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: retry_test
          uri: http://httpbin.org/get # 在转发请求时/get会被去掉
          predicates:
            - Path=/get
          filters:
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY
                methods: GET,POST
                backoff:
                  firstBackoff: 10ms
                  maxBackoff: 50ms
                  factor: 2
                  basedOnPreviousValue: false

~~~

![image-20240815163438183](http://47.101.155.205/image-20240815163438183.png)

### 6.26.RequestSize

限制到下游请求的请求大小，默认单位B，可以添加后缀KB/MB表示单位。

没有配置，请求大小默认时5MB。超出大小，返回状态码为413。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
      - id: request_size_route
        uri: http://localhost:8080/upload
        predicates:
        - Path=/upload
        filters:
        - name: RequestSize
          args:
            maxSize: 5000000

~~~

![image-20240815163924559](http://47.101.155.205/image-20240815163924559.png)





### 6.27.SetRequestHostHeader

修改已经存在的请求头key为Host的value。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: set_request_host_header_route
          uri: http://httpbin.org
          predicates:
            - Path=/get
          filters:
            - name: SetRequestHostHeader
              args:
                host: example.org

~~~



![image-20240815164051153](http://47.101.155.205/image-20240815164051153.png)



### 6.28.ModifyRequestBody

修改请求体在发送请求之前。

只能使用代码实现，Java DSL。需要怎加null判断处理。

~~~java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_request_obj", r -> r.path("/**")
            .filters(f -> f.prefixPath("/delay")
                .modifyRequestBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,
                    (exchange, s) -> s != null ? Mono.just(new Hello(s.toUpperCase())): Mono.empty())).uri("http://httpbin.org"))
        .build();
}

static class Hello {
    String message;

    public Hello() { }

    public Hello(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

~~~

![image-20240815165320644](http://47.101.155.205/image-20240815165320644.png)



### 6.29.ModifyResponseBody

在返回请求到客户端之前，修改响应体。

只能使用代码实现，Java DSL。需要怎加null判断处理。

~~~java
// 这里不用一定要挂在一个过滤器下面
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
	return builder.routes()
        .route("rewrite_response_upper", r -> r.path("/get")
               .filters(f -> f.modifyResponseBody(String.class, String.class, 
                                                  (exchange, s) -> Mono.just(s.toUpperCase()))).uri("http://httpbin.org"))
        .build();
}

~~~

![image-20240815170320240](http://47.101.155.205/image-20240815170320240.png)



### 6.30.Relay

抓取oauth2的token到下游。

https://github.com/spring-cloud-samples/sample-gateway-oauth2login

~~~pom
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

~~~

~~~java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
	return builder.routes()
        .route("resource", r -> r.path("/get")
               .filters(f -> f.tokenRelay())
               .uri("http://httpbin.org"))
        .build();
}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
      - id: resource
        uri: http://httpbin.org
        predicates:
        - Path=/get
        filters:
        - TokenRelay=

~~~



![image-20240815171541757](http://47.101.155.205/image-20240815171541757.png)





### 6.31.CacheRequestBody

提起请求body至ServerWebExchange.getAttributes()中，key为ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR。

仅适用于http和https请求。

~~~java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
	return builder.routes()
        .route("cache_request_body_route", r -> r.path("/get").filters(f -> f.cacheRequestBody(String.class)).uri("http://httpbin.org"))
        .build();
}

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: cache_request_body_route
          uri: http://httpbin.org
          predicates:
            - Path=/get
          filters:
            - name: CacheRequestBody
              args:
                bodyClass: java.lang.String

~~~





### 6.32.Default

将过滤器应用至所有的route。

6.13有提到。

~~~yaml
spring:
  cloud:
    gateway:
      default-filters:
      - AddResponseHeader=X-Response-Default-Red, Default-Blue
      - PrefixPath=/httpbin
      
~~~



## 7.全局过滤器



### 7.1.自定义全局过滤器GlobalFilter

当请求与路由匹配时，过滤web处理器会将所有的全局过滤器实例和特定作用于路由的过滤器添加到过滤链中。

~~~java
package com.example.springcloudgatewaysimple.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

@Bean
public GlobalFilter customFilter() {
	return new CustomGlobalFilter();
}

~~~



### 7.2.ForwardRoutingFilter

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path_route
          uri: forward:///demo
          predicates:
            - Path=/get

~~~

![image-20240816094556770](http://47.101.155.205/image-20240816094556770.png)



### 7.3.ReactiveLoadBalancerClientFilter

过滤器获取ServerWebExchange的属性ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR(协议的lb://service)或获取的属性ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR为lb，则会使用ReactorLoadBalancer解析service的host和port，然后替换属性ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR的值，为修改的url会添加的这个ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR列表中。

当gateway接收的请求协议是https时，如果这个过滤器获取协议为http，则会使用http协议发送到下游。相反也是如此。

spring-coud-loadbalancer：https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#spring-cloud-loadbalancer

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: myRoute
          uri: lb://service
          predicates:
            - Path=/get/**

~~~

当route配置成lb协议时，如果ReactorLoadBalancer对象没有在容器中找到，则会返回503的状态码。

可以通过这个spring.cloud.gateway.loadbalancer.use404: true配置修改返回状态码为404。

![image-20240816095824567](http://47.101.155.205/image-20240816095824567.png)



### 7.4.NettyRoutingFilter

URI requestUrl = (URI)exchange.getRequiredAttribute(ServerWebExchangeUtils.*GATEWAY_REQUEST_URL_ATTR*);获取的请求协议时http或https，则使用Netty HttpClient发送请求到下游。请求响应体放在exchange的属性ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR中(可以在后续的过滤器中使用该属性)。

WebClientHttpRoutingFilter不要求Netty



### 7.5.NettyWriteResponseFilter

如果Netty的请求响应在exchange的属性ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR中，则执行该过滤器。该过滤器执行在其他过滤器完成之后，将代理请求响应结果返回至客户端。

WebClientWriteResponseFilter不要求Netty。



### 7.6.RouteToRequestUrlFilter

如果exchange中属性ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR存在值，RouteToRequestUrlFilter将会执行。

基于请求的URI和Route的URI创建一个新的URI，新的URI会被添加到exchange的属性ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR中。

如果是其他协议(URI.getSchemeSpecificPart())，例如：lb:ws://serviceid，则lb(协议)会被添加到exchange的属性ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR中，以便其他后续过滤器使用。

![image-20240816110532018](http://47.101.155.205/image-20240816110532018.png)





### 7.7.WebsocketRoutingFilter

如果exchange的属性ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR值为ws或wss协议，该过滤器启用。使用Spring WebSocket发送请求到下游。

例如：lb:ws://serviceid配置在route中，则会触发该过滤器。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
      # SockJS route
      - id: websocket_sockjs_route
        uri: http://localhost:3001
        predicates:
        - Path=/websocket/info/**
      # Normal Websocket route
      - id: websocket_route
        uri: ws://localhost:3001
        predicates:
        - Path=/websocket/**

~~~



### 7.8.GatewayMetricsFilter

GatewayMetricsFilter启用需要引入spring-boot-starter-actuator依赖。

~~~pom
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

~~~

过滤器会添加名为spring.cloud.gateway.requests的计时器指标，有以下标签，

1. routeId：route的id；
2. routeUri：API路由到的uri；
3. outcome：HttpStatus.Series，4XX或5XX等，表示一些了的状态码；
4. status：返回给客户端的请求的HTTP状态码；
5. httpStatusCode：返回给客户端的请求的HTTP状态码；
6. httpMethod：请求使用的请求方法GET、POST等；
7. path：请求的路径。

spring.cloud.gateway.metrics.tags.path.enabled可以配置一些额外的监控标签。

查询这些指标，需要暴露端点metrics，同时这个过滤器有执行过，否则也是404.

![image-20240816142737446](http://47.101.155.205/image-20240816142737446.png)

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: myRoute
          uri: http://httpbin.org/get
          predicates:
            - Path=/get/**
      metrics:
        enabled: true
  application:
    name: spring-cloud-gateway-simple
management:
  endpoints:
    web:
      exposure:
        include: "metrics"
  metrics:
    export:
      prometheus:
        enabled: true

~~~







#### 7.8.1.安装prometheus

~~~bash
# 解压缩安装
tar zvxf prometheus-2.53.0.linux-amd64.tar.gz

cd prometheus-2.53.0.linux-amd64

# 读取指定yml配置启动,以8090端口启动
./prometheus --config.file=prometheus.yml --web.listen-address=:8090

nohup ./prometheus --config.file=prometheus.yml  > ./logs/prometheus.log 2>&1 &

~~~

配置yaml文件，/actuator/metrics/spring.cloud.gateway.requests接口的数据prometheus不能解析。

~~~yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

rule_files:

scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]
  - job_name: "spring-cloud-gateway-simple"
    metrics_path: "/actuator/prometheus" # 默认/metrics
    static_configs:
      - targets: ["localhost:8080"]

~~~

![image-20240816153742812](http://47.101.155.205/image-20240816153742812.png)



#### 7.8.2.安装grafana

~~~bash
# 解压缩安装安装
wget https://dl.grafana.com/enterprise/release/grafana-enterprise-11.1.0.linux-amd64.tar.gz
tar -zxvf grafana-enterprise-11.1.0.linux-amd64.tar.gz

# 启动
./bin/grafana-server

# http://localhost:3000 访问登录,默认admin/admin

~~~

json模板地址：https://docs.spring.io/spring-cloud-gateway/docs/3.1.0/reference/html/gateway-grafana-dashboard.json

~~~json
{"__inputs":[{"name":"DS_PROMETHEUS","label":"prometheus","description":"","type":"datasource","pluginId":"prometheus","pluginName":"Prometheus"}],"__requires":[{"type":"grafana","id":"grafana","name":"Grafana","version":"5.0.0"},{"type":"panel","id":"graph","name":"Graph","version":"5.0.0"},{"type":"datasource","id":"prometheus","name":"Prometheus","version":"5.0.0"}],"annotations":{"list":[{"builtIn":1,"datasource":"-- Grafana --","enable":true,"hide":true,"iconColor":"rgba(0, 211, 255, 1)","name":"Annotations & Alerts","type":"dashboard"}]},"editable":true,"gnetId":null,"graphTooltip":0,"id":null,"iteration":1550777954006,"links":[],"panels":[{"collapsed":true,"gridPos":{"h":1,"w":24,"x":0,"y":0},"id":50,"panels":[{"cacheTimeout":null,"colorBackground":false,"colorValue":false,"colors":["rgba(245, 54, 54, 0.9)","rgba(191, 148, 112, 0.89)","rgba(50, 172, 45, 0.97)"],"datasource":"${DS_PROMETHEUS}","description":"","format":"none","gauge":{"maxValue":100,"minValue":0,"show":false,"thresholdLabels":false,"thresholdMarkers":true},"gridPos":{"h":3,"w":8,"x":0,"y":2},"hideTimeOverride":false,"id":28,"interval":null,"links":[],"mappingType":1,"mappingTypes":[{"name":"value to text","value":1},{"name":"range to text","value":2}],"maxDataPoints":100,"nullPointMode":"connected","nullText":null,"postfix":"","postfixFontSize":"50%","prefix":"","prefixFontSize":"50%","rangeMaps":[{"from":"null","text":"N/A","to":"null"}],"sparkline":{"fillColor":"rgba(31, 118, 189, 0.18)","full":false,"lineColor":"rgb(31, 120, 193)","show":false},"tableColumn":"Value","targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{job=~\"$gatewayService\"})","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":1200}],"thresholds":"","timeFrom":null,"timeShift":null,"title":"Total Requests Served","type":"singlestat","valueFontSize":"80%","valueMaps":[{"op":"=","text":"N/A","value":"null"}],"valueName":"current"},{"cacheTimeout":null,"colorBackground":false,"colorValue":false,"colors":["rgba(245, 54, 54, 0.9)","rgba(191, 148, 112, 0.89)","rgba(50, 172, 45, 0.97)"],"datasource":"${DS_PROMETHEUS}","description":"","format":"none","gauge":{"maxValue":100,"minValue":0,"show":false,"thresholdLabels":false,"thresholdMarkers":true},"gridPos":{"h":3,"w":8,"x":8,"y":2},"hideTimeOverride":false,"id":29,"interval":null,"links":[],"mappingType":1,"mappingTypes":[{"name":"value to text","value":1},{"name":"range to text","value":2}],"maxDataPoints":100,"nullPointMode":"connected","nullText":null,"postfix":"","postfixFontSize":"50%","prefix":"","prefixFontSize":"50%","rangeMaps":[{"from":"null","text":"N/A","to":"null"}],"sparkline":{"fillColor":"rgba(31, 118, 189, 0.18)","full":false,"lineColor":"rgb(31, 120, 193)","show":false},"tableColumn":"Value","targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{outcome=\"SUCCESSFUL\", job=~\"$gatewayService\"})","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":1200}],"thresholds":"","timeFrom":null,"timeShift":null,"title":"Total Successful Requests Served","type":"singlestat","valueFontSize":"80%","valueMaps":[{"op":"=","text":"N/A","value":"null"}],"valueName":"current"},{"cacheTimeout":null,"colorBackground":false,"colorValue":false,"colors":["rgba(245, 54, 54, 0.9)","rgba(191, 148, 112, 0.89)","rgba(50, 172, 45, 0.97)"],"datasource":"${DS_PROMETHEUS}","description":"","format":"none","gauge":{"maxValue":100,"minValue":0,"show":false,"thresholdLabels":false,"thresholdMarkers":true},"gridPos":{"h":3,"w":8,"x":16,"y":2},"hideTimeOverride":false,"id":30,"interval":null,"links":[],"mappingType":1,"mappingTypes":[{"name":"value to text","value":1},{"name":"range to text","value":2}],"maxDataPoints":100,"nullPointMode":"connected","nullText":null,"postfix":"","postfixFontSize":"50%","prefix":"","prefixFontSize":"50%","rangeMaps":[{"from":"null","text":"N/A","to":"null"}],"sparkline":{"fillColor":"rgba(31, 118, 189, 0.18)","full":false,"lineColor":"rgb(31, 120, 193)","show":false},"tableColumn":"Value","targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{outcome!=\"SUCCESSFUL\", job=~\"$gatewayService\"})","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":1200}],"thresholds":"","timeFrom":null,"timeShift":null,"title":"Total Unsuccessful Requests Served","type":"singlestat","valueFontSize":"80%","valueMaps":[{"op":"=","text":"N/A","value":"null"}],"valueName":"current"}],"repeat":null,"title":"Total Throughput","type":"row"},{"collapsed":true,"gridPos":{"h":1,"w":24,"x":0,"y":1},"id":51,"panels":[{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":7,"w":6,"x":0,"y":3},"id":49,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":"routeId","scopedVars":{"routeId":{"selected":false,"text":"ms-test","value":"ms-test"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_sum{routeId=~\"$routeId\", job=~\"$gatewayService\"}/spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ status }}","metric":"gateway_api_time_seconds_sum","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"s","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":7,"w":6,"x":6,"y":3},"id":70,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":49,"scopedVars":{"routeId":{"selected":false,"text":"msnext","value":"msnext"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_sum{routeId=~\"$routeId\", job=~\"$gatewayService\"}/spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ status }}","metric":"gateway_api_time_seconds_sum","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"s","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":7,"w":6,"x":12,"y":3},"id":71,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":49,"scopedVars":{"routeId":{"selected":false,"text":"msnext-a3s","value":"msnext-a3s"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_sum{routeId=~\"$routeId\", job=~\"$gatewayService\"}/spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ status }}","metric":"gateway_api_time_seconds_sum","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"s","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":7,"w":6,"x":18,"y":3},"id":72,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":49,"scopedVars":{"routeId":{"selected":false,"text":"msnext-sample","value":"msnext-sample"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_sum{routeId=~\"$routeId\", job=~\"$gatewayService\"}/spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ status }}","metric":"gateway_api_time_seconds_sum","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"s","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]}],"repeat":null,"title":"Request Processing Time","type":"row"},{"collapsed":true,"gridPos":{"h":1,"w":24,"x":0,"y":2},"id":52,"panels":[{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":0,"y":4},"id":2,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"minSpan":null,"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":"routeId","scopedVars":{"routeId":{"selected":false,"text":"ms-test","value":"ms-test"}},"seriesOverrides":[],"spaceLength":10,"stack":true,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_count{outcome=\"SUCCESSFUL\", routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","hide":false,"interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ routeUri }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId Success API Gateway Calls","tooltip":{"shared":false,"sort":0,"value_type":"cumulative"},"transparent":false,"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":6,"y":4},"id":73,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"minSpan":null,"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":2,"scopedVars":{"routeId":{"selected":false,"text":"msnext","value":"msnext"}},"seriesOverrides":[],"spaceLength":10,"stack":true,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_count{outcome=\"SUCCESSFUL\", routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","hide":false,"interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ routeUri }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId Success API Gateway Calls","tooltip":{"shared":false,"sort":0,"value_type":"cumulative"},"transparent":false,"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":12,"y":4},"id":74,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"minSpan":null,"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":2,"scopedVars":{"routeId":{"selected":false,"text":"msnext-a3s","value":"msnext-a3s"}},"seriesOverrides":[],"spaceLength":10,"stack":true,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_count{outcome=\"SUCCESSFUL\", routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","hide":false,"interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ routeUri }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId Success API Gateway Calls","tooltip":{"shared":false,"sort":0,"value_type":"cumulative"},"transparent":false,"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":18,"y":4},"id":75,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"minSpan":null,"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550777954005,"repeatPanelId":2,"scopedVars":{"routeId":{"selected":false,"text":"msnext-sample","value":"msnext-sample"}},"seriesOverrides":[],"spaceLength":10,"stack":true,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_count{outcome=\"SUCCESSFUL\", routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","hide":false,"interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ routeUri }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId Success API Gateway Calls","tooltip":{"shared":false,"sort":0,"value_type":"cumulative"},"transparent":false,"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]}],"repeat":null,"title":"Successful API Calls","type":"row"},{"collapsed":true,"gridPos":{"h":1,"w":24,"x":0,"y":3},"id":53,"panels":[{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":8,"x":0,"y":14},"id":3,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":"routeId","seriesOverrides":[],"spaceLength":10,"stack":true,"steppedLine":false,"targets":[{"expr":"spring_cloud_gateway_requests_seconds_count{outcome!=\"SUCCESSFUL\", routeId=~\"$routeId\", job=~\"$gatewayService\"}","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ routeUri }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId Unsuccessful GW Calls","tooltip":{"shared":false,"sort":0,"value_type":"cumulative"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]}],"repeat":null,"title":"Unsuccessful API Calls","type":"row"},{"collapsed":true,"gridPos":{"h":1,"w":24,"x":0,"y":4},"id":54,"panels":[{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":0,"y":15},"id":35,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":"routeId","scopedVars":{"routeId":{"selected":false,"text":"ms-test","value":"ms-test"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}) by (job, status)","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ status }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":6,"y":15},"id":58,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550775166360,"repeatPanelId":35,"scopedVars":{"routeId":{"selected":false,"text":"msnext","value":"msnext"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}) by (job, status)","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ status }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":12,"y":15},"id":59,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550775166360,"repeatPanelId":35,"scopedVars":{"routeId":{"selected":false,"text":"msnext-a3s","value":"msnext-a3s"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}) by (job, status)","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ status }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","description":"","fill":1,"gridPos":{"h":7,"w":6,"x":18,"y":15},"id":60,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","repeat":null,"repeatIteration":1550775166360,"repeatPanelId":35,"scopedVars":{"routeId":{"selected":false,"text":"msnext-sample","value":"msnext-sample"}},"seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"sum(spring_cloud_gateway_requests_seconds_count{routeId=~\"$routeId\", job=~\"$gatewayService\"}) by (job, status)","format":"time_series","interval":"","intervalFactor":2,"legendFormat":"{{ job }} / {{ status }}","metric":"spring_cloud_gateway_requests_seconds_count","refId":"A","step":240}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"$routeId","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]}],"repeat":null,"title":"API / HTTP Status","type":"row"},{"collapsed":false,"gridPos":{"h":1,"w":24,"x":0,"y":5},"id":64,"panels":[],"title":"JVM","type":"row"},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":9,"w":6,"x":0,"y":6},"id":62,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"process_cpu_usage{job=~\"$gatewayService\"}","format":"time_series","hide":false,"interval":"","intervalFactor":1,"legendFormat":"{{ instance }}","refId":"A"}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"CPU Usage","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":9,"w":6,"x":6,"y":6},"id":69,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"sum(jvm_memory_used_bytes{job=~\"$gatewayService\"}) by (job, instance)","format":"time_series","hide":false,"interval":"","intervalFactor":1,"legendFormat":"{{ instance }}","refId":"A"}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"Memory Used","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":9,"w":6,"x":12,"y":6},"id":77,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"jvm_threads_live_threads{job=~\"$gatewayService\"}","format":"time_series","hide":false,"intervalFactor":1,"legendFormat":"{{ instance }}","refId":"A"}],"thresholds":[],"timeFrom":null,"timeShift":null,"title":"Live Threads","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]},{"alert":{"conditions":[{"evaluator":{"params":[0.3],"type":"gt"},"operator":{"type":"and"},"query":{"params":["A","5m","now"]},"reducer":{"params":[],"type":"avg"},"type":"query"}],"executionErrorState":"alerting","frequency":"60s","handler":1,"name":"Memory Saturation alert","noDataState":"no_data","notifications":[]},"aliasColors":{},"bars":false,"dashLength":10,"dashes":false,"datasource":"${DS_PROMETHEUS}","fill":1,"gridPos":{"h":9,"w":6,"x":18,"y":6},"id":79,"legend":{"avg":false,"current":false,"max":false,"min":false,"show":false,"total":false,"values":false},"lines":true,"linewidth":1,"links":[],"nullPointMode":"null","percentage":false,"pointradius":5,"points":false,"renderer":"flot","seriesOverrides":[],"spaceLength":10,"stack":false,"steppedLine":false,"targets":[{"expr":"jvm_gc_memory_promoted_bytes_total/jvm_gc_memory_allocated_bytes_total","format":"time_series","hide":false,"intervalFactor":1,"legendFormat":"{{ instance }}","refId":"A"}],"thresholds":[{"colorMode":"critical","fill":true,"line":true,"op":"gt","value":0.3}],"timeFrom":null,"timeShift":null,"title":"Memory Saturation","tooltip":{"shared":false,"sort":0,"value_type":"individual"},"type":"graph","xaxis":{"buckets":null,"mode":"time","name":null,"show":true,"values":[]},"yaxes":[{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true},{"format":"short","label":null,"logBase":1,"max":null,"min":null,"show":true}]}],"refresh":"1m","schemaVersion":16,"style":"dark","tags":[],"templating":{"list":[{"allValue":null,"current":{},"datasource":"${DS_PROMETHEUS}","hide":0,"includeAll":true,"label":"Gateway Service","multi":true,"name":"gatewayService","options":[],"query":"label_values(spring_cloud_gateway_requests_seconds_count,job)","refresh":1,"regex":"","sort":1,"tagValuesQuery":"","tags":[],"tagsQuery":"","type":"query","useTags":false},{"allValue":null,"current":{},"datasource":"${DS_PROMETHEUS}","hide":0,"includeAll":true,"label":"Route Id","multi":true,"name":"routeId","options":[],"query":"label_values(spring_cloud_gateway_requests_seconds_count,routeId)","refresh":1,"regex":"","sort":1,"tagValuesQuery":"","tags":[],"tagsQuery":"","type":"query","useTags":false}]},"time":{"from":"now/d","to":"now"},"timepicker":{"refresh_intervals":["5s","10s","30s","1m","5m","15m","30m","1h","2h","1d"],"time_options":["5m","15m","1h","6h","12h","24h","2d","7d","30d"]},"timezone":"","title":"Spring Cloud Gateway","uid":"7voUGKrik","version":5}

~~~



![image-20240816154008774](http://47.101.155.205/image-20240816154008774.png)





### 7.9.Marking An Exchange As Routed

标记请求已被route，如果请求已经被route，则exchange中有属性gatewayAlreadyRouted的值为true。一但请求被标记为已路由，则其他的route过滤器不会再路由它。

ServerWebExchangeUtils.isAlreadyRouted(exchange)，校验exchange是否被route。

ServerWebExchangeUtils.setAlreadyRouted(exchange)，标记exchange已被route。



## 8.HttpHeadersFilters

HttpHeadersFilters过滤器在gateway转发请求前执行，例如：NettyRoutingFilter。



### 8.1.ForwardedHeadersFilter

http://[0:0:0:0:0:0:0:1]:8080/info发起ipv6的请求

请求头不存在Forwarded的key，则创建这样一个头，里面的值有请求头的Host，请求的协议，客户端的ip和端口。如下图格式。

~~~java
exchange.getRequest().getURI().getScheme();// 获取请求的协议
HttpHeaders.getFirst("host");// 获取请求头host的第一个value
// 获取客户端
request.getRemoteAddress().getAddress().getHostAddress();// ipv4则是客户端的ipv4地址
request.getRemoteAddress().getPort();//远程客户端的端口号

~~~



![image-20240816164311608](http://47.101.155.205/image-20240816164311608.png)



### 8.2.RemoveHopByHopHeadersFilter

从接收的请求头移除一些key，同时接收的响应头中也会移除这些请求头的key，spring.cloud.gateway.filter.remove-hop-by-hop.headers可以配置移除的key。

![image-20240816170721760](http://47.101.155.205/image-20240816170721760.png)

![image-20240816171749041](http://47.101.155.205/image-20240816171749041.png)

![image-20240816171813317](http://47.101.155.205/image-20240816171813317.png)



### 8.3.XForwardedHeadersFilter

过滤器已经执行，共添加了4个请求头，实际只有一个请求头(是因为这里测试地址没有返回这个地址)。

1. X-Forwarded-For：远程客户端的ip地址(可以是IPv4或IPv6)；
2. X-Forwarded-Host：到gateway请求域名(ip)+端口(http和https默认端口不显示)
3. X-Forwarded-Port：到gateway请求的端口号；
4. X-Forwarded-Proto：到gateway获取请求的协议；

~~~java
// 通过到web的Controller
Enumeration<String> headerNames = request.getHeaderNames();
while (headerNames.hasMoreElements()) {
	String key = headerNames.nextElement();
	System.out.println(key + ": " + request.getHeader(key));
}

~~~



~~~java
content-length: 303
Accept: */*
User-Agent: PostmanRuntime/7.41.1
Forwarded: proto=http;host="192.168.8.8:8080";for="192.168.8.8:59116"
X-Forwarded-Proto: http
X-Forwarded-Host: 192.168.8.8:8080
host: 192.168.8.8:9041
X-Forwarded-For: 192.168.8.8
Postman-Token: aaf82bc0-e59b-478d-988d-c00d88585d0b
Accept-Encoding: gzip, deflate, br
X-Forwarded-Port: 8080
Content-Type: application/json

// 请求地址为IPv6
content-length: 303
Accept: */*
User-Agent: PostmanRuntime/7.41.1
Forwarded: proto=http;host="[fe80::410c:f0df:b0e:cb5b%6]:8080";for="[fe80:0:0:0:410c:f0df:b0e:cb5b%6]:59626"
X-Forwarded-Proto: http
X-Forwarded-Host: [fe80::410c:f0df:b0e:cb5b%6]:8080
host: 192.168.8.8:9041
X-Forwarded-For: fe80:0:0:0:410c:f0df:b0e:cb5b%6
Postman-Token: f71e291e-8528-42d6-9d70-51e4ba5cd6ac
Accept-Encoding: gzip, deflate, br
X-Forwarded-Port: 8080
Content-Type: application/json
    
~~~



![image-20240819130310522](http://47.101.155.205/image-20240819130310522.png)

![image-20240819125301054](http://47.101.155.205/image-20240819125301054.png)

![image-20240819125210435](http://47.101.155.205/image-20240819125210435.png)



## 9.TLS和SSL

支持处理https请求协议。

~~~bash
# 生成keysotre文件
# -alias gateway 对应springboot配置server.ssl.key-alias
# -storetype PKCS12 对应springboot配置server.ssl.key-sore-type
# -keystore keystore.p12(文件名) 对应springboot配置server.ssl.key-sore(文件路径)
keytool -genkeypair -alias gateway -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
# 密码窗口输入 对应springboot配置server.ssl.key-sore-password

~~~



~~~yaml
server:
  ssl:
    enabled: true
    key-alias: gateway
    key-store-password: oycm12
    key-store: keystore.p12
    key-store-type: PKCS12

~~~



支持路由到http/https下游，路由到https，以下配置表示信任下游所有证书。

~~~yaml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          useInsecureTrustManager: true

~~~



### 9.1.TLS连接

配置HTTPS连接时间

~~~yaml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          handshake-timeout-millis: 10000
          close-notify-flush-timeout-millis: 3000
          close-notify-read-timeout-millis: 0

~~~



## 10.配置

Spring Cloud Gateway的配置有RouteDefinition对象集合驱动。

默认情况，PropertiesRouteDefinitionLocator加载配置通过SpringBoot的@ConfigurationProperties注解机制。

一般情况下，使用配置文件路由请求是足够的，也可以使用外部数据源加载路由，例如数据库，后续将支持Redis、MongoDB和Cassandra作为数据源，基于RouteDefinitionLocator实现。RedisRouteDefinitionRepository



### 10.1.路由指标

添加spring-boot-starter-actuator依赖之后，监控指标默认启动。

默认spring.cloud.gateway.metrics.enabled=true。

/actuator/metrics/spring.cloud.gateway.routes.count查询

![image-20240821171320036](http://47.101.155.205/image-20240821171320036.png)



## 11.路由元数据配置

每个路由配置额外参数。

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route_with_metadata
          uri: http://httpbin.org/get
          metadata:
            optionName: "OptionValue"
            compositeObject:
              name: "value"
            iAmNumber: 1
          predicates: 
            - Path=/get/**

```

~~~java
Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
// 获取所有的原数据key=value
log.info(route.getMetadata());
// 获取指定名称的原数据key的value
log.info(route.getMetadata().get("optionName"));

~~~

![image-20240821172140235](http://47.101.155.205/image-20240821172140235.png)





## 12.Http超时配置

可以为所有的路由或特定的路由配置http超时时间(连接和响应)。

### 12.1.全局配置

connect-timeout配置时间单位为毫秒；

response-timeout配置时间遵循java.time.Duration格式，携带单位；

~~~yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 5s

~~~





### 12.2.每个路由配置

response-timeout、connect-timeout时间配置为毫秒单位，配置为负值表示禁用全局的配置。

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route_with_metadata
          uri: http://httpbin.org/
          metadata:
            optionName: "OptionValue"
            compositeObject:
              name: "value"
            iAmNumber: 1
            response-timeout: 1000
            connect-timeout: 2000
          predicates:
            - Path=/delay/**

~~~



代码配置格式

~~~java
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

      @Bean
      public RouteLocator customRouteLocator(RouteLocatorBuilder routeBuilder){
         return routeBuilder.routes()
               .route("test1", r -> {
                  return r.host("*.somehost.org").and().path("/somepath")
                        .filters(f -> f.addRequestHeader("header1", "header-value-1"))
                        .uri("http://someuri")
                        .metadata(RESPONSE_TIMEOUT_ATTR, 200)
                        .metadata(CONNECT_TIMEOUT_ATTR, 200);
               })
               .build();
      }

~~~





### 12.3.流式编程

支持and、or、negate操作。

~~~java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ThrottleGatewayFilterFactory throttle) {
    return builder.routes()
            .route(r -> r.host("**.abc.org").and().path("/image/png")
                .filters(f ->
                        f.addResponseHeader("X-TestHeader", "foobar"))
                .uri("http://httpbin.org:80")
            )
            .route(r -> r.path("/image/webp")
                .filters(f ->
                        f.addResponseHeader("X-AnotherHeader", "baz"))
                .uri("http://httpbin.org:80")
                .metadata("key", "value")
            )
            .route(r -> r.order(-1)
                .host("**.throttle.org").and().path("/get")
                .filters(f -> f.filter(throttle.apply(1,
                        1,
                        10,
                        TimeUnit.SECONDS)))
                .uri("http://httpbin.org:80")
                .metadata("key", "value")
            )
            .build();
}

~~~



### 12.4.结合DiscoveryClient转发请求

spring.cloud.gateway.discovery.locator.enabled=true

classpath中引入DiscoveryClient的实现依赖，例如Netflix Eureka、Consul、Zookeeper。

gateway会根据发现的客户端自动定义predicate和过滤器来route请求。默认使用/serviceId/**断言请求，serviceId来自DiscoveryClient的服务id。

过滤器默认是RewritePath过滤器，/serviceId/?(?<remaining>.*)，替换成/${remaining}，serviceId会在发送到下游请求之前从服务端剥离。

#### 12.4.1.eureka

~~~pom
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route_with_metadata
          uri: http://httpbin.org/
          metadata:
            optionName: "OptionValue"
            compositeObject:
              name: "value"
            iAmNumber: 1
            response-timeout: -1
            connect-timeout: 3000
          predicates:
            - Path=/delay/**
      discovery:
        locator:
          enabled: true # 开启基于发现客户端的route注册功能
          lower-case-service-id: true # 将服务id全部小写
eureka:
  client:
    service-url:
      defaultZone: http://192.168.125.224:8761/eureka/,http://192.168.125.225:8761/eureka/,http://192.168.125.226:8761/eureka/
logging:
  level:
    root: info

~~~

![image-20240822101533020](http://47.101.155.205/image-20240822101533020.png)

在原有的Path断言和RewritePath过滤器中再添加过滤器。要按格式才能注册成功。

下面predicate断言新增Host要求，增加断路器的过滤器(转发到本地接口)。

~~~yaml
spring.cloud.gateway.discovery.locator.predicates[0].name: Path
spring.cloud.gateway.discovery.locator.predicates[0].args[pattern]: "'/'+serviceId+'/**'"
spring.cloud.gateway.discovery.locator.predicates[1].name: Host
spring.cloud.gateway.discovery.locator.predicates[1].args[pattern]: "'**.foo.com'"
spring.cloud.gateway.discovery.locator.filters[0].name: CircuitBreaker
spring.cloud.gateway.discovery.locator.filters[0].args[name]: serviceId
spring.cloud.gateway.discovery.locator.filters[0].args[fallbackUri]: "'forward:/demo/'+serviceId"
spring.cloud.gateway.discovery.locator.filters[1].name: RewritePath
spring.cloud.gateway.discovery.locator.filters[1].args[regexp]: "'/' + serviceId + '/?(?<remaining>.*)'"
spring.cloud.gateway.discovery.locator.filters[1].args[replacement]: "'/${remaining}'"
~~~

![image-20240822112901286](http://47.101.155.205/image-20240822112901286.png)

![image-20240822112926427](http://47.101.155.205/image-20240822112926427.png)



## 13.netty请求日志

配置系统参数-Dreactor.netty.http.server.accessLogEnabled=true(java启动参数)



### 13.1.log.xml日志结果说明

logger-日志记录器名称是log创建指定的类名？

rollingPolicy-是否只能出现在相关的appender的标签块中？

log.xml配置标签块：

1. property：定义属性，文件中全局使用；
2. appender：日志输出目标：ConsoleAppender、FileAppender、RollingFileAppender、AsyncAppender等，每个Appender需要通过appender标签定义，并在root或logger中通过appender-ref引用；
3. rollingPolicy ：控制日志文件的滚动策略，如按时间、按文件大小；
   1. ch.qos.logback.core.rolling.TimeBasedRollingPolicy；
   2. ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy；
4. pattern：定义日志的输出格式；
   1. %d{日期格式}：打印时间；
   2. %thread：线程名称；
   3. %level：日志级别，%-5level不够5个字符，后面空格结束；
   4. %msg：日志消息；
   5. %n：换行符；
   6. %logger{length}：日志记录器名称，length限制长度；
   7. %class：日志记录器名称(全路径名称)；
   8. %file：触发日志的源文件名；
   9. %M：触发日志的代码方法名；
   10. %L：触发日志的源文件行号；
   11. %line：触发日志的源文件行号；
   12. %X{traceId}：
   13. %X{spanId}：
5. logger：指定特定包或类的日志级别和输出目标；
   1. name：控制包或类；
   2. level：控制日志级别；
   3. additivity：默认true，会将该logger的记录传递到父级Logger，false则不会；，例如其中一个logger1(假如配置包为com.org.controller)指向一个输出至指定文件，如果不继承，则如果有另一个logger(com.org)则这个日志只会输出至logger1的位置，继承则会logger1使用其配置输出，在logger以其配置输出；
   4. appender-ref：指向输出目标，appender的name；
6. 日志级别：TRACE、DEBUD、INFO、WARN、ERROR由低到高，设置日志级别意味着低于此级别的日志不会输出，例如设置为WARN，INFO级别的日志不会输出；



### 13.2.AsyncAppender用法

日志在控制台打印，且异步输出至file。

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="INFO">

    <!-- 配置输出日志到控制台 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss} [%-5level] [%thread] [%logger{36}.%M.%L]: %msg%n</pattern>
        </encoder>
    </appender>


    <!-- 配置输出日志到File,使用时间滚动策略 -->
    <appender name="FILE_TIME" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志文件的名称模式 -->
            <fileNamePattern>logs/logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 启动是否清理过期日志,过期日志根据maxHistory最大文件数 -->
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
            <!-- 30个最大文件 -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <!-- 日志输出格式 -->
            <pattern>%d{HH:mm:ss.SSS}[%X{traceId},%X{spanId}] [%-5level] [%thread] %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>

    <!-- 配置输出日志到File,使用大小和时间策略 -->
    <appender name="FILE_SIZE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 当前日志文件的名称 -->
        <!--<file>logs/logFile.log</file>-->

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 日志文件的名称模式 -->
            <fileNamePattern>logs/logFile.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 单个日志文件的最大大小 -->
            <maxFileSize>10MB</maxFileSize>
            <!-- 保留的最大历史文件数 -->
            <maxHistory>30</maxHistory>
            <!-- 最大日志存储总量 -->
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>

        <encoder>
            <!-- 日志输出格式 -->
            <pattern>%d{HH:mm:ss.SSS}[%X{traceId},%X{spanId}] [%-5level] [%thread] %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>

    <!-- 配置AsyncAppender,异步输出日志到FILE配置 -->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="FILE_TIME" />
        <!-- 存储日志事件的队列的大小,默认256 -->
        <queueSize>512</queueSize>
        <!-- 0表示满时清理低优先级队列日志(在队列中怎么判断日志的优先级?),其他正值表示表示超过多少%开始清理,默认值时queueSize/5 -->
        <discardingThreshold>0</discardingThreshold>
        <!--默认false,队列满会阻塞调用线程,true则不会-->
        <neverBlock>true</neverBlock>
    </appender>

    <root level="INFO">
        <!--控制台-->
        <appender-ref ref="CONSOLE"/>
        <!--异步实际滚动日志输出-->
        <appender-ref ref="ASYNC"/>
        <!--时间+文件大小滚动-->
        <!--<appender-ref ref="FILE_SIZE"/>-->
    </root>

</configuration>

~~~



## 14.CORS跨域配置

### 14.1.全局CORS配置

以下配置允许跨域源头来自docs.spring.io的所有get请求。

~~~yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "https://docs.spring.io"
            allowedMethods:
            - GET

~~~

配置spring.cloud.gateway.globalcors.add-to-simple-url-handler-mapping为true(默认false)，false：只有gateway路由处理的请求才允许跨域；true：gate所有的请求配置都应用跨域规则。



### 14.2.局部CORS配置

基于路由的跨域配置

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: custom_route
          uri: http://example.org
          predicates:
            - Path=/api/**
          filters:
            - name: "CorsResponseHeader"
              args:
                allowedOrigins: "http://example.com"
                allowedMethods: "GET,POST"
                allowedHeaders: "*"
                allowCredentials: true


~~~





### 14.3.Spring WebFlux

通过注入CorsWebFilter过滤器实现解决跨域，gateway所有的请求都会配置。

~~~java
package com.example.springcloudgatewaysimple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author ouyangcm
 * create 2024/8/23 11:28
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://example.com");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}

~~~



### 14.4.手动自定义过滤器处理

{FilterName}GatewayFilterFactory继承了AbstractGatewayFilterFactory，往容器中注入了该过滤器，过滤器要生效，需要在路由中应用过滤器。

~~~java
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

~~~



~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route_with_metadata
          uri: http://httpbin.org/
          metadata:
            optionName: "OptionValue"
            compositeObject:
              name: "value"
            iAmNumber: 1
            response-timeout: -1
            connect-timeout: 3000
          predicates:
            - Path=/delay/**
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      default-filters:
        - name: CustomCorsFilter # 配置过滤器生效,不可缺少
eureka:
  client:
    service-url:
      defaultZone: http://192.168.125.224:8761/eureka/,http://192.168.125.225:8761/eureka/
logging:
  level:
    root: info
spring.cloud.gateway.discovery.locator.predicates[0].name: Path
spring.cloud.gateway.discovery.locator.predicates[0].args[pattern]: "'/'+serviceId+'/**'"
spring.cloud.gateway.discovery.locator.predicates[1].name: Host
spring.cloud.gateway.discovery.locator.predicates[1].args[pattern]: "'**.foo.com'"
spring.cloud.gateway.discovery.locator.filters[0].name: CircuitBreaker
spring.cloud.gateway.discovery.locator.filters[0].args[name]: serviceId
spring.cloud.gateway.discovery.locator.filters[0].args[fallbackUri]: "'forward:/demo/'+serviceId"
spring.cloud.gateway.discovery.locator.filters[1].name: RewritePath
spring.cloud.gateway.discovery.locator.filters[1].args[regexp]: "'/' + serviceId + '/?(?<remaining>.*)'"
spring.cloud.gateway.discovery.locator.filters[1].args[replacement]: "'/${remaining}'"


~~~



## 15.Actuator API



~~~properties
management.endpoint.gateway.enabled=true # default value
management.endpoints.web.exposure.include=gateway

~~~

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: myRoute
          uri: http://httpbin.org/get
          predicates:
            - Path=/get/**
      metrics:
        enabled: true
  application:
    name: spring-cloud-gateway-simple
management:
  endpoints:
    web:
      exposure:
        include: ["metrics", "gateway", "actuator"]
  endpoint:
    gateway:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true


~~~



### 15.1.route查询

/actuator/gateway/routes

org.springframework.cloud.gateway.actuate.GatewayLegacyControllerEndpoint

org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint

类中可以查询哪些接口可以调用。

![image-20240823163048254](http://47.101.155.205/image-20240823163048254.png)



~~~properties
spring.cloud.gateway.actuator.verbose.enabled=false

~~~

![image-20240823163726862](http://47.101.155.205/image-20240823163726862.png)

![image-20240823163632878](http://47.101.155.205/image-20240823163632878.png)



### 15.2.检索过滤器

#### 15.2.1.全局过滤器

/actuator/gateway/globalfilters显示作用于所有routes的

过滤器的toStirng方法，getOrder返回值。

![image-20240823164804225](http://47.101.155.205/image-20240823164804225.png)





#### 15.2.2.路由过滤器

/actuator/gateway/routefilters显示所有GatewayFilter工厂对象，这些过滤器可以作用于特定的路由。

![image-20240823165827192](http://47.101.155.205/image-20240823165827192.png)



### 15.3.刷新路由缓存

Post：/actuator/gateway/refresh，没有响应体。



### 15.4.查询定义的所有路由信息



![image-20240823170603505](http://47.101.155.205/image-20240823170603505.png)

spring.cloud.gateway.actuator.verbose.enabled=false的效果如下

![image-20240823171925548](http://47.101.155.205/image-20240823171925548.png)

上面的json格式是因为使用配置创建的路由，接口创建路由以下面json格式为准。

~~~json
{
  "id": "first_route",
  "predicates": [{
    "name": "Path",
    "args": {"_genkey_0":"/first"}
  }],
  "filters": [],
  "uri": "https://www.uri-destination.org",
  "order": 0
}

~~~





### 15.5.查询特定路由的信息

/actuator/gateway/routes/{route_id}

![image-20240823170958270](http://47.101.155.205/image-20240823170958270.png)



### 15.6.创建/删除特定路由

POST：/actuator/gateway/routes/{route_id}，请求体为JSON，以spring.cloud.gateway.actuator.verbose.enabled=false的查询路由信息Json格式为准；

DELETE：/actuator/gateway/routes/{route_id}，删除路由；



~~~json
{
    "route_id": "delay",
    "route_definition": {
        "id": "delay",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/delay/**"
                }
            }
        ],
        "filters": [],
        "uri": "http://httpbin.org/",
        "metadata": {},
        "order": 0
    },
    "order": 0
}

~~~

![image-20240823172541915](http://47.101.155.205/image-20240823172541915.png)

![image-20240823173110791](http://47.101.155.205/image-20240823173110791.png)





### 15.7.所有端点接口

![image-20240823171515170](http://47.101.155.205/image-20240823171515170.png)





### 15.8.在多个网关共享路由

spring cloud gateway提供两个RouteDefinitionRepository(路由定义仓库)的实现类。一个是InMemoryRouteDefinitionRepository，仅保存在gateway的内存中，这个类型不能实现跨多个网关实例填充路由。

在gateway集群中共享路由，RedisRouteDefinitionRepository可以实现。开启这个功能，需要开启spring.cloud.gateway.redis-route-definition-repository.enabled=true。引入spring-boot-starter-data-redis-reactive依赖。

redis中的key有格式要求routedefinition_。

![image-20240826135121656](http://47.101.155.205/image-20240826135121656.png)



## 16.故障排除

### 16.1.日志级别

DEBUG和TRACE的日志级别消息，以下的包中，包含一个可用信息：

1. org.springframework.cloud.gateway
2. org.springframework.http.server.reactive
3. org.springframework.web.reactive
4. org.springframework.boot.autoconfigure.web
5. reactor.netty
6. redisratelimiter



### 16.2.窃听请求/响应

reactor.netty的日志级别设为DEBUG/TRACE，能够记录发送/接收的请求体和请求头。

启用功能需要有以下配置。

~~~properties
spring.cloud.gateway.httpserver.wiretap=true
spring.cloud.gateway.httpclient.wiretap=true

~~~

![image-20240826151107962](http://47.101.155.205/image-20240826151107962.png)





## 17.开发指南



### 17.1.自定义路由断言工厂

实现RoutePredicateFactory接口，将对象作为Bean注入在容器中。也可以基于抽象类AbstractRoutePredicateFactory扩展。

~~~java
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


~~~





### 17.2.自定义过滤器工厂

实现GatewayFilterFactory接口，作为Bean注入在容器中。可以继承抽象类AbstractGatewayFilterFactory实现。

建议定义的类名以GatewayFilterFactory结尾，引用过滤器则使用GatewayFilterFactory之前的作为过滤器名称。也可以不按这个规则使用，后续可能会不支持。

~~~java
package com.example.springcloudgatewaysimple.filter.facotry;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 前置过滤器
 * @author ouyangcm
 * create 2024/8/27 15:27
 */
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

    public PreGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
        return (exchange, chain) -> {
            // If you want to build a "pre" filter you need to manipulate the request before calling chain.filter
            // 在chain.filter之前执行代码的是前置过滤器
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            //use builder to manipulate the request
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    public static class Config {
        // 存放定义过滤器的配置属性内容
    }

}

~~~



~~~java
package com.example.springcloudgatewaysimple.filter.facotry;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * 后置过滤器
 * @author ouyangcm
 * create 2024/8/27 15:33
 */
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {

    public PostGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
        return (exchange, chain) -> {
            // chain.filter(exchange)之后执行的是后置过滤器
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                //Manipulate the response in some way
            }));
        };
    }

    public static class Config {
        //放定义过滤器的属性
    }

}

~~~



#### 17.2.1网关拒绝请求

![image-20240827165407346](http://47.101.155.205/image-20240827165407346.png)



### 17.3.自定义全局过滤器

实现GlobalFilter接口，并作为Bean注入到容器中。

#### 17.3.1

~~~java
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

~~~



#### 17.3.2

~~~java
@Bean
public GlobalFilter customGlobalFilter() {
    return (exchange, chain) -> exchange.getPrincipal()
        .map(Principal::getName)
        .defaultIfEmpty("Default User")
        .map(userName -> {
          //adds header to proxied request
          exchange.getRequest().mutate().header("CUSTOM-REQUEST-HEADER", userName).build();
          return exchange;
        })
        .flatMap(chain::filter);
}

@Bean
public GlobalFilter customGlobalPostFilter() {
    return (exchange, chain) -> chain.filter(exchange)
        .then(Mono.just(exchange))
        .map(serverWebExchange -> {
          //adds header to response
          serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
              HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
          return serverWebExchange;
        })
        .then();
}

~~~



## 18.不使用配置构建gateway

引入spring-cloud-gateway-mvc或spring-cloud-gateway-webflux的依赖

~~~xml
<!-- spring-cloud-gateway-webflux 依赖 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-gateway-webflux</artifactId>
	<version>3.1.0</version>
</dependency>

~~~

gateway作为一个controller处理请求，使用ProxyExchange对请求进行后续处理。转发请求到下游之前，添加请求参数，请求头添加参数等。

默认情况下支持cookie和authorization的请求头，但是不支持x-forwarded-*代理相关的请求头。

![image-20240827161954748](http://47.101.155.205/image-20240827161954748.png)

~~~java
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

~~~





## 19.其他配置项介绍

https://docs.spring.io/spring-cloud-gateway/docs/3.1.0/reference/html/appendix.html