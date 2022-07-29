package web.links.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import web.links.model.LinkModel;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkDto {
    private String id;
    private String ownerId;
    @JsonProperty("private")
    private boolean private_;
    private boolean disabled;
    private String destination;
    private String source;
    private ZonedDateTime created;
    private ZonedDateTime modified;

    public LinkDto() {
    }

    public LinkDto(String id, String ownerId, boolean private_, boolean disabled, String destination, String source, ZonedDateTime created, ZonedDateTime modified) {
        this.id = id;
        this.ownerId = ownerId;
        this.private_ = private_;
        this.disabled = disabled;
        this.destination = destination;
        this.source = source;
        this.created = created;
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPrivate_() {
        return private_;
    }

    public void setPrivate_(final boolean private_) {
        this.private_ = private_;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(final ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getModified() {
        return modified;
    }

    public void setModified(final ZonedDateTime modified) {
        this.modified = modified;
    }

    public static LinkDto fromModel(LinkModel model) {
        return new LinkDto(model.linkId(), model.ownerId(), model.private_(), model.disabled(), model.destination(), model.source(), model.created(), model.modified());
    }
}
