package es.onebox.mgmt.datasources.ms.channel.dto.catalog;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelEventUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Boolean onCatalog;
    private Integer catalogPosition;
    private Integer carouselPosition;
    private Boolean extended;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Boolean getOnCatalog() {
        return onCatalog;
    }

    public void setOnCatalog(Boolean onCatalog) {
        this.onCatalog = onCatalog;
    }

    public Integer getCatalogPosition() {
        return catalogPosition;
    }

    public void setCatalogPosition(Integer catalogPosition) {
        this.catalogPosition = catalogPosition;
    }

    public Integer getCarouselPosition() {
        return carouselPosition;
    }

    public void setCarouselPosition(Integer carouselPosition) {
        this.carouselPosition = carouselPosition;
    }

    public Boolean getExtended() {
        return extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
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
