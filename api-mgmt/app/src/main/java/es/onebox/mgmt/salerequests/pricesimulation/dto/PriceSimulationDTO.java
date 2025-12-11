package es.onebox.mgmt.salerequests.pricesimulation.dto;

import java.io.Serializable;
import java.util.List;

public class PriceSimulationDTO implements Serializable {

    private static final long serialVersionUID = 6016217542772954098L;

    private PriceDTO price;
    private List<PromotionDTO> promotions;

    public PriceDTO getPrice() {
        return price;
    }

    public void setPrice(PriceDTO price) {
        this.price = price;
    }

    public List<PromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionDTO> promotions) {
        this.promotions = promotions;
    }
}
