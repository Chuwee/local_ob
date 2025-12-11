package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.packs.enums.PriceTypeRangeDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypesRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("price_type_ids")
    private List<Integer> priceTypeIds;

    @NotNull(message = "selectionType is mandatory and can not be null")
    @JsonProperty("selection_type")
    private PriceTypeRangeDTO selectionType;

    public List<Integer> getPriceTypeIds() {
        return priceTypeIds;
    }

    public void setPriceTypeIds(List<Integer> priceTypeIds) {
        this.priceTypeIds = priceTypeIds;
    }

    public PriceTypeRangeDTO getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(PriceTypeRangeDTO selectionType) {
        this.selectionType = selectionType;
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
