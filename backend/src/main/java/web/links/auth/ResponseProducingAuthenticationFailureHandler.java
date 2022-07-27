package web.links.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.util.List;

public class ResponseProducingAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private final HandlerStrategies strategies = HandlerStrategies.withDefaults();

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException exception) {
        final HttpStatus statusCode = exceptionStatusCode(exception);

        return ServerResponse.status(statusCode).body(Mono.just(exception), AuthenticationException.class)
                .flatMap(response -> response.writeTo(exchange.getExchange(), new ServerResponse.Context() {
                    @Override
                    public List<HttpMessageWriter<?>> messageWriters() {
                        return strategies.messageWriters();
                    }

                    @Override
                    public List<ViewResolver> viewResolvers() {
                        return strategies.viewResolvers();
                    }
                }));
    }

    private static HttpStatus exceptionStatusCode(AuthenticationException exception) {
        if (exception instanceof AuthenticationServiceException)
            return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.UNAUTHORIZED;
    }
}
