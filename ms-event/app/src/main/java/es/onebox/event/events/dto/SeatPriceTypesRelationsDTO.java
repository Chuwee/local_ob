package es.onebox.event.events.dto;

import java.util.List;

public class SeatPriceTypesRelationsDTO {

    private Long sourcePriceTypeId;
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
