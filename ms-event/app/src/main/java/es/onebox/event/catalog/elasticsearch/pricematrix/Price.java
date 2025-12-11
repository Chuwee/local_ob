/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.catalog.elasticsearch.pricematrix;

import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author ignasi
 */
public class Price implements Serializable {

    @Serial
    private static final long serialVersionUID = -4948583830447388146L;

    private Double value;
    private PriceSurcharges surcharge;
    private List<DynamicPriceTranslationDTO> dynamicPriceTranslations;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PriceSurcharges getSurcharge() { return surcharge; }

    public void setSurcharge(PriceSurcharges surcharge) {
        this.surcharge = surcharge;
    }

    public List<DynamicPriceTranslationDTO> getDynamicPriceTranslationDTO() {
        return dynamicPriceTranslations;
    }

    public void setDynamicPriceTranslationDTO(List<DynamicPriceTranslationDTO> dynamicPriceTranslations) {
        this.dynamicPriceTranslations = dynamicPriceTranslations;
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
