package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class PriceTypeAndRateConditionDTO implements Serializable {

    private static final long serialVersionUID = -7902360172245662380L;

    private Integer priceTypeId;
    private Integer rateId;
    private Double refundPercentage;

    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public Double getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(Double refundPercentage) {
        this.refundPercentage = refundPercentage;
    }
}
