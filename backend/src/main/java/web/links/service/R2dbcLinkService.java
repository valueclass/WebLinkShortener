package web.links.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// FIXME: Source can be null

@Service
public class R2dbcLinkService implements LinkService {
    @Autowired
    private LinkRepository links;

    private final UrlValidator validator = new UrlValidator(new String[]{"http", "https"});

    @Override
    public Flux<LinkDto> allLinks(final String userId) {
        return links.findAll()
                .filter(model -> checkAccess(model, userId))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> createLink(final String userId, final ModifyLinkDto metadata) {
        final String destination = metadata.getDestination();
        final String source = metadata.getSource();
        final boolean private_ = metadata.getPrivate() != null && metadata.getPrivate();

        requireField(destination, "destination");
        requireField(source, "source");
        requireValidDestinationUrl(destination);

        return checkSourceAndBuild(source, LinkModel.builder(userId))
                .map(builder -> builder.destination(destination).private_(private_).build())
                .flatMap(model -> links.save(model))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> modifyLink(final String userId, final String linkId, final ModifyLinkDto metadata) {
        return findLinkAndCheckAccess(linkId, userId, "Cannot modify this link")
                .flatMap(model -> checkSourceAndBuild(metadata.getSource(), model.mutable()))
                .map(builder -> checkDestinationAndBuild(metadata.getDestination(), builder))
                .map(builder -> Optional.ofNullable(metadata.getPrivate()).map(builder::private_).orElse(builder).build())
                .flatMap(model -> links.save(model))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<Void> deleteLink(final String userId, final String linkId) {
        return findLinkAndCheckAccess(linkId, userId, "Cannot delete this link")
                .flatMap(model -> links.delete(model));
    }

    @Override
    public Mono<LinkDto> findLink(final String userId, final String linkId) {
        return links.findByLinkId(linkId)
                .filter(model -> checkAccess(model, userId))
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")))
                .map(LinkDto::fromModel);
    }

    private void requireValidDestinationUrl(final String destination) {
        if (!validator.isValid(destination))
            throw new InvalidLinkException("Destination is not a valid URL");
    }

    private void requireField(final String field, final String name) {
        if (field == null || field.isBlank())
            throw new InvalidLinkException("No " + name);
    }

    private void requireSourceLength(final String source) {
        if (source.length() > 128)
            throw new InvalidLinkException("Source cannot be longer than 128 characters");
    }

    private Mono<LinkModel> findLinkAndCheckAccess(final String linkId, final String userId, final String accessDeniedMessage) {
        return links.findByLinkId(linkId)
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")))
                .filter(link -> link.ownerId().equals(userId))
                .switchIfEmpty(Mono.error(() -> new LinkAccessDeniedException(accessDeniedMessage)));
    }

    private Mono<LinkModel.Builder> checkSourceAndBuild(final String source, final LinkModel.Builder builder) {
        if (source == null || source.isBlank())
            return Mono.just(builder);

        requireSourceLength(source);

        return links.findBySource(source)
                .hasElement()
                .flatMap(b -> b ? Mono.error(new InvalidLinkException("Source is not unique")) : Mono.just(builder.source(source)))
                .switchIfEmpty(Mono.just(builder));
    }

    private LinkModel.Builder checkDestinationAndBuild(final String destination, final LinkModel.Builder builder) {
        if (destination == null || destination.isBlank())
            return builder;

        requireValidDestinationUrl(destination);

        return builder.destination(destination);
    }

    private boolean checkAccess(final LinkModel model, final String userId) {
        return !model.disabled() && (!model.private_() || model.ownerId().equals(userId));
    }
}
