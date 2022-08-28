package web.links.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import web.links.dto.UserDto;
import web.links.utils.HandlerStrategiesResponseContext;

public class ResponseServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(final WebFilterExchange exchange, final Authentication authentication) {
        if (authentication.getPrincipal() instanceof final ExtendedUserDetails user) {
            return ServerResponse.ok().bodyValue(new UserDto(user.getUserId(), user.getUsername()))
                    .flatMap(response -> response.writeTo(exchange.getExchange(), new HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())));
        }

        return Mono.error(new IllegalStateException("Authentication principal passed was not an instance of ExtendedUserDetails"));
    }
}
