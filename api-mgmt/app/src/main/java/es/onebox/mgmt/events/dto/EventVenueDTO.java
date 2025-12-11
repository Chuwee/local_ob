package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.NameDTO;

import java.io.Serializable;
import java.util.List;

public class EventVenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String city;

    private String country;

    @JsonProperty("google_place_id")
    private String googlePlaceId;

    @JsonProperty("access_control_systems")
    private List<NameDTO> accessControlSystems;

    public EventVenueDTO() {
    }

    public EventVenueDTO(Long id, String name, String city, String googlePlaceId, List<NameDTO> accessControlSystems) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.googlePlaceId = googlePlaceId;
        this.accessControlSystems = accessControlSystems;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    public List<NameDTO> getAccessControlSystems() {
        return accessControlSystems;
    }

    public void setAccessControlSystems(List<NameDTO> accessControlSystems) {
        this.accessControlSystems = accessControlSystems;
    }
}
