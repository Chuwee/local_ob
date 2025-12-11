package es.onebox.event.pricesengine.dto;

import es.onebox.event.pricesengine.dto.enums.SurchargeType;

import java.io.Serializable;

public class SurchargeDTO implements Serializable {

    private static final long serialVersionUID = -1332554981797542427L;

    private Double value;
    private SurchargeType type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }
}
