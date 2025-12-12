package es.onebox.ms.notification.webhooks.dto.promotion;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateEventPromotionPriceTypesDTO extends PromotionTarget {

    private static final long serialVersionUID = 1L;

    private Set<Long> priceTypes;

    public Set<Long> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(Set<Long> priceTypes) {
        this.priceTypes = priceTypes;
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
