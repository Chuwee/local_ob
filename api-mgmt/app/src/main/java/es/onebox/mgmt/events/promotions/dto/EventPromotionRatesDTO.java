package es.onebox.mgmt.events.promotions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class EventPromotionRatesDTO extends PromotionType {

    private static final long serialVersionUID = 2L;

    private Set<IdNameDTO> rates;

    public void setRates(Set<IdNameDTO> rates) {
        this.rates = rates;
    }

    public Set<IdNameDTO> getRates() {
        return rates;
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
