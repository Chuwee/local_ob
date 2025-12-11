package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class EventPromotionPriceTypesDTO extends PromotionType {

    private static final long serialVersionUID = 2L;

    @JsonProperty("price_types")
    private Set<IdNameDTO> priceTypes;

    public Set<IdNameDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(Set<IdNameDTO> priceTypes) {
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
