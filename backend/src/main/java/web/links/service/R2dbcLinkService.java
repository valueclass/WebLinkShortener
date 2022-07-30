package web.links.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.dto.LinkDto;
import web.links.dto.ModifyLinkDto;
import web.links.exception.InvalidLinkException;
import web.links.exception.LinkAccessDeniedException;
import web.links.exception.LinkNotFoundException;
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
        final LinkModel link = new LinkModel(null, id, userId, metadata.isPrivate(), false, destination, source, now, now);

        return links.save(link).map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> modifyLink(final String userId, final String linkId, final ModifyLinkDto metadata) {
        final String destination = Optional.ofNullable(metadata.getDestination())
                .filter(s -> !(s.isEmpty() || s.isBlank()))
                .orElse("");

        if (!destination.isEmpty())
            validateDestination(destination);

        final String source = Optional.ofNullable(metadata.getSource())
                .filter(s -> !(s.isEmpty() || s.isBlank()))
                .orElse("");
        validateSource(source);

        return Mono.justOrEmpty(source)
                .filter(s -> !(s.isBlank() || s.isEmpty()))
                .flatMap(s -> links.findBySource(s))
                .hasElement()
                .flatMap(exists -> exists ? Mono.error(() -> new InvalidLinkException("Source exists")) : links.findByLinkId(linkId))
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")))
                .filter(link -> link.ownerId().equals(userId))
                .switchIfEmpty(Mono.error(() -> new LinkAccessDeniedException("Cannot modify this link")))
                .map(link -> modify(link.mutable(), metadata.isPrivate(), destination, source))
                .flatMap(link -> links.save(link))
                .map(LinkDto::fromModel);
    }

    private LinkModel modify(final LinkModel.Builder builder, final boolean private_, final String destination, final String source) {
        if (!destination.isEmpty())
            builder.destination(destination);

        if (!source.isEmpty())
            builder.source(source);

        return builder.private_(private_).build();
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
