package es.onebox.event.sessions.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SessionRecord extends CpanelSesionRecord {

    @Serial
    private static final long serialVersionUID = 1303719761797355554L;

    private String eventName;
    private Integer eventType;
    private Integer eventStatus;
    private Byte eventPackType;
    private Integer entityId;
    private String entityName;
    private Integer sessionId;
    private Integer operatorId;
    private Integer venueTemplateId;
    private String venueTemplateName;
    private Integer venueTemplateType;
    private Byte venueTemplateGraphic;
    private Integer venueTemplateSpaceId;
    private String venueTemplateSpaceName;
    private Integer venueId;
    private String venueName;
    private String venueCity;
    private Integer venueCountryId;
    private String venueTZ;
    private String venueTZName;
    private Integer venueTZOffset;
    private String spaceName;
    private String taxTicketName;
    private String taxChargesName;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Byte getEventPackType() {
        return eventPackType;
    }

    public void setEventPackType(Byte eventPackType) {
        this.eventPackType = eventPackType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public String getVenueTemplateName() {
        return venueTemplateName;
    }

    public void setVenueTemplateName(String venueTemplateName) {
        this.venueTemplateName = venueTemplateName;
    }

    public Integer getVenueTemplateType() {
        return venueTemplateType;
    }

    public void setVenueTemplateType(Integer venueTemplateType) {
        this.venueTemplateType = venueTemplateType;
    }

    public Byte getVenueTemplateGraphic() {
        return venueTemplateGraphic;
    }

    public void setVenueTemplateGraphic(Byte venueTemplateGraphic) {
        this.venueTemplateGraphic = venueTemplateGraphic;
    }

    public Integer getVenueTemplateSpaceId() {
        return venueTemplateSpaceId;
    }

    public void setVenueTemplateSpaceId(Integer venueTemplateSpaceId) {
        this.venueTemplateSpaceId = venueTemplateSpaceId;
    }

    public String getVenueTemplateSpaceName() {
        return venueTemplateSpaceName;
    }

    public void setVenueTemplateSpaceName(String venueTemplateSpaceName) {
        this.venueTemplateSpaceName = venueTemplateSpaceName;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public Integer getVenueCountryId() {
        return venueCountryId;
    }

    public void setVenueCountryId(Integer venueCountryId) {
        this.venueCountryId = venueCountryId;
    }

    public String getVenueTZ() {
        return venueTZ;
    }

    public void setVenueTZ(String venueTZ) {
        this.venueTZ = venueTZ;
    }

    public String getVenueTZName() {
        return venueTZName;
    }

    public void setVenueTZName(String venueTZName) {
        this.venueTZName = venueTZName;
    }

    public Integer getVenueTZOffset() {
        return venueTZOffset;
    }

    public void setVenueTZOffset(Integer venueTZOffset) {
        this.venueTZOffset = venueTZOffset;
    }

    public String getTaxTicketName() {
        return taxTicketName;
    }

    public void setTaxTicketName(String taxTicketName) {
        this.taxTicketName = taxTicketName;
    }

    public String getTaxChargesName() {
        return taxChargesName;
    }

    public void setTaxChargesName(String taxChargesName) {
        this.taxChargesName = taxChargesName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
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
