package web.links.service;

import reactor.core.publisher.Flux;
import web.links.dto.LinkDto;

public interface LinkService {
    Flux<LinkDto> allLinks();
}
