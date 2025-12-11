package es.onebox.event.priceengine.simulation.domain;

import java.util.Map;

public class VenueConfigMap {

    public VenueConfigMap() {
    }

    public VenueConfigMap(Long id, String name, Map<Integer, RateMap> rate) {
        this.id = id;
        this.name = name;
        this.rate = rate;
    }

    private Long id;
    private String name;
    private Map<Integer, RateMap> rate;

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

    public Map<Integer, RateMap> getRate() {
        return rate;
    }

    public void setRate(Map<Integer, RateMap> rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "VenueConfigMap{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                '}';
    }
}
