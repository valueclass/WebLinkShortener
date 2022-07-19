package web.links.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.service.LinkService;

import java.net.URI;

@Controller
public class LinkController {
    @Autowired
    private LinkService service;

    public Mono<ServerResponse> allLinks(final ServerRequest request) {
        return ServerResponse.ok().body(service.allLinks(), LinkDto.class);
    }

    public Mono<ServerResponse> findLink(final ServerRequest request) {
        return ServerResponse.accepted().build();
    }

    public Mono<ServerResponse> createLink(final ServerRequest request) {
        return ServerResponse.created(URI.create("http://exmaple.com/")).build();
    }

    public Mono<ServerResponse> deleteLink(final ServerRequest request) {
        return ServerResponse.notFound().build();
    }

    public Mono<ServerResponse> updateLink(final ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
