package es.onebox.event.sessions.domain.sessionconfig;

import java.util.List;

public class DynamicPriceZone {

    private Long IdPriceZone;
    private Long activeZone;
    private List<DynamicPrice> dynamicPrices;
    private DynamicPrice defaultPrice;

    public Long getIdPriceZone() {
        return IdPriceZone;
    }

    public void setIdPriceZone(Long idPriceZone) {
        IdPriceZone = idPriceZone;
    }

    public List<DynamicPrice> getDynamicPrices() {
        return dynamicPrices;
    }

    public void setDynamicPrices(List<DynamicPrice> dynamicPrices) {
        this.dynamicPrices = dynamicPrices;
    }

    public Long getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(Long activeZone) {
        this.activeZone = activeZone;
    }

    public DynamicPrice getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(DynamicPrice defaultPrice) {
        this.defaultPrice = defaultPrice;
    }
}
