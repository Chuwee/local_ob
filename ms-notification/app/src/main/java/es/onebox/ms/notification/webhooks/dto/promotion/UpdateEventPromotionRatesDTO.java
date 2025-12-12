package es.onebox.ms.notification.webhooks.dto.promotion;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateEventPromotionRatesDTO extends PromotionTarget {

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
