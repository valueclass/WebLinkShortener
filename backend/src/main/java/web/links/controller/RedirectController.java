package web.links.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import web.links.exception.LinkAccessDeniedException;
import web.links.exception.LinkNotFoundException;
import web.links.service.LinkService;
import web.links.utils.Utils;

import java.net.URI;

@Controller
public class RedirectController {
    @Autowired
    private LinkService links;

    public Mono<ServerResponse> redirect(final ServerRequest request) {
        return Utils.userId(request)
                .switchIfEmpty(Mono.just(""))
                .flatMap(userId -> links.findDestination(userId, request.pathVariable("name")))
                .flatMap(destination -> ServerResponse.permanentRedirect(URI.create(destination)).build())
                .onErrorResume(LinkAccessDeniedException.class, $ -> Mono.empty())
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")));
    }
}
