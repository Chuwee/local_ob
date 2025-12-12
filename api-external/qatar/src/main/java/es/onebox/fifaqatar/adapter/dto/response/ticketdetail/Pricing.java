package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Pricing implements Serializable {

    @Serial
    private static final long serialVersionUID = -5462326785042835052L;

    private BigDecimal amount;
    private String currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
