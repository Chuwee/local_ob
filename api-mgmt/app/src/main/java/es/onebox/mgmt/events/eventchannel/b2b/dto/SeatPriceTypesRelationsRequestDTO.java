package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SeatPriceTypesRelationsRequestDTO {

    @JsonProperty("source_price_type_id")
    private Long sourcePriceTypeId;

    @JsonProperty("target_price_type_ids")
    private List<Long> targetPriceTypeIds;

    public Long getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(Long sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public List<Long> getTargetPriceTypeIds() {
        return targetPriceTypeIds;
    }

    public void setTargetPriceTypeIds(List<Long> targetPriceTypeIds) {
        this.targetPriceTypeIds = targetPriceTypeIds;
    }
}
