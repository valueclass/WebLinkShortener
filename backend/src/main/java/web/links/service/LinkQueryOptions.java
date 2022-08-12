package web.links.service;

import org.springframework.data.domain.Example;
import org.springframework.web.reactive.function.server.ServerRequest;
import web.links.model.LinkModel;

public class LinkQueryOptions {
    private final String owner;
    private final String source;
    private final String destination;
    private final Boolean disabled;
    private final Boolean private_;
    private final long max;

    private LinkQueryOptions(final String owner, final String source, final String destination, final Boolean disabled, final Boolean private_, final long max) {
        this.owner = owner;
        this.source = source;
        this.destination = destination;
        this.disabled = disabled;
        this.private_ = private_;
        this.max = max;
    }

    public Example<LinkModel> makeExample() {
        return Example.of(new LinkModel(null, null, owner, private_, disabled, destination, source, null, null));
    }

    public long getMax() {
        return max;
    }

    public static LinkQueryOptions fromRequest(final ServerRequest request) {
        final String owner = request.queryParam("owner").orElse(null);
        final String source = request.queryParam("source").orElse(null);
        final String destination = request.queryParam("destination").orElse(null);
        final Boolean disabled = request.queryParam("disabled").map(Boolean::parseBoolean).orElse(null);
        final Boolean private_ = request.queryParam("private").map(Boolean::parseBoolean).orElse(null);
        final long max = Long.parseLong(request.queryParam("max").orElse("-1"));

        return new LinkQueryOptions(owner, source, destination, disabled, private_, max);
    }
}
