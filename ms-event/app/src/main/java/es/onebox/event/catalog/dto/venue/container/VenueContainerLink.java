package es.onebox.event.catalog.dto.venue.container;

import java.io.Serial;
import java.io.Serializable;

public class VenueContainerLink extends VenueContainerItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 7963866819704243524L;

    private Integer originId;
    private Integer destinationId;
    private String refId;

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
