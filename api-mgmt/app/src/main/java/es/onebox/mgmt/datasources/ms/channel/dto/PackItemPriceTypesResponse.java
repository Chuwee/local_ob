package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.enums.PriceTypeRange;

import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypesResponse implements Serializable {

    private PriceTypeRange selectionType;
    private List<IdNameDTO> priceTypes;

    public PriceTypeRange getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(PriceTypeRange selectionType) {
        this.selectionType = selectionType;
    }

    public List<IdNameDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<IdNameDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
