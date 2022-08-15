package web.links.controller.v1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserControllerRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUsersV1(final UserController users) {
        RouterFunction<ServerResponse> usersRouter = RouterFunctions.route()
                .POST("/users/password", users::updatePassword)
                .GET("/users/whoami", users::whoami)
                .build();

        return RouterFunctions.nest(RequestPredicates.path("/api/v1"), usersRouter);
    }
}
