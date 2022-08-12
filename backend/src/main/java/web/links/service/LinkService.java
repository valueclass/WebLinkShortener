package web.links.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;

public interface LinkService {
    Flux<LinkDto> allLinks(String userId, LinkQueryOptions options);
    Mono<LinkDto> createLink(String userId, ModifyLinkDto metadata);
    Mono<LinkDto> modifyLink(String userId, String linkId, ModifyLinkDto metadata);
    Mono<Void> deleteLink(String userId, String linkId);
    Mono<LinkDto> findLink(String userId, String linkId);
}
