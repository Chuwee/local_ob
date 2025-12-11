package es.onebox.event.priceengine.simulation.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class PriceZoneConfigWithPrice {

    private Double price;
    private CpanelZonaPreciosConfigRecord priceZoneConfig;

    public PriceZoneConfigWithPrice() {
    }

    public PriceZoneConfigWithPrice(Double price, CpanelZonaPreciosConfigRecord priceZoneConfig) {
        this.price = price;
        this.priceZoneConfig = priceZoneConfig;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CpanelZonaPreciosConfigRecord getPriceZoneConfig() {
        return priceZoneConfig;
    }

    public void setPriceZoneConfig(CpanelZonaPreciosConfigRecord priceZoneConfig) {
        this.priceZoneConfig = priceZoneConfig;
    }
}
