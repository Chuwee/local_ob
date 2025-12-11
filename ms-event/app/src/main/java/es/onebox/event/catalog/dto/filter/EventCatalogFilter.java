package es.onebox.event.catalog.dto.filter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EventCatalogFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -7543684169532369514L;

    private List<Integer> eventIdList;
    private Integer eventStatus;
    private List<Byte> eventType;
    private Integer venueId;
    private Integer taxonomyId;
    private Integer customTaxonomyId;
    private String customTaxonomyCode;
    private Integer tourId;
    private Date beginEventDate;
    private Date endEventDate;
    private List<Integer> eventAttributesValueIds;
    private Boolean publishChannelEvent;

    public List<Integer> getEventIdList() {
        return eventIdList;
    }

    public void setEventIdList(List<Integer> eventIdList) {
        this.eventIdList = eventIdList;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public List<Byte> getEventType() {
        return eventType;
    }

    public void setEventType(List<Byte> eventType) {
        this.eventType = eventType;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(Integer taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public Integer getCustomTaxonomyId() {
        return customTaxonomyId;
    }

    public void setCustomTaxonomyId(Integer customTaxonomyId) {
        this.customTaxonomyId = customTaxonomyId;
    }

    public String getCustomTaxonomyCode() {
        return customTaxonomyCode;
    }

    public void setCustomTaxonomyCode(String customTaxonomyCode) {
        this.customTaxonomyCode = customTaxonomyCode;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Date getBeginEventDate() {
        return beginEventDate;
    }

    public void setBeginEventDate(Date beginEventDate) {
        this.beginEventDate = beginEventDate;
    }

    public Date getEndEventDate() {
        return endEventDate;
    }

    public void setEndEventDate(Date endEventDate) {
        this.endEventDate = endEventDate;
    }

    public List<Integer> getEventAttributesValueIds() {
        return eventAttributesValueIds;
    }

    public void setEventAttributesValueIds(List<Integer> eventAttributesValueIds) {
        this.eventAttributesValueIds = eventAttributesValueIds;
    }

    public Boolean getPublishChannelEvent() {
        return publishChannelEvent;
    }

    public void setPublishChannelEvent(Boolean publishChannelEvent) {
        this.publishChannelEvent = publishChannelEvent;
    }

}
