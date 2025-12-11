package es.onebox.event.priceengine.simulation.record;

public class EventPromotionConditionRateRecord {
    private Integer idPromotionEvent;
    private Integer id;
    private Long quantity;

    public Integer getIdPromotionEvent() {
        return idPromotionEvent;
    }

    public void setIdPromotionEvent(Integer idPromotionEvent) {
        this.idPromotionEvent = idPromotionEvent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
