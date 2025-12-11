package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.dto.promotion.CatalogPromotionPriceVariationType;

import java.io.Serial;
import java.io.Serializable;

public class CatalogPromotedPriceVariation implements Serializable {

    @Serial
    private static final long serialVersionUID = -5228617818382136650L;

    private CatalogPromotionPriceVariationType type;
    private Double value;

    public CatalogPromotionPriceVariationType getType() {
        return type;
    }

    public void setType(CatalogPromotionPriceVariationType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
