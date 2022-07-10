package web.links.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Controller
@Configuration(proxyBeanMethods = false)
public class HelloController {

    private Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().bodyValue("Hello!");
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.path("/hello"), this::hello);
    }
}
