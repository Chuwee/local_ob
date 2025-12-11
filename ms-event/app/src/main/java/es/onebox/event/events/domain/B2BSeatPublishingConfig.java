package es.onebox.event.events.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;

@CouchDocument
public class B2BSeatPublishingConfig implements Serializable {

    private Long eventId;
    private Long channelId;
    private Long venueTemplateId;
    private Boolean enabled;
    private Long publishedSeatQuotaId;
    private PublishedSeatPriceType publishedSeatPriceType;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPublishedSeatQuotaId() {
        return publishedSeatQuotaId;
    }

    public void setPublishedSeatQuotaId(Long publishedSeatQuotaId) {
        this.publishedSeatQuotaId = publishedSeatQuotaId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public PublishedSeatPriceType getPublishedSeatPriceType() {
        return publishedSeatPriceType;
    }

    public void setPublishedSeatPriceType(PublishedSeatPriceType publishedSeatPriceType) {
        this.publishedSeatPriceType = publishedSeatPriceType;
    }
}
