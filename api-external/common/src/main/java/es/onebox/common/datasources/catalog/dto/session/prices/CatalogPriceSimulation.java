package es.onebox.common.datasources.catalog.dto.session.prices;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceSimulation implements Serializable {


    @Serial
    private static final long serialVersionUID = 1165903907509032509L;
    private CatalogPrice price;
    private List<CatalogPromotion> promotions;

    public CatalogPrice getPrice() {
        return price;
    }

    public void setPrice(CatalogPrice price) {
        this.price = price;
    }

    public List<CatalogPromotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<CatalogPromotion> promotions) {
        this.promotions = promotions;
    }
}
