package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateStatusDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class BaseVenueTemplateDTO implements DateConvertible, Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer capacity;
    private VenueTemplateStatusDTO status;
    private VenueTemplateTypeDTO type;
    private VenueTemplateScopeDTO scope;
    private Boolean graphic;
    @JsonProperty("public")
    private Boolean isPublic;
    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;
    private IdNameDTO entity;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("inventory_provider")
    private String inventoryProvider;
    @JsonProperty("external_id")
    private Long externalId;

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

    public VenueTemplateStatusDTO getStatus() {
        return status;
    }

    public void setStatus(VenueTemplateStatusDTO status) {
        this.status = status;
    }

    public VenueTemplateTypeDTO getType() {
        return type;
    }

    public void setType(VenueTemplateTypeDTO type) {
        this.type = type;
    }

    public VenueTemplateScopeDTO getScope() {
        return scope;
    }

    public void setScope(VenueTemplateScopeDTO scope) {
        this.scope = scope;
    }

    public Boolean getGraphic() {
        return graphic;
    }

    public void setGraphic(Boolean graphic) {
        this.graphic = graphic;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public abstract BaseVenueDTO getVenue();

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(String inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public Long getExternalId() { return externalId; }

    public void setExternalId(Long externalId) { this.externalId = externalId; }

    @Override
    public void convertDates() {
        if (getVenue() != null && getVenue().getTimezone() != null && getCreationDate() != null) {
            setCreationDate(getCreationDate().withZoneSameInstant(ZoneId.of(getVenue().getTimezone())));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
