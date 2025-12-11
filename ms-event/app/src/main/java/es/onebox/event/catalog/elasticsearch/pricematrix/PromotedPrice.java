/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.catalog.elasticsearch.pricematrix;

import es.onebox.event.promotions.dto.restriction.PromotionValidationPeriod;
import es.onebox.event.promotions.enums.PromotionPriceVariationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class PromotedPrice extends Price {

    @Serial
    private static final long serialVersionUID = 3107050970229914181L;

    private Long eventPromotionTemplateId;
    private Double discountedValue;
    private Double originalPrice;
    private PromotionPriceVariationType variationType;
    private Double variationValue;

    private PromotionValidationPeriod validationPeriod;

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

    public PromotionValidationPeriod getValidationPeriod() {
        return validationPeriod;
    }

    public void setValidationPeriod(PromotionValidationPeriod validationPeriod) {
        this.validationPeriod = validationPeriod;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public PromotionPriceVariationType getVariationType() {
        return variationType;
    }

    public void setVariationType(PromotionPriceVariationType variationType) {
        this.variationType = variationType;
    }

    public Double getVariationValue() {
        return variationValue;
    }

    public void setVariationValue(Double variationValue) {
        this.variationValue = variationValue;
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
