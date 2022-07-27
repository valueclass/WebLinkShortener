package web.links.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import web.links.dto.UserCredentialsDto;

public class ApiRequestServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final HandlerStrategies strategies = HandlerStrategies.withDefaults();

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return ServerRequest.create(exchange, strategies.messageReaders())
                .bodyToMono(UserCredentialsDto.class)
                .map(credentials -> UsernamePasswordAuthenticationToken.unauthenticated(credentials.getUsername(), credentials.getPassword()));
    }
}
