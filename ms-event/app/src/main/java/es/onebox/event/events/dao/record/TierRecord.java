package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TierRecord extends CpanelTierRecord implements Serializable {

    private String priceTypeName;
    private Integer venueTemplateId;
    private List<LimiteCupoRecord> limitesCupo;
    private String timeZoneName;
    private String timeZoneOlsonId;
    private Integer timeZoneOffset;

    public String getPriceTypeName() {
        return priceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        this.priceTypeName = priceTypeName;
    }

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public List<LimiteCupoRecord> getLimitesCupo() {
        return limitesCupo;
    }

    public void setLimitesCupo(List<LimiteCupoRecord> limitesCupo) {
        this.limitesCupo = limitesCupo;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public String getTimeZoneOlsonId() {
        return timeZoneOlsonId;
    }

    public void setTimeZoneOlsonId(String timeZoneOlsonId) {
        this.timeZoneOlsonId = timeZoneOlsonId;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "TierRecord{" +
                "priceTypeName='" + priceTypeName + '\'' +
                ", venueTemplateId=" + venueTemplateId +
                ", limitesCupo=" + limitesCupo +
                ", timeZoneName='" + timeZoneName + '\'' +
                ", timeZoneOlsonId='" + timeZoneOlsonId + '\'' +
                ", timeZoneOffset='" + timeZoneOffset + '\'' +
                "} " + super.toString();
    }
}
