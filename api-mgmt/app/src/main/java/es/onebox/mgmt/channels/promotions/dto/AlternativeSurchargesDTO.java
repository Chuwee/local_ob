package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AlternativeSurchargesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("use_alternative_surcharges")
    private Boolean useAlternativeSurcharges;
    @JsonProperty("use_alternative_promoter_surcharges")
    private Boolean useAlternativePromoterSurcharges;

    public Boolean getUseAlternativeSurcharges() {
        return useAlternativeSurcharges;
    }

    public void setUseAlternativeSurcharges(Boolean useAlternativeSurcharges) {
        this.useAlternativeSurcharges = useAlternativeSurcharges;
    }

    public Boolean getUseAlternativePromoterSurcharges() {
        return useAlternativePromoterSurcharges;
    }

    public void setUseAlternativePromoterSurcharges(Boolean useAlternativePromoterSurcharges) {
        this.useAlternativePromoterSurcharges = useAlternativePromoterSurcharges;
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
