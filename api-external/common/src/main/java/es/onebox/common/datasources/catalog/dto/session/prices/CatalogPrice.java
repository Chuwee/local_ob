package es.onebox.common.datasources.catalog.dto.session.prices;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPrice implements Serializable {


    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double base;
    private Double total;
    private Double original;
    private List<CatalogSurcharge> surcharges;

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

    public Double getOriginal() {
        return original;
    }

    public void setOriginal(Double original) {
        this.original = original;
    }

    public List<CatalogSurcharge> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<CatalogSurcharge> catalogSurchargeDTOS) {
        this.surcharges = catalogSurchargeDTOS;
    }
}
