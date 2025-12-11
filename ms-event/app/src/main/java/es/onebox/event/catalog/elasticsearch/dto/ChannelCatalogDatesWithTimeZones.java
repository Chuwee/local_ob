package es.onebox.event.catalog.elasticsearch.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelCatalogDatesWithTimeZones extends ChannelCatalogDates {

    private static final long serialVersionUID = 1L;

    private String publishTimeZone;
    private String saleStartTimeZone;
    private String saleEndTimeZone;
    private String startTimeZone;
    private String endTimeZone;

    public String getPublishTimeZone() {
        return publishTimeZone;
    }

    public void setPublishTimeZone(String publishTimeZone) {
        this.publishTimeZone = publishTimeZone;
    }

    public String getSaleStartTimeZone() {
        return saleStartTimeZone;
    }

    public void setSaleStartTimeZone(String saleStartTimeZone) {
        this.saleStartTimeZone = saleStartTimeZone;
    }

    public String getSaleEndTimeZone() {
        return saleEndTimeZone;
    }

    public void setSaleEndTimeZone(String saleEndTimeZone) {
        this.saleEndTimeZone = saleEndTimeZone;
    }

    public String getStartTimeZone() {
        return startTimeZone;
    }

    public void setStartTimeZone(String startTimeZone) {
        this.startTimeZone = startTimeZone;
    }

    public String getEndTimeZone() {
        return endTimeZone;
    }

    public void setEndTimeZone(String endTimeZone) {
        this.endTimeZone = endTimeZone;
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
