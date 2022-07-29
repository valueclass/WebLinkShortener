package web.links.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;
import web.links.exception.InvalidLinkException;
import web.links.model.LinkModel;
import web.links.repository.LinkRepository;
import web.links.utils.Utils;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class R2dbcLinkService implements LinkService {
    @Autowired
    private LinkRepository links;

    private final UrlValidator validator = new UrlValidator(new String[]{ "http", "https" });

    @Override
    public Flux<LinkDto> allLinks() {
        return links.findAll().map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> createLink(final String userId, final ModifyLinkDto metadata) {
        final String id = Utils.generateAlphanumericId(8);
        final String source = Optional.ofNullable(metadata.getSource())
                .filter(s -> !(s.isEmpty() || s.isBlank()))
                .orElse(id);
        validateSource(source);

        final String destination = metadata.getDestination();
        validateDestination(destination);

        final ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        final LinkModel link = new LinkModel(null, id, userId, metadata.isPrivate(), metadata.isDisabled(), destination, source, now, now);

        return links.save(link).map(LinkDto::fromModel);
    }

    private void validateDestination(final String destination) {
        if (destination == null || destination.isEmpty() || destination.isBlank())
            throw new InvalidLinkException("No destination");

        if (!validator.isValid(destination))
            throw new InvalidLinkException("Destination is not a valid URL");
    }

    private void validateSource(final String source) {
        if (source.length() > 128)
            throw new InvalidLinkException("Source cannot be longer than 128 characters");
    }
}
