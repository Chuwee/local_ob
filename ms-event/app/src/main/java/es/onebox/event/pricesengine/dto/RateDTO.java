package es.onebox.event.pricesengine.dto;

import java.io.Serializable;
import java.util.List;

public class RateDTO implements Serializable {

    private static final long serialVersionUID = 6415607390938115293L;

    private Long id;
    private String name;
    private Long currencyId;
    private List<PriceTypeDTO> priceTypes;

    public RateDTO() {
    }

    public RateDTO(Long id, String name, List<PriceTypeDTO> priceTypes, Long currencyId) {
        this.id = id;
        this.name = name;
        this.priceTypes = priceTypes;
        this.currencyId = currencyId;
    }

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

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public List<PriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
