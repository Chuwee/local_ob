package es.onebox.event.priceengine.simulation.domain;

import es.onebox.event.priceengine.simulation.domain.enums.PriceType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Rate implements Serializable {

    @Serial
    private static final long serialVersionUID = 6415607390938115293L;

    private Long id;
    private String name;
    private List<PriceType> priceTypes;

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

    public List<PriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
