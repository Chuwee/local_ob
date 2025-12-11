package es.onebox.event.catalog.dao.couch;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceTaxes implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double total;
    private List<CatalogTaxesBreakdown> breakdown;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<CatalogTaxesBreakdown> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<CatalogTaxesBreakdown> breakdown) {
        this.breakdown = breakdown;
    }
}
