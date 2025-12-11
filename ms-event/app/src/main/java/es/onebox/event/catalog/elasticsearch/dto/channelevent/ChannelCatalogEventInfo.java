package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ChannelCatalogEventInfo extends ChannelCatalogInfo {

    private static final long serialVersionUID = 1L;

    private ChannelCatalogDatesWithTimeZones date;
    private Boolean onCatalog;
    private Integer catalogPosition;
    private Boolean isHighlighted;
    private Boolean onCarousel;
    private Integer carouselPosition;
    private Boolean extended;

    public ChannelCatalogEventInfo() {
    }

    public ChannelCatalogEventInfo(Boolean onCatalog, Integer catalogPosition, Boolean isHighlighted,
                                   Boolean onCarousel, Integer carouselPosition, Boolean extended) {
        this.onCatalog = onCatalog;
        this.catalogPosition = catalogPosition;
        this.isHighlighted = isHighlighted;
        this.onCarousel = onCarousel;
        this.carouselPosition = carouselPosition;
        this.extended = extended;
    }

    public ChannelCatalogDatesWithTimeZones getDate() {
        return date;
    }

    public void setDate(ChannelCatalogDatesWithTimeZones date) {
        this.date = date;
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

    public Boolean getHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        isHighlighted = highlighted;
    }

    public Boolean getOnCarousel() {
        return onCarousel;
    }

    public void setOnCarousel(Boolean onCarousel) {
        this.onCarousel = onCarousel;
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
