package web.links.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import web.links.controller.v1.LinkController;

@Component
public class ApiControllerRouter {

    @Bean
    public RouterFunction<ServerResponse> routeV1(LinkController links) {
        RouterFunction<ServerResponse> apiRouter = RouterFunctions.route()
                .GET("/links", links::allLinks)
                .GET("/links/{id}", links::findLink)
                .POST("/links", links::createLink)
                .DELETE("/links/{id}", links::deleteLink)
                .PATCH("/links/{id}", links::updateLink)
                .build();

        return RouterFunctions.nest(RequestPredicates.path("/api/v1"), apiRouter);
    }
}
