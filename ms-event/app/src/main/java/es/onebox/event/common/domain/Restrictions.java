package es.onebox.event.common.domain;

import java.io.Serial;
import java.io.Serializable;

public class Restrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PriceZonesRestrictions priceZones;
    private RatesRestrictions rates;
    private CountryConfig countryConfig;
    private SaleRestrictions sale;

    public PriceZonesRestrictions getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(PriceZonesRestrictions priceZones) {
        this.priceZones = priceZones;
    }

    public RatesRestrictions getRates() {
        return rates;
    }

    public void setRates(RatesRestrictions rates) {
        this.rates = rates;
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
