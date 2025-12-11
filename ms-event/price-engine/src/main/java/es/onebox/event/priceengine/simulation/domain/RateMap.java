package es.onebox.event.priceengine.simulation.domain;

import java.util.List;

public class RateMap {

    public RateMap() {
    }

    public RateMap(Integer id, String name, List<PriceZone> priceZones) {
        this.id = id;
        this.name = name;
        this.priceZones = priceZones;
    }

    private Integer id;
    private String name;
    private List<PriceZone> priceZones;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PriceZone> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<PriceZone> priceZones) {
        this.priceZones = priceZones;
    }
}
