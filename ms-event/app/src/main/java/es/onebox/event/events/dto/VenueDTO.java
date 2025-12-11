package es.onebox.event.events.dto;

import es.onebox.event.datasources.ms.accesscontrol.dto.enums.AccessControlSystem;
import es.onebox.event.events.domain.VenueTemplateType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2972172476093916692L;

    private Long id;

    private String name;

    private Long entityId;

    private String city;

    private Integer countryId;

    private String googlePlaceId;

    private Long configId;

    private String configName;

    private VenueTemplateType configType;

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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public VenueTemplateType getConfigType() {
        return configType;
    }

    public void setConfigType(VenueTemplateType configType) {
        this.configType = configType;
    }

    public List<AccessControlSystem> getAccessControlSystems() {
        return accessControlSystems;
    }

    public void setAccessControlSystems(List<AccessControlSystem> accessControlSystems) {
        this.accessControlSystems = accessControlSystems;
    }
}
