package web.links.controller.v1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LinkControllerRouter {

    @Bean
    public RouterFunction<ServerResponse> routeLinksV1(final LinkController links) {
        RouterFunction<ServerResponse> linksRouter = RouterFunctions.route()
                .GET("/links", links::allLinks)
                .GET("/links/{id}", links::findLink)
                .POST("/links", links::createLink)
                .POST("/links/{id}/disable", links::disableLink)
                .POST("/links/{id}/enable", links::enableLink)
                .DELETE("/links/{id}", links::deleteLink)
                .PATCH("/links/{id}", links::updateLink)
                .build();

        return RouterFunctions.nest(RequestPredicates.path("/api/v1"), linksRouter);
    }
}
