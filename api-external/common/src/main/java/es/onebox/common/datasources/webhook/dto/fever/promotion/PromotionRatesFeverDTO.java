package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class PromotionRatesFeverDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<IdNameDTO> rates;

    public Set<IdNameDTO> getRates() {
        return rates;
    }

    public void setRates(Set<IdNameDTO> rates) {
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
