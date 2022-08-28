package web.links.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ErrorServerAuthenticationEndpoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException ex) {
        return Mono.error(new ResponseStatusException(exceptionStatusCode(ex), ex.getMessage(), ex));
    }

    private static HttpStatus exceptionStatusCode(Throwable exception) {
        if (exception instanceof AuthenticationException && !(exception instanceof AuthenticationServiceException))
            return HttpStatus.UNAUTHORIZED;

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
