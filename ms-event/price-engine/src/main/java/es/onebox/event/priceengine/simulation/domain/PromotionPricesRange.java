package es.onebox.event.priceengine.simulation.domain;

public class PromotionPricesRange {

    private Double from;
    private Double value;

    public PromotionPricesRange() {
        super();
    }

    public PromotionPricesRange(Double from, Double value) {
        this();
        this.from = from;
        this.value = value;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
