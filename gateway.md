# gateway

不同版本文档：https://docs.spring.io/spring-cloud-gateway/docs/

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

