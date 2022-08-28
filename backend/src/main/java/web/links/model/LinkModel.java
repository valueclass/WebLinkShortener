package web.links.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import web.links.utils.Utils;

import java.time.ZonedDateTime;
import java.util.Objects;

@Table("links")
public record LinkModel(
    @Id Integer id,
    @Column("link_id") String linkId,
    @Column("owner_id") String ownerId,
    @Column("private") Boolean private_,
    Boolean disabled,
    String destination,
    String name,
    ZonedDateTime created,
    ZonedDateTime modified
) {

    public LinkModel private_(final boolean private_) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, name, created, ZonedDateTime.now());
    }

    public LinkModel disabled(final boolean disabled) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, name, created, ZonedDateTime.now());
    }

    public LinkModel destination(final String destination) {
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, name, created, ZonedDateTime.now());
    }

    public LinkModel source(final String source) {
        final String s = source == null ? this.linkId : source;
        return new LinkModel(id, linkId, ownerId, private_, disabled, destination, s, created, ZonedDateTime.now());
    }

    public Builder mutable() {
        return new Builder(this);
    }

    public static Builder builder(final String ownerId) {
        return new Builder(ownerId);
    }

    public static class Builder {
        private final Integer id;
        private final String linkId;
        private final String ownerId;
        private Boolean private_;
        private Boolean disabled;
        private String destination;
        private String name;
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
            this.name = model.name;
            this.created = model.created;
            this.modified = model.modified;
            this.parent = model;
        }

        private Builder(final String ownerId) {
            this.id = null;
            this.linkId = Utils.generateAlphanumericId(8);
            this.ownerId = ownerId;
            this.private_ = false;
            this.disabled = false;
            this.destination = null;
            this.name = null;
            this.created = null;
            this.modified = null;
            this.parent = null;
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

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public LinkModel build() {
            Objects.requireNonNull(destination, "Cannot build LinkModel, this.destination is null");
            if (name == null) name = linkId;

            if (isModified())
                return new LinkModel(id, linkId, ownerId, private_, disabled, destination, name, created(), modified);

            return parent;
        }

        private boolean isModified() {
            return parent == null || !private_.equals(parent.private_) || !disabled.equals(parent.disabled) || !destination.equals(parent.destination) || !name.equals(parent.name);
        }

        private ZonedDateTime created() {
            modified = Utils.getTime();
            return created != null ? created : modified;
        }
    }
}
