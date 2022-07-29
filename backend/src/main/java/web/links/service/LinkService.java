package web.links.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;

public interface LinkService {
    Flux<LinkDto> allLinks();
    Mono<LinkDto> createLink(String userId, ModifyLinkDto metadata);
}
