package es.onebox.event.catalog.dto.price;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double base;
    private Double net;
    private CatalogPriceTaxesDTO taxes;
    private Double total;
    private Double original;
    private List<CatalogSurchargeDTO> surcharges;

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

    public CatalogPriceTaxesDTO getTaxes() {
        return taxes;
    }

    public void setTaxes(CatalogPriceTaxesDTO taxes) {
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

    public List<CatalogSurchargeDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<CatalogSurchargeDTO> catalogSurchargeDTOS) {
        this.surcharges = catalogSurchargeDTOS;
    }
}
