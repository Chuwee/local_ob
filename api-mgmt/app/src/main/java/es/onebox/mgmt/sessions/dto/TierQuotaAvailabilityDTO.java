package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.events.dto.PriceTypeTierDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TierQuotaAvailabilityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("price_type")
    private PriceTypeTierDTO priceType;
    @JsonProperty("quota")
    private QuotaTierInfoDTO quota;
    private TierInfoDTO tier;
    private Integer sold = 0;
    private Integer refunded = 0;

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public Integer getRefunded() {
        return refunded;
    }

    public void setRefunded(Integer refunded) {
        this.refunded = refunded;
    }

    public IdNameDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceTypeTierDTO priceType) {
        this.priceType = priceType;
    }

    public QuotaTierInfoDTO getQuota() {
        return quota;
    }

    public void setQuota(QuotaTierInfoDTO quota) {
        this.quota = quota;
    }

    public TierInfoDTO getTier() {
        return tier;
    }

    public void setTier(TierInfoDTO tier) {
        this.tier = tier;
    }

    public void addSold() {
        sold++;
    }

    public void addRefunded() {
        refunded++;
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
