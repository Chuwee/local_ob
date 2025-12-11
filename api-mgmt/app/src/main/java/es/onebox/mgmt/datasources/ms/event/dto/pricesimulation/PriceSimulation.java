package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceSimulation implements Serializable {

    @Serial
    private static final long serialVersionUID = 6016217542772954098L;

    private Price price;
    private List<Promotion> promotions;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }
}
