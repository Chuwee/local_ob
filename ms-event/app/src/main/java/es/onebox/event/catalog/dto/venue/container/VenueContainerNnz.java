package es.onebox.event.catalog.dto.venue.container;

import java.io.Serial;
import java.io.Serializable;

public class VenueContainerNnz extends VenueContainerItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 7963866819704243524L;

    private String name;
    private Integer sectorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }
}
