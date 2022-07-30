package web.links.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyLinkDto {
    @JsonProperty("private")
    private boolean private_;
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
