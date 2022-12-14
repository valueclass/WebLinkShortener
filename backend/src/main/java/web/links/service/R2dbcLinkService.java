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
import java.util.function.Predicate;
import java.util.regex.Pattern;


@Service
public class R2dbcLinkService implements LinkService {
    @Autowired
    private LinkRepository links;

    private final UrlValidator validator = new UrlValidator(new String[]{"http", "https"});
    private final Predicate<String> nameValidator = Pattern.compile("^[a-zA-Z0-9_-]{1,128}$").asMatchPredicate();

    @Override
    public Flux<LinkDto> allLinks(final String userId, final LinkQueryOptions options) {
        return limit(links.findAll(options.makeExample()), options.getMax())
                .filter(model -> checkAccess(model, userId))
                .map(LinkDto::fromModel);
    }

    private Flux<LinkModel> limit(final Flux<LinkModel> flux, final long max) {
        return max > -1 ? flux.take(max) : flux;
    }

    @Override
    public Mono<LinkDto> createLink(final String userId, final ModifyLinkDto metadata) {
        final String destination = metadata.getDestination();
        final String source = metadata.getSource();
        final boolean private_ = metadata.getPrivate() != null && metadata.getPrivate();

        requireField(destination, "destination");
        requireField(source, "name");
        requireValidDestinationUrl(destination);
        requireValidName(source);

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
                .switchIfEmpty(Mono.error(() -> new LinkAccessDeniedException("Cannot access this link")))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> disableLink(final String userId, final String linkId) {
        return findLinkAndCheckAccess(linkId, userId, "Cannot disable this link")
                .filter(model -> !model.disabled())
                .map(model -> model.mutable().disabled(true).build())
                .flatMap(model -> links.save(model))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<LinkDto> enableLink(final String userId, final String linkId) {
        return findLinkAndCheckAccess(linkId, userId, "Cannot enable this link")
                .filter(LinkModel::disabled)
                .map(model -> model.mutable().disabled(false).build())
                .flatMap(model -> links.save(model))
                .map(LinkDto::fromModel);
    }

    @Override
    public Mono<String> findDestination(final String userId, final String linkName) {
        return links.findByName(linkName)
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")))
                .filter(link -> checkAccess(link, userId))
                .switchIfEmpty(Mono.error(() -> new LinkAccessDeniedException("Cannot access this link")))
                .filter(link -> !link.disabled())
                .map(LinkModel::destination);
    }

    private void requireValidDestinationUrl(final String destination) {
        if (!validator.isValid(destination))
            throw new InvalidLinkException("Destination is not a valid URL");
    }

    private void requireValidName(final String name) {
        final int len = name.length();
        if (len > 0) {
            if (len > 128)
                throw new InvalidLinkException("Name cannot be longer than 128 characters");

            if (!nameValidator.test(name))
                throw new InvalidLinkException("Name is invalid");
        }
    }

    private void requireField(final String field, final String name) {
        if (field == null || field.isBlank())
            throw new InvalidLinkException("No " + name);
    }

    private Mono<LinkModel> findLinkAndCheckAccess(final String linkId, final String userId, final String accessDeniedMessage) {
        return links.findByLinkId(linkId)
                .switchIfEmpty(Mono.error(() -> new LinkNotFoundException("Link not found")))
                .filter(link -> link.ownerId().equals(userId))
                .switchIfEmpty(Mono.error(() -> new LinkAccessDeniedException(accessDeniedMessage)));
    }

    private Mono<LinkModel.Builder> checkSourceAndBuild(final String name, final LinkModel.Builder builder) {
        if (name == null || name.isBlank())
            return Mono.just(builder);

        requireValidName(name);

        return links.findByName(name)
                .hasElement()
                .flatMap(b -> b ? Mono.error(new InvalidLinkException("Name is not unique")) : Mono.just(builder.name(name)))
                .switchIfEmpty(Mono.just(builder));
    }

    private LinkModel.Builder checkDestinationAndBuild(final String destination, final LinkModel.Builder builder) {
        if (destination == null || destination.isBlank())
            return builder;

        requireValidDestinationUrl(destination);

        return builder.destination(destination);
    }

    private boolean checkAccess(final LinkModel model, final String userId) {
        if (model.disabled() || model.private_())
            return model.ownerId().equals(userId);

        return true;
    }
}
