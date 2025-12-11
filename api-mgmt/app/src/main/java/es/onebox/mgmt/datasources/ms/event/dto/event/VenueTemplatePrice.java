package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.PriceType;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplatePrice implements Serializable {

    @Serial
    private static final long serialVersionUID = -6560592646986164117L;

    private Long priceTypeId;
    private String priceTypeCode;
    private String priceTypeDescription;
    private Integer rateId;
    private String rateName;
    private Double price;
    private PriceType priceType;
    private AdditionalConfigDTO additionalConfig;
    private Integer rateGroupId;
    private String rateGroupName;

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
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

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
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
}
