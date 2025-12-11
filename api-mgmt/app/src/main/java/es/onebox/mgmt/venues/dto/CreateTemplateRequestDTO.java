package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Optional;

public class CreateTemplateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;

    @JsonProperty("event_id")
    private Long eventId;

    @JsonProperty("venue_id")
    private Long venueId;

    @JsonProperty("space_id")
    private Long spaceId;

    @JsonProperty("entity_id")
    private Long entityId;

    private VenueTemplateScopeDTO scope;

    private VenueTemplateTypeDTO type;

    private Boolean graphic;

    @JsonProperty("from_template_id")
    private Long fromTemplateId;

    private Optional<String> image;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    @JsonProperty("external_id")
    private Long externalId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public VenueTemplateScopeDTO getScope() {
        return scope;
    }

    public void setScope(VenueTemplateScopeDTO scope) {
        this.scope = scope;
    }

    public VenueTemplateTypeDTO getType() {
        return type;
    }

    public void setType(VenueTemplateTypeDTO type) {
        this.type = type;
    }

    public Boolean getGraphic() {
        return graphic;
    }

    public void setGraphic(Boolean graphic) {
        this.graphic = graphic;
    }

    public Long getFromTemplateId() {
        return fromTemplateId;
    }

    public void setFromTemplateId(Long fromTemplateId) {
        this.fromTemplateId = fromTemplateId;
    }

    public Optional<String> getImage() {
        return image;
    }

    public void setImage(Optional<String> image) {
        this.image = image;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public Long getExternalId() { return externalId; }

    public void setExternalId(Long externalId) { this.externalId = externalId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
