package web.links.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyLinkDto {
    @JsonProperty("private")
    private Boolean private_;
    private String source;
    private String destination;

    public ModifyLinkDto() {
    }

    public Boolean getPrivate() {
        return private_;
    }

    public void setPrivate(final Boolean private_) {
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
