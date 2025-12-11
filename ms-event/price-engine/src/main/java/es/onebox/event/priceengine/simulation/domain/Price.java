package es.onebox.event.priceengine.simulation.domain;

import es.onebox.event.priceengine.taxes.domain.Taxes;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Price implements Serializable {

    @Serial
    private static final long serialVersionUID = 4671604821979555345L;

    private Double base;
    private Double net;
    private Double total;
    private Taxes taxes;
    private List<Surcharge> surcharges;

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Taxes getTaxes() {
        return taxes;
    }

    public void setTaxes(Taxes taxes) {
        this.taxes = taxes;
    }

    public List<Surcharge> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<Surcharge> surcharges) {
        this.surcharges = surcharges;
    }
}
