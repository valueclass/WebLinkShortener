package web.links.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;
import web.links.service.LinkService;
import web.links.utils.Utils;

import java.net.URI;

@Controller
public class LinkController {
    @Autowired
    private LinkService service;

    public Mono<ServerResponse> allLinks(final ServerRequest request) {
        return Utils.userId(request)
                .defaultIfEmpty("")
                .map(userId -> service.allLinks(userId))
                .flatMap(flux -> ServerResponse.ok().body(flux, LinkDto.class));
    }

    public Mono<ServerResponse> findLink(final ServerRequest request) {
        return Utils.userId(request)
                .defaultIfEmpty("")
                .flatMap(userId -> service.findLink(userId, request.pathVariable("id")))
                .flatMap(link -> ServerResponse.ok().bodyValue(link));
    }

    public Mono<ServerResponse> createLink(final ServerRequest request) {
        return Utils.userId(request)
                .zipWith(request.bodyToMono(ModifyLinkDto.class))
                .flatMap(tuple -> service.createLink(tuple.getT1(), tuple.getT2()))
                .flatMap(link -> ServerResponse.created(URI.create("/api/v1/links/" + link.getId())).bodyValue(link));
    }

    public Mono<ServerResponse> deleteLink(final ServerRequest request) {
        return Utils.userId(request)
                .flatMap(userId -> service.deleteLink(userId, request.pathVariable("id")))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateLink(final ServerRequest request) {
        return Mono.zip(Utils.userId(request), Mono.just(request.pathVariable("id")), request.bodyToMono(ModifyLinkDto.class))
                .flatMap(tuple -> service.modifyLink(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .flatMap(link -> ServerResponse.ok().bodyValue(link));
    }
}
