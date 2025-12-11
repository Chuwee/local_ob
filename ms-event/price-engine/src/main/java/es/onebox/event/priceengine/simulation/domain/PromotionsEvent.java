package es.onebox.event.priceengine.simulation.domain;

import java.util.List;

public class PromotionsEvent {

    private List<Promotion> discounts;
    private List<Promotion> automatics;
    private List<Promotion> promotions;
    private List<Promotion> notCumulative;

    public List<Promotion> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Promotion> discounts) {
        this.discounts = discounts;
    }

    public List<Promotion> getAutomatics() {
        return automatics;
    }

    public void setAutomatics(List<Promotion> automatics) {
        this.automatics = automatics;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<Promotion> getNotCumulative() {
        return notCumulative;
    }

    public void setNotCumulative(List<Promotion> notCumulative) {
        this.notCumulative = notCumulative;
    }
}
