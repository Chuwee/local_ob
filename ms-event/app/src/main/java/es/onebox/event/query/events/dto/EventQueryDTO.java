package es.onebox.event.query.events.dto;

import java.io.Serializable;
import java.util.List;

import es.onebox.event.events.enums.EventType;

/**
 * @author ignasi
 */
public class EventQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private EventType type;
    private Entity entity;
    private Entity promoter;
    private List<Sale> sales;
    private List<Rate> rates;
    private List<Venue> venues;

    private String promoterRef;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Entity getPromoter() {
        return promoter;
    }

    public void setPromoter(Entity promoter) {
        this.promoter = promoter;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public String getPromoterRef() {
        return promoterRef;
    }

    public void setPromoterRef(String promoterRef) {
        this.promoterRef = promoterRef;
    }

}
