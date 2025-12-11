package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.packs.enums.PriceTypeRangeDTO;

import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypesResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("selection_type")
    private PriceTypeRangeDTO selectionType;

    @JsonProperty("price_types")
    private List<IdNameDTO> priceTypes;

    public PriceTypeRangeDTO getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(PriceTypeRangeDTO selectionType) {
        this.selectionType = selectionType;
    }

    public List<IdNameDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<IdNameDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
