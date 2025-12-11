package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.dto.promotion.CatalogPromotionValidationPeriodDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CatalogPromotedPrice extends CatalogPrice {

    private static final long serialVersionUID = -9064603782332562440L;

    private Long eventPromotionTemplateId;
    private Double discountedValue;
    private CatalogPromotionValidationPeriodDTO validityPeriod;
    private Double originalPrice;
    private CatalogPromotedPriceVariation variation;

    public Long getEventPromotionTemplateId() {
        return eventPromotionTemplateId;
    }

    public void setEventPromotionTemplateId(Long eventPromotionTemplateId) {
        this.eventPromotionTemplateId = eventPromotionTemplateId;
    }

    public Double getDiscountedValue() {
        return discountedValue;
    }

    public void setDiscountedValue(Double discountedValue) {
        this.discountedValue = discountedValue;
    }

    public CatalogPromotionValidationPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(CatalogPromotionValidationPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public CatalogPromotedPriceVariation getVariation() {
        return variation;
    }

    public void setVariation(CatalogPromotedPriceVariation variation) {
        this.variation = variation;
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
