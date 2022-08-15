package web.links.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;
import web.links.exception.BadRequestException;
import web.links.exception.InvalidLinkQueryOptionsException;
import web.links.exception.LinkAccessDeniedException;
import web.links.exception.LinkNotFoundException;
import web.links.service.LinkQueryOptions;
import web.links.service.LinkService;
import web.links.utils.Utils;

import java.net.URI;

@Controller
public class LinkController {
    @Autowired
    private LinkService service;

    public Mono<ServerResponse> allLinks(final ServerRequest request) {
        try {
            final LinkQueryOptions options = LinkQueryOptions.fromRequest(request);

            return Utils.userId(request)
                    .defaultIfEmpty("")
                    .map(userId -> service.allLinks(userId, options))
                    .flatMap(flux -> ServerResponse.ok().body(flux, LinkDto.class));
        } catch (InvalidLinkQueryOptionsException e) {
            return Mono.error(new BadRequestException("Invalid query parameters: " + e.getMessage(), e));
        }
    }

    public Mono<ServerResponse> findLink(final ServerRequest request) {
        return Utils.userId(request)
                .defaultIfEmpty("")
                .flatMap(userId -> service.findLink(userId, request.pathVariable("id")))
                .onErrorMap(LinkAccessDeniedException.class, ex -> new LinkNotFoundException("Link not found"))
                .flatMap(link -> ServerResponse.ok().bodyValue(link));
    }

    public Mono<ServerResponse> createLink(final ServerRequest request) {
        return Utils.userId(request)
                .zipWith(request.bodyToMono(ModifyLinkDto.class))
                .flatMap(tuple -> service.createLink(tuple.getT1(), tuple.getT2()))
                .flatMap(link -> ServerResponse.created(linkLocation(link)).bodyValue(link));
    }

    public Mono<ServerResponse> deleteLink(final ServerRequest request) {
        return Utils.userId(request)
                .flatMap(userId -> service.deleteLink(userId, request.pathVariable("id")))
                .onErrorMap(LinkAccessDeniedException.class, ex -> new LinkNotFoundException("Link not found"))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateLink(final ServerRequest request) {
        return Mono.zip(Utils.userId(request), Mono.just(request.pathVariable("id")), request.bodyToMono(ModifyLinkDto.class))
                .flatMap(tuple -> service.modifyLink(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .onErrorMap(LinkAccessDeniedException.class, ex -> new LinkNotFoundException("Link not found"))
                .flatMap(link -> ServerResponse.ok().bodyValue(link));
    }

    public Mono<ServerResponse> disableLink(final ServerRequest request) {
        return Utils.userId(request)
                .flatMap(userId -> service.disableLink(userId, request.pathVariable("id")))
                .onErrorMap(LinkAccessDeniedException.class, ex -> new LinkNotFoundException("Link not found"))
                .flatMap(link -> ServerResponse.noContent().build())
                .switchIfEmpty(Mono.defer(() -> ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> enableLink(final ServerRequest request) {
        return Utils.userId(request)
                .flatMap(userId -> service.enableLink(userId, request.pathVariable("id")))
                .onErrorMap(LinkAccessDeniedException.class, ex -> new LinkNotFoundException("Link not found"))
                .flatMap(link -> ServerResponse.ok().bodyValue(link))
                .switchIfEmpty(Mono.defer(() -> ServerResponse.noContent().build()));
    }

    private static URI linkLocation(final LinkDto link) {
        return URI.create("/api/v1/links/" + link.getId());
    }
}
