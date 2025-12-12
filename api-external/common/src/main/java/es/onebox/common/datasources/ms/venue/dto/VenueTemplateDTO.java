package es.onebox.common.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class VenueTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2653577840478632067L;

    private Long id;
    private String name;
    private Integer capacity;
    private VenueTemplateStatus status;
    private VenueTemplateScope scope;
    private VenueTemplateType templateType;
    private TemplateVenue venue;
    private Long eventId;
    private Long entityId;
    private String entityName;
    private List<SectorDTO> sectors;
    private String description;
    private Boolean graphic;
    private Boolean isPublic;
    private Long avetCapacityId;
    private ZonedDateTime creationDate;
    private Integer maxGroups;
    private Integer minAttendees;
    private Integer maxAttendees;
    private Integer minCompanions;
    private Integer maxCompanions;
    private Boolean companionsOccupyCapacity;
    private String imageUrl;
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

    public VenueTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(VenueTemplateType templateType) {
        this.templateType = templateType;
    }

    public TemplateVenue getVenue() {
        return venue;
    }

    public void setVenue(TemplateVenue venue) {
        this.venue = venue;
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
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

    public Integer getMaxGroups() {
        return maxGroups;
    }

    public void setMaxGroups(Integer maxGroups) {
        this.maxGroups = maxGroups;
    }

    public Integer getMinAttendees() {
        return minAttendees;
    }

    public void setMinAttendees(Integer minAttendees) {
        this.minAttendees = minAttendees;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getMinCompanions() {
        return minCompanions;
    }

    public void setMinCompanions(Integer minCompanions) {
        this.minCompanions = minCompanions;
    }

    public Integer getMaxCompanions() {
        return maxCompanions;
    }

    public void setMaxCompanions(Integer maxCompanions) {
        this.maxCompanions = maxCompanions;
    }

    public Boolean isCompanionsOccupyCapacity() {
        return companionsOccupyCapacity;
    }

    public Long getAvetCapacityId() {
        return avetCapacityId;
    }

    public void setAvetCapacityId(Long avetCapacityId) {
        this.avetCapacityId = avetCapacityId;
    }

    public void setCompanionsOccupyCapacity(Boolean companionsOccupyCapacity) {
        this.companionsOccupyCapacity = companionsOccupyCapacity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getUseExternalTickets() {
        return useExternalTickets;
    }

    public void setUseExternalTickets(Boolean useExternalTickets) {
        this.useExternalTickets = useExternalTickets;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(String inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }
}
