package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AlternativeSurcharges implements Serializable {

    public AlternativeSurcharges() {
    }

    public AlternativeSurcharges(Boolean useAlternativeSurcharges, Boolean useAlternativePromoterSurcharges) {
        this.useAlternativeSurcharges = useAlternativeSurcharges;
        this.useAlternativePromoterSurcharges = useAlternativePromoterSurcharges;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean useAlternativeSurcharges;
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

    public void setUseAlternativePromoterSurcharges(Boolean promoterSurcharges) {
        this.useAlternativePromoterSurcharges = promoterSurcharges;
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
