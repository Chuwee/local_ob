package es.onebox.event.catalog.dto.price;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceTaxesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double total;
    private List<CatalogTaxesBreakdownDTO> breakdown;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<CatalogTaxesBreakdownDTO> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<CatalogTaxesBreakdownDTO> breakdown) {
        this.breakdown = breakdown;
    }
}
