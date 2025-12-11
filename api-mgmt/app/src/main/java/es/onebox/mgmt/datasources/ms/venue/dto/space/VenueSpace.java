package es.onebox.mgmt.datasources.ms.venue.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class VenueSpace implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("venueId")    private Long venueId;
    @JsonProperty("id")         private Long id;
    @JsonProperty("name")       private String name;
    @JsonProperty("capacity")   private Integer capacity;
    @JsonProperty("default")    private Boolean isDefault;
    @JsonProperty("notes")      private String notes;


    public Long getVenueId() {
        return venueId;
    }
    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

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

    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
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
