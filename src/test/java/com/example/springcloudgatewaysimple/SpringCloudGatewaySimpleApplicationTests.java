package com.example.springcloudgatewaysimple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.server.PathContainer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @AutoConfigureWireMock(port = 0) 代表随机端口启动一个模拟的web客户端
 *
 * properties = {"httpbin=http://localhost:${wiremock.server.port}"} 将UriConfiguration类的属性httpbin替换成这个
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"httpbin=http://localhost:${wiremock.server.port}"})
@AutoConfigureWireMock(port = 0)
public class SpringCloudGatewaySimpleApplicationTests {


    @Autowired
    private WebTestClient webClient;

    //@Test
    public void contextLoads() throws Exception {
        //Stubs
        // 模拟一个
        stubFor(get(urlEqualTo("/get"))
                .willReturn(aResponse()
                        .withBody("{\"headers\":{\"Hello\":\"World\"}}")
                        .withHeader("Content-Type", "application/json")));
        stubFor(get(urlEqualTo("/delay/3"))
                .willReturn(aResponse()
                        .withBody("no fallback")
                        .withFixedDelay(3000)));

        webClient
                .get().uri("/get")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.headers.Hello").isEqualTo("World");

        webClient
                .get().uri("/delay/3")
                .header("Host", "www.circuitbreaker.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(
                        response -> assertThat(response.getResponseBody()).isEqualTo("demo".getBytes()));
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {

        //testPathPatternParser(true, "/get/1/");
        List<Map<String,String>> list = getList();
        System.out.println(list.get(0));


    }
    public static List getList(){
        List<String> list = new ArrayList<>();

        list.add("1");
        return list;
    }

    public static void testPathPatternParser(boolean matchTrailingSlash, String path){

        PathPatternParser pathPatternParser = new PathPatternParser();
        pathPatternParser.setMatchOptionalTrailingSeparator(matchTrailingSlash);
        List<String> list = Arrays.asList("/get/1");

        List<PathPattern> pathPatterns = new ArrayList<>();
        list.forEach(pattern -> {
            PathPattern pathPattern = pathPatternParser.parse(pattern);
            pathPatterns.add(pathPattern);
        });

        PathContainer pathContainer = PathContainer.parsePath(path);

        PathPattern match = null;
        for (int i = 0; i < pathPatterns.size(); i++) {
            PathPattern pathPattern = pathPatterns.get(i);
            if (pathPattern.matches(pathContainer)) {
                match = pathPattern;
                break;
            }
        }
        if (match != null) {
            traceMatch("Pattern", match.getPatternString(), path, true);
        }else {
            traceMatch("Pattern", list, path, false);
        }


    }

    private static void traceMatch(String prefix, Object desired, Object actual, boolean match) {

        String message = String.format("%s \"%s\" %s against value \"%s\"", prefix, desired, match ? "matches" : "does not match", actual);
        System.out.println(message);

    }
}
