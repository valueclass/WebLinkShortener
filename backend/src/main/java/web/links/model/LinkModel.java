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
}
