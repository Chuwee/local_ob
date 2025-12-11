package es.onebox.mgmt.common.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class PromotionSurchargesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("channel_fees")
    private Boolean channelFees;
    @JsonProperty("promoter")
    private Boolean promoter;

    public Boolean getChannelFees() {
        return channelFees;
    }

    public void setChannelFees(Boolean channelFees) {
        this.channelFees = channelFees;
    }

    public Boolean getPromoter() {
        return promoter;
    }

    public void setPromoter(Boolean promoter) {
        this.promoter = promoter;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
