package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;

import java.io.Serializable;
import java.util.List;

public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String city;

    private Integer countryId;

    private String googlePlaceId;

    private Long configId;

    private String configName;

    private List<AccessControlSystem> accessControlSystems;

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

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public List<AccessControlSystem> getAccessControlSystems() {
        return accessControlSystems;
    }

    public void setAccessControlSystems(List<AccessControlSystem> accessControlSystems) {
        this.accessControlSystems = accessControlSystems;
    }
}
