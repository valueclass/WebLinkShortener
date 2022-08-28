package web.links.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import javax.annotation.Resource;

@Configuration
public class ControllerRouter {

    @Bean
    public RouterFunction<ServerResponse> routeRedirect(final RedirectController redirect) {
        return RouterFunctions.route(RequestPredicates.GET("/link/{name}"), redirect::redirect);
    }
}
