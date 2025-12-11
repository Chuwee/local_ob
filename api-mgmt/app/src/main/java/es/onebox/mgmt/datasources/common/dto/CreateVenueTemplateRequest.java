package es.onebox.mgmt.datasources.common.dto;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateScope;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.Optional;

public class CreateVenueTemplateRequest {

    private String name;
    private Long venueId;
    private Long spaceId;
    private Long eventId;
    private Long entityId;
    private Integer capacityId;
    private VenueTemplateScope scope;
    private VenueTemplateType type;
    private Boolean graphical;
    private Long fromTemplateId;
    private Optional<String> image;
    private Map<String, Object> additionalConfig;
    private Long externalId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public VenueTemplateScope getScope() {
        return scope;
    }

    public void setScope(VenueTemplateScope scope) {
        this.scope = scope;
    }

    public VenueTemplateType getType() {
        return type;
    }

    public void setType(VenueTemplateType type) {
        this.type = type;
    }

    public Boolean getGraphical() {
        return graphical;
    }

    public void setGraphical(Boolean graphical) {
        this.graphical = graphical;
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

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
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

    public Map<String, Object> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(Map<String, Object> additionalConfig) {
        this.additionalConfig = additionalConfig;
    }
}
