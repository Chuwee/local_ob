package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VenueTemplatePrice implements Serializable {

    private static final long serialVersionUID = 2240891514841162458L;

    private Integer id;
    private String name;
    private List<PriceZonePrice> priceZones;

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

    public List<PriceZonePrice> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<PriceZonePrice> priceZones) {
        this.priceZones = priceZones;
    }
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
