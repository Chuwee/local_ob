package es.onebox.event.catalog.dto.price;

import es.onebox.event.pricesengine.dto.PromotionDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceSimulationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8416565024721193157L;

    private CatalogPriceDTO price;
    private List<PromotionDTO> promotions;

    public CatalogPriceDTO getPrice() {
        return price;
    }

    public void setPrice(CatalogPriceDTO price) {
        this.price = price;
    }

    public List<PromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionDTO> promotions) {
        this.promotions = promotions;
    }
}
