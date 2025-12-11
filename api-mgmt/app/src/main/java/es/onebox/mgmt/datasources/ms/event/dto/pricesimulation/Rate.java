package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Rate implements Serializable {

    @Serial
    private static final long serialVersionUID = -6405993809545067282L;

    private Long id;
    private String name;
    private List<PriceType> priceTypes;
    private Long currencyId;

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

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }
}
