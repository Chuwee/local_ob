package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import es.onebox.mgmt.common.LimitlessValueDTO;

import java.io.Serial;
import java.io.Serializable;

@JsonPropertyOrder({"venue_id","id","name","capacity","default","notes"})
public class VenueSpaceDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("venue_id")
    private Long venueId;
    @JsonProperty("id")
    private Long spaceId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("capacity")
    private LimitlessValueDTO capacity;
    @JsonProperty("default")
    private Boolean isDefault;
    @JsonProperty("notes")
    private String notes;


    public Long getVenueId() {
        return venueId;
    }
    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Long getSpaceId() {
        return spaceId;
    }
    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public LimitlessValueDTO getCapacity() {
        return capacity;
    }
    public void setCapacity(LimitlessValueDTO capacity) {
        this.capacity = capacity;
    }

    public Boolean getDefault() {
        return isDefault;
    }
    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
