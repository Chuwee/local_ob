package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TierPriceTypeAvailabilityDTO implements Serializable {

    public TierPriceTypeAvailabilityDTO() {

    }

    public TierPriceTypeAvailabilityDTO(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
        this.saleGroupsAvailabilities = new HashMap<>();
    }

    private Long priceTypeId;
    private Integer priceTypeLimit;
    // SaleGroupId -> available tickets to sell
    private Map<Long, Integer> saleGroupsAvailabilities;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Integer getPriceTypeLimit() {
        return priceTypeLimit;
    }

    public void setPriceTypeLimit(Integer priceTypeLimit) {
        this.priceTypeLimit = priceTypeLimit;
    }

    public Map<Long, Integer> getSaleGroupsAvailabilities() {
        return saleGroupsAvailabilities;
    }

    public void setSaleGroupsAvailabilities(Map<Long, Integer> saleGroupsAvailabilities) {
        this.saleGroupsAvailabilities = saleGroupsAvailabilities;
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
        return "TierPriceTypeAvailabilityDTO{" +
                "priceTypeId=" + priceTypeId +
                ", priceTypeLimit=" + priceTypeLimit +
                ", saleGroupsAvailabilities=" + saleGroupsAvailabilities +
                '}';
    }
}
