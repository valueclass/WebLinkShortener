package web.links.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

public class HttpStatusReturningAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final HttpStatus status;

    public HttpStatusReturningAuthenticationSuccessHandler() {
        this(HttpStatus.OK);
    }

    public HttpStatusReturningAuthenticationSuccessHandler(HttpStatus status) {
        this.status = status;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return Mono.fromRunnable(() -> exchange.getExchange().getResponse().setStatusCode(status));
    }
}
