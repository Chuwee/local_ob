package es.onebox.event.packs.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> priceTypeIds;

    @NotNull(message = "selectionType is mandatory and can not be null")
    private PriceTypeRange selectionType;

    public PackItemPriceTypeRequest() {
    }

    public List<Integer> getPriceTypeIds() {
        return priceTypeIds;
    }

    public void setPriceTypeIds(List<Integer> priceTypeIds) {
        this.priceTypeIds = priceTypeIds;
    }

    public PriceTypeRange getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(PriceTypeRange selectionType) {
        this.selectionType = selectionType;
    }
}
