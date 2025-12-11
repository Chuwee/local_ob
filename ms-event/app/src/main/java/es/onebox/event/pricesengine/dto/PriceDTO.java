package es.onebox.event.pricesengine.dto;

import java.io.Serializable;
import java.util.List;

public class PriceDTO implements Serializable {

    private static final long serialVersionUID = 4671604821979555345L;

    private Double base;
    private Double total;
    private List<SurchargeDTO> surcharges;

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

    public List<SurchargeDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<SurchargeDTO> surcharges) {
        this.surcharges = surcharges;
    }
}
