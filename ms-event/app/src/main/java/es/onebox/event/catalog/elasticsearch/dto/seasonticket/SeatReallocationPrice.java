package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class SeatReallocationPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = -8138771445119848508L;

    private Integer sourcePriceType;
    private Integer targetPriceType;
    private Integer rateId;
    private Double value;

    public Integer getSourcePriceType() {
        return sourcePriceType;
    }

    public void setSourcePriceType(Integer sourcePriceType) {
        this.sourcePriceType = sourcePriceType;
    }

    public Integer getTargetPriceType() {
        return targetPriceType;
    }

    public void setTargetPriceType(Integer targetPriceType) {
        this.targetPriceType = targetPriceType;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}