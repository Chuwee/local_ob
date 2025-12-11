package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class VenueSpacePostRequest implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    @NotBlank(message="venue space name is mandatory")
    @Size(max=200, message="venue space names have a limit of 200 chars")
    private String name;

    @JsonProperty("capacity")
    private LimitlessValueDTO capacity;

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

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
