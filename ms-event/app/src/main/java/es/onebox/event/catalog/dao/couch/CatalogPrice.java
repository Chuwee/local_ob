package es.onebox.event.catalog.dao.couch;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double base;
    private Double net;
    private CatalogPriceTaxes taxes;
    private Double total;
    private Double original;
    private List<CatalogSurcharge> catalogSurcharges;

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public CatalogPriceTaxes getTaxes() {
        return taxes;
    }

    public void setTaxes(CatalogPriceTaxes taxes) {
        this.taxes = taxes;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getOriginal() {
        return original;
    }

    public void setOriginal(Double original) {
        this.original = original;
    }

    public List<CatalogSurcharge> getSurcharges() {
        return catalogSurcharges;
    }

    public void setSurcharges(List<CatalogSurcharge> catalogSurcharges) {
        this.catalogSurcharges = catalogSurcharges;
    }
}
