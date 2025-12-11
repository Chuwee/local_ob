package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.packs.enums.PriceTypeRangeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypesRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> priceTypeIds;

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
