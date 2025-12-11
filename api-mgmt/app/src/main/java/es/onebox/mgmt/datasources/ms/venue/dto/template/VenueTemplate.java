package es.onebox.mgmt.datasources.ms.venue.dto.template;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class VenueTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = -8113356614545092857L;

    private Long id;
    private String name;
    private Integer capacity;
    private Integer availableCapacity;
    private VenueTemplateStatus status;
    private VenueTemplateScope scope;
    private VenueTemplateType templateType;
    private Venue venue;
    private IdNameDTO space;
    private Long eventId;
    private Long entityId;
    private String entityName;
    private List<Sector> sectors;
    private List<Gate> gates;
    private List<Row> rows;
    private List<NotNumberedZone> notNumberedZones;
    private String description;
    private Boolean graphic;
    private Boolean isPublic;
    private ZonedDateTime creationDate;
    private Integer maxGroups;
    private Integer minAttendees;
    private Integer maxAttendees;
    private Integer minCompanions;
    private Integer maxCompanions;
    private Boolean companionsOccupyCapacity;
    private Long avetCapacityId;
    private String imageUrl;
    private Map<String, Object> externalData;
    private String inventoryProvider;
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

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
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

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public IdNameDTO getSpace() {
        return space;
    }

    public void setSpace(IdNameDTO space) {
        this.space = space;
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

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<NotNumberedZone> getNotNumberedZones() {
        return notNumberedZones;
    }

    public void setNotNumberedZones(List<NotNumberedZone> notNumberedZones) {
        this.notNumberedZones = notNumberedZones;
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

    public Boolean getCompanionsOccupyCapacity() {
        return companionsOccupyCapacity;
    }

    public void setCompanionsOccupyCapacity(Boolean companionsOccupyCapacity) {
        this.companionsOccupyCapacity = companionsOccupyCapacity;
    }

    public Long getAvetCapacityId() {
        return avetCapacityId;
    }

    public void setAvetCapacityId(Long avetCapacityId) {
        this.avetCapacityId = avetCapacityId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    public String getInventoryProvider() { return inventoryProvider; }

    public void setInventoryProvider(String inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
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
