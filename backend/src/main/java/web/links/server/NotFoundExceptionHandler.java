package web.links.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import web.links.utils.HandlerStrategiesResponseContext;

public class NotFoundExceptionHandler implements WebExceptionHandler {

    @Value("classpath:/resources/static/index.html")
    private Resource resource;

    public NotFoundExceptionHandler() {
    }

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        final ServerHttpRequest request = exchange.getRequest();

        if (caresAbout(request)) {
            return ServerResponse.ok().bodyValue(resource)
                    .flatMap(response -> response.writeTo(exchange, new HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())));
        }

        return Mono.error(ex);
    }

    private boolean caresAbout(ServerHttpRequest request) {
        return !request.getPath().elements().get(0).value().equals("api") && request.getHeaders().getAccept().contains(MediaType.TEXT_HTML);
    }
}
