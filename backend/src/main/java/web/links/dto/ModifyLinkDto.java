package web.links.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyLinkDto {
    @JsonProperty("private")
    private boolean private_;
    private boolean disabled;
    private String source;
    private String destination;

    public ModifyLinkDto() {
    }

    public boolean isPrivate() {
        return private_;
    }

    public void setPrivate(final boolean private_) {
        this.private_ = private_;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }
}
