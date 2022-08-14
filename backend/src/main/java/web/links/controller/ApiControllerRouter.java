package web.links.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import web.links.controller.v1.LinkController;
import web.links.controller.v1.UserController;

@Component
public class ApiControllerRouter {

    @Bean
    public RouterFunction<ServerResponse> routeV1(final LinkController links, final UserController users) {
        RouterFunction<ServerResponse> apiRouter = RouterFunctions.route()
                .GET("/links", links::allLinks)
                .GET("/links/{id}", links::findLink)
                .POST("/links", links::createLink)
                .POST("/links/{id}/disable", links::disableLink)
                .POST("/links/{id}/enable", links::enableLink)
                .DELETE("/links/{id}", links::deleteLink)
                .PATCH("/links/{id}", links::updateLink)
                .POST("/users/password", users::updatePassword)
                .GET("/users/whoami", users::whoami)
                .build();

        return RouterFunctions.nest(RequestPredicates.path("/api/v1"), apiRouter);
    }
}
