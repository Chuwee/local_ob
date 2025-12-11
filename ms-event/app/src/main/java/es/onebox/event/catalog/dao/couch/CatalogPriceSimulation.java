package es.onebox.event.catalog.dao.couch;

import es.onebox.event.pricesengine.dto.PromotionDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceSimulation implements Serializable {
    @Serial
    private static final long serialVersionUID = -8416565024721193157L;

    private CatalogPrice price;
    private List<PromotionDTO> promotions;

    public CatalogPrice getPrice() {
        return price;
    }

    public void setPrice(CatalogPrice price) {
        this.price = price;
    }

    public List<PromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionDTO> promotions) {
        this.promotions = promotions;
    }
}
