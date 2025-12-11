package es.onebox.event.priceengine.simulation.domain;

public class PriceZone {

    public PriceZone() {
    }

    public PriceZone(Long id, Double price, PriceZoneConfig config) {
        this.id = id;
        this.price = price;
        this.config = config;
    }

    private Long id;
    private Double price;
    private PriceZoneConfig config;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public PriceZoneConfig getConfig() {
        return config;
    }

    public void setConfig(PriceZoneConfig config) {
        this.config = config;
    }
}
