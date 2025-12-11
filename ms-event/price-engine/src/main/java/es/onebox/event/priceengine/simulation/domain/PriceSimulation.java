package es.onebox.event.priceengine.simulation.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceSimulation implements Serializable {
    @Serial
    private static final long serialVersionUID = -8416565024721193157L;

    private Price price;
    private List<BasePromotion> basePromotions;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<BasePromotion> getBasePromotions() {
        return basePromotions;
    }

    public void setBasePromotions(List<BasePromotion> basePromotions) {
        this.basePromotions = basePromotions;
    }
}
