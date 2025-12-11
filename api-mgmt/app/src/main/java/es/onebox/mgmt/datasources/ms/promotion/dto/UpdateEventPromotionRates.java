package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class UpdateEventPromotionRates extends PromotionTarget {

	private static final long serialVersionUID = 1L;

    private Set<Long> rates;

    public Set<Long> getRates() {
        return rates;
    }

    public void setRates(Set<Long> rates) {
        this.rates = rates;
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
