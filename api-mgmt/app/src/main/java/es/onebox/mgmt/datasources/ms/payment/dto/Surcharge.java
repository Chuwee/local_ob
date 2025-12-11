package es.onebox.mgmt.datasources.ms.payment.dto;

import es.onebox.mgmt.channels.gateways.dto.SurchargeType;

import java.io.Serial;
import java.io.Serializable;

public class Surcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 281323428906775601L;

    private String currency;
    private SurchargeType type;
    private Double value;
    private Double minValue;
    private Double maxValue;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
