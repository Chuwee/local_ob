package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;

public class MandatoryThreshold implements Serializable {

    @Serial
    private static final long serialVersionUID = -2954462812123302165L;

    private String currency;
    private Double amount;

    public MandatoryThreshold() {}

    public MandatoryThreshold(String currency, Double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
