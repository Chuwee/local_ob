package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Price implements Serializable {

    @Serial
    private static final long serialVersionUID = -7605545752268194878L;

    private Double base;
    private Double total;
    private List<Surcharge> surcharges;

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<Surcharge> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<Surcharge> surcharges) {
        this.surcharges = surcharges;
    }
}
