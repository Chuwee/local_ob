package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class VenueSpacePutRequest implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    @Size(max=200, message="venue space names have a limit of 200 chars")
    private String name;

    @JsonProperty("capacity")
    private LimitlessValueDTO capacity;

    @JsonProperty("default")
    private Boolean isDefault;

    @JsonProperty("notes")
    @Size(max = 1000, message = "notes size must be between 0 and 1000")
    private String notes;


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
