package es.onebox.event.seasontickets.dto.changeseat;

import java.io.Serializable;

public class ChangeSeatSeasonTicketPriceRelation implements Serializable {

    private static final long serialVersionUID = -1990625723418990274L;

    private Long sourcePriceTypeId;

    private Long targetPriceTypeId;

    private Long rateId;

    private Double value;

    public Long getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(Long sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public Long getTargetPriceTypeId() {
        return targetPriceTypeId;
    }

    public void setTargetPriceTypeId(Long targetPriceTypeId) {
        this.targetPriceTypeId = targetPriceTypeId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}