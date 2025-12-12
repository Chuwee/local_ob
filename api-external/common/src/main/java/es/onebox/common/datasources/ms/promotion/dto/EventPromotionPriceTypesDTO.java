package es.onebox.common.datasources.ms.promotion.dto;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventPromotionPriceTypesDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<PromotionPriceTypeDTO> priceTypes;

    public void setPriceTypes(Set<PromotionPriceTypeDTO> result) {
        this.priceTypes = result;
    }

    public Set<PromotionPriceTypeDTO> getPriceTypes() {
        return priceTypes;
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
