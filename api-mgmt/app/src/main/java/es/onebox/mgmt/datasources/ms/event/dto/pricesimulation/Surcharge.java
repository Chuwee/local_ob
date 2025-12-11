package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serial;
import java.io.Serializable;

public class Surcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 7271000322500557155L;

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
