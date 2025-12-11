package es.onebox.event.priceengine.taxes.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Taxes implements Serializable {
    @Serial
    private static final long serialVersionUID = -8416565024721193157L;

    private Double total;
    private List<TaxBreakdown> breakdown;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<TaxBreakdown> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<TaxBreakdown> breakdown) {
        this.breakdown = breakdown;
    }
}
