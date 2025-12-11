package es.onebox.mgmt.salerequests.pricesimulation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RateDTO implements Serializable {

    private static final long serialVersionUID = -6405993809545067282L;

    private Long id;
    private String name;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("price_types")
    private List<PriceTypeDTO> priceTypes;

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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<PriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
