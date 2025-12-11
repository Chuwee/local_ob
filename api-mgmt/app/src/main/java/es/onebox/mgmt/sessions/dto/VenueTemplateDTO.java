package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8560395444980476053L;

    private Long id;

    private String name;

    private Long capacity;

    private Boolean graphic;

    private VenueTemplateTypeDTO type;

    private VenueDTO venue;

    private IdNameDTO space;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Boolean isGraphic() { return graphic; }

    public void setGraphic(Boolean graphic) { this.graphic = graphic; }

    public VenueDTO getVenue() {
        return venue;
    }

    public void setVenue(VenueDTO venue) {
        this.venue = venue;
    }

    public IdNameDTO getSpace() {
        return space;
    }

    public void setSpace(IdNameDTO space) {
        this.space = space;
    }

    public VenueTemplateTypeDTO getType() {
        return type;
    }

    public void setType(VenueTemplateTypeDTO type) {
        this.type = type;
    }
}
