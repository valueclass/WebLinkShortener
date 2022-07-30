package web.links.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import web.links.utils.Utils;

import java.time.Clock;
import java.time.ZonedDateTime;

@Table("links")
public record LinkModel(
    @Id Integer id,
    @Column("link_id") String linkId,
    @Column("owner_id") String ownerId,
    @Column("private") Boolean private_,
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

    public LinkModel source(final String source) {
        final String s = source == null ? this.linkId : source;
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, s, created, ZonedDateTime.now());
    }

    public Builder mutable() {
        return new Builder(this);
    }

    public static class Builder {
        private final Integer id;
        private final String linkId;
        private final String ownerId;
        private Boolean private_;
        private Boolean disabled;
        private String destination;
        private String source;
        private final ZonedDateTime created;
        private ZonedDateTime modified;
        private final LinkModel parent;

        private Builder(final LinkModel model) {
            this.id = model.id;
            this.linkId = model.linkId;
            this.ownerId = model.ownerId;
            this.private_ = model.private_;
            this.disabled = model.disabled;
            this.destination = model.destination;
            this.source = model.source;
            this.created = model.created;
            this.modified = model.modified;
            this.parent = model;
        }

        public Builder private_(final boolean private_) {
            this.private_ = private_;
            return this;
        }

        public Builder disabled(final boolean disabled) {
            this.disabled = disabled;
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

        public LinkModel build() {
            if (isModified())
                return new LinkModel(id, linkId, ownerId, private_, disabled, destination, source, created(), modified);

            return parent;
        }

        private boolean isModified() {
            return !private_.equals(parent.private_) || !disabled.equals(parent.disabled) || !destination.equals(parent.destination) || !source.equals(parent.source);
        }

        private ZonedDateTime created() {
            modified = Utils.getTime();
            return created != null ? created : modified;
        }
    }
}
