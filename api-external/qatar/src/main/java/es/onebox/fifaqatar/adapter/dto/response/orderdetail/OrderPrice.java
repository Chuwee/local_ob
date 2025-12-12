package es.onebox.fifaqatar.adapter.dto.response.orderdetail;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 5006027362156619517L;

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
