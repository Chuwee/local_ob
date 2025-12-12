package es.onebox.flc.invoices.dto;

import java.io.Serializable;

public class InvoiceTaxDTO implements Serializable {

    private static final long serialVersionUID = 8621071881227095739L;

    private Double base;
    private Double percentage;
    private Double value;
    private Double total;

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
