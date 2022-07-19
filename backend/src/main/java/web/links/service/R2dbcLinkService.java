package web.links.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import web.links.dto.LinkDto;
import web.links.repository.LinkRepository;

@Service
public class R2dbcLinkService implements LinkService {
    @Autowired
    private LinkRepository links;

    @Override
    public Flux<LinkDto> allLinks() {
        return links.findAll().map(LinkDto::fromModel);
    }
}
