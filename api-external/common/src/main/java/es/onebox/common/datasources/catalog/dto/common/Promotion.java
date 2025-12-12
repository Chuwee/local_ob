package es.onebox.common.datasources.catalog.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.promotion.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Promotion implements Serializable {

    private static final long serialVersionUID = -74393738911951518L;

    private Long id;
    private String name;
    private PromotionCommunicationElements texts;
    @JsonProperty("validity_period")
    private PromotionValidityPeriod validityPeriod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionCommunicationElements getTexts() {
        return texts;
    }

    public void setTexts(PromotionCommunicationElements texts) {
        this.texts = texts;
    }

    public PromotionValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PromotionValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
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
