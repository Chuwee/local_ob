package es.onebox.mgmt.seasontickets.dto.changeseats;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ChangeSeatSeasonTicketPriceFilterDTO implements Serializable {

    private static final long serialVersionUID = -8337874287456290298L;

    @JsonProperty("source_price_type_id")
    private Long sourcePriceTypeId;

    @JsonProperty("target_price_type_id")
    private Long targetPriceTypeId;

    @JsonProperty("rate_id")
    private Long rateId;

    public ChangeSeatSeasonTicketPriceFilterDTO() {
    }

    public ChangeSeatSeasonTicketPriceFilterDTO(Long sourcePriceTypeId, Long targetPriceTypeId, Long rateId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
        this.targetPriceTypeId = targetPriceTypeId;
        this.rateId = rateId;
    }

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

}