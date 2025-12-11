package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.event.catalog.elasticsearch.enums.RateGroupType;

import java.util.List;
import java.util.Map;

public class RateGroup {

    private Integer id;
    private String name;
    private RateGroupType type;
    private Map<String, String> translations;
    private List<Integer> rates;
    private Integer position;

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

    public RateGroupType getType() {
        return type;
    }

    public void setType(RateGroupType type) {
        this.type = type;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }


    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public List<Integer> getRates() {
        return rates;
    }

    public void setRates(List<Integer> rates) {
        this.rates = rates;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
