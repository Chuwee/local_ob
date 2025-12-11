package es.onebox.event.sessions.dto;

import es.onebox.event.common.domain.CountryConfig;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.SaleRestrictions;

import java.io.Serializable;
import java.util.Map;

public class RestrictionsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<Integer, PriceZoneRestriction> priceZones;
    private CountryConfig countryConfig;
    private SaleRestrictions sale;

    public Map<Integer, PriceZoneRestriction> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(Map<Integer, PriceZoneRestriction> priceZones) {
        this.priceZones = priceZones;
    }

    public CountryConfig getCountryConfig() {
        return countryConfig;
    }

    public void setCountryConfig(CountryConfig countryConfig) {
        this.countryConfig = countryConfig;
    }

    public SaleRestrictions getSale() {
        return sale;
    }

    public void setSale(SaleRestrictions sale) {
        this.sale = sale;
    }
}
