package es.onebox.event.priceengine.simulation.domain;

import es.onebox.event.priceengine.simulation.domain.enums.SurchargeType;
import es.onebox.event.priceengine.taxes.domain.Taxes;

import java.io.Serial;
import java.io.Serializable;

public class Surcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = -1332554981797542427L;

    private Double value;
    private Double net;
    private Taxes taxes;
    private SurchargeType type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Taxes getTaxes() {
        return taxes;
    }

    public void setTaxes(Taxes taxes) {
        this.taxes = taxes;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }
}
