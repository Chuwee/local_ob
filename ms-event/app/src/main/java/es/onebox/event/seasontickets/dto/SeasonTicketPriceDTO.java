package es.onebox.event.seasontickets.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.events.enums.PriceType;

import java.io.Serializable;

public class SeasonTicketPriceDTO implements Serializable {

    private static final long serialVersionUID = 5185905408571933850L;

    private Long priceTypeId;
    private String priceTypeCode;
    private String priceTypeDescription;
    private Integer rateId;
    private String rateName;
    private Double price;
    private PriceType priceType;
    private String priceTypeColor;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public String getPriceTypeCode() {
        return priceTypeCode;
    }

    public void setPriceTypeCode(String priceTypeCode) {
        this.priceTypeCode = priceTypeCode;
    }

    public String getPriceTypeDescription() {
        return priceTypeDescription;
    }

    public void setPriceTypeDescription(String priceTypeDescription) {
        this.priceTypeDescription = priceTypeDescription;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPriceTypeColor() {
        return priceTypeColor;
    }

    public void setPriceTypeColor(String priceTypeColor) {
        this.priceTypeColor = priceTypeColor;
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
