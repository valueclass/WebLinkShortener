package web.links.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import web.links.utils.Utils;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;

@Table("links")
public record LinkModel(
    @Id Integer id,
    @Column("link_id") String linkId,
    @Column("owner_id") String ownerId,
    Boolean private_,
    Boolean disabled,
    String destination,
    String source,
    ZonedDateTime created,
    ZonedDateTime modified
) {

    public LinkModel private_(final boolean private_) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, source, created, ZonedDateTime.now());
    }

    public LinkModel disabled(final boolean disabled) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, source, created, ZonedDateTime.now());
    }

    public LinkModel destination(final String destination) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, source, created, ZonedDateTime.now());
    }

    public LinkModel source(final Optional<String> source) {
        final var s = source.orElse(this.source);
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, s, created, ZonedDateTime.now());
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String linkId;
        private String ownerId;
        private boolean private_ = false;
        private boolean disabled = false;
        private String destination;
        private String source;
        private ZonedDateTime created;

        private Builder() {
        }

        public Builder private_() {
            this.private_ = true;
            return this;
        }

        public Builder disabled() {
            this.disabled = true;
            return this;
        }

        public Builder destination(final String destination) {
            this.destination = destination;
            return this;
        }

        public Builder source(final String source) {
            this.source = source;
            return this;
        }

        public Builder randomSource() {
            this.source = null;
            return this;
        }

        public Builder createdNow() {
            this.created = ZonedDateTime.now(Clock.systemUTC());
            return this;
        }

        public Builder owner(final String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public LinkModel build() {
            final String id = Utils.generateRandomId(16);
            final String source = this.source == null ? id : this.source;
            return new LinkModel(null, id, ownerId, private_, disabled, destination, source, created, created);
        }
    }
}
