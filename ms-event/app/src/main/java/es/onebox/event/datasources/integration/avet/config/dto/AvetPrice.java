package es.onebox.event.datasources.integration.avet.config.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AvetPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 3381126778632178879L;
    private Integer priceId;
    private Short priceCode;
    private String priceDescription;
    private Integer clubCode;
    private Integer seasonCode;
    private Integer capacityId;

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Short getPriceCode() {
        return priceCode;
    }

    public void setPriceCode(Short priceCode) {
        this.priceCode = priceCode;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription;
    }

    public Integer getClubCode() {
        return clubCode;
    }

    public void setClubCode(Integer clubCode) {
        this.clubCode = clubCode;
    }

    public Integer getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(Integer seasonCode) {
        this.seasonCode = seasonCode;
    }

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
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
