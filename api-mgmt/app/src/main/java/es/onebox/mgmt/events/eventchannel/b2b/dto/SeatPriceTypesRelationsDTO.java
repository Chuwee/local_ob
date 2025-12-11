package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.PriceTypeDTO;

import java.io.Serializable;
import java.util.List;

public class SeatPriceTypesRelationsDTO implements Serializable {

    @JsonProperty("source_price_type_id")
    private PriceTypeDTO sourcePriceTypeId;
    @JsonProperty("target_price_type_ids")
    private List<PriceTypeDTO> targetPriceTypeIds;

    public PriceTypeDTO getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(PriceTypeDTO sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public List<PriceTypeDTO> getTargetPriceTypeIds() {
        return targetPriceTypeIds;
    }

    public void setTargetPriceTypeIds(List<PriceTypeDTO> targetPriceTypeIds) {
        this.targetPriceTypeIds = targetPriceTypeIds;
    }
}
