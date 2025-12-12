package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class EventPromotionPriceTypesFeverDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<PromotionPriceTypeFeverDTO> priceTypes;

    public void setPriceTypes(Set<PromotionPriceTypeFeverDTO> result) {
        this.priceTypes = result;
    }

    public Set<PromotionPriceTypeFeverDTO> getPriceTypes() {
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
