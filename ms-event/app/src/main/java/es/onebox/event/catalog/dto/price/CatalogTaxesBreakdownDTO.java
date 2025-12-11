package es.onebox.event.catalog.dto.price;

import java.io.Serial;
import java.io.Serializable;

public class CatalogTaxesBreakdownDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Long id;
    private Double amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
