package es.onebox.mgmt.datasources.integration.avetconfig.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class VenueTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2653577840478632067L;

    private Long id;
    private String name;
    private Integer capacity;
    private VenueTemplateStatus status;
    private VenueTemplateScope scope;
    private Long eventId;
    private String description;
    private Boolean graphic;
    private Boolean isPublic;
    private Long avetCapacityId;
    private ZonedDateTime creationDate;
    private Boolean useExternalTickets;
    private String inventoryProvider;

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

    public VenueTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(VenueTemplateStatus status) {
        this.status = status;
    }

    public VenueTemplateScope getScope() {
        return scope;
    }

    public void setScope(VenueTemplateScope scope) {
        this.scope = scope;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getAvetCapacityId() {
        return avetCapacityId;
    }

    public void setAvetCapacityId(Long avetCapacityId) {
        this.avetCapacityId = avetCapacityId;
    }

    public Boolean getUseExternalTickets() {
        return useExternalTickets;
    }

    public void setUseExternalTickets(Boolean useExternalTickets) {
        this.useExternalTickets = useExternalTickets;
    }

    public String getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(String inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
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
