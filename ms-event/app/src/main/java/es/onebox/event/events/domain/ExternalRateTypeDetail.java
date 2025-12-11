package es.onebox.event.events.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;

@CouchDocument
public class ExternalRateTypeDetail implements Serializable {

    private Long priceZoneId;
    private Long rateId;
    private String externalCode;
    private Double basePrice;
    private Double discount;

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
