package es.onebox.common.datasources.ms.event.dto;


import es.onebox.common.datasources.ms.event.enums.PriceType;
import java.io.Serial;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventTemplatePriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -659369228867846560L;

    private Long priceTypeId;
    private String priceTypeCode;
    private String priceTypeDescription;
    private Integer rateId;
    private String rateName;
    private Double price;
    private PriceType priceType;
    private Integer rateGroupId;
    private String rateGroupName;

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

    public Integer getRateGroupId() {
        return rateGroupId;
    }

    public void setRateGroupId(Integer rateGroupId) {
        this.rateGroupId = rateGroupId;
    }

    public String getRateGroupName() {
        return rateGroupName;
    }

    public void setRateGroupName(String rateGroupName) {
        this.rateGroupName = rateGroupName;
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
