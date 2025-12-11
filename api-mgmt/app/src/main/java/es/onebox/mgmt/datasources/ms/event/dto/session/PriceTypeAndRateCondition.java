package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class PriceTypeAndRateCondition implements Serializable {

    private static final long serialVersionUID = -7902360172245662380L;

    private Long priceTypeId;
    private Long rateId;
    private Double refundPercentage;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Double getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(Double refundPercentage) {
        this.refundPercentage = refundPercentage;
    }
}
