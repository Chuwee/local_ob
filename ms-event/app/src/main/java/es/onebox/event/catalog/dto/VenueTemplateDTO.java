package es.onebox.event.catalog.dto;

import java.io.Serializable;
import java.util.List;

public class VenueTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private List<PriceZoneDTO> priceZones;

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

    public List<PriceZoneDTO> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<PriceZoneDTO> priceZones) {
        this.priceZones = priceZones;
    }
}
