package es.onebox.common.datasources.ms.venue.dto;

import es.onebox.common.datasources.ms.venue.enums.VenueTemplateScope;
import es.onebox.common.datasources.ms.venue.enums.VenueTemplateStatus;
import es.onebox.common.datasources.ms.venue.enums.VenueTemplateType;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VenueTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = -5431716633272538617L;

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
    private Map<String, Object> externalData;

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

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
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
