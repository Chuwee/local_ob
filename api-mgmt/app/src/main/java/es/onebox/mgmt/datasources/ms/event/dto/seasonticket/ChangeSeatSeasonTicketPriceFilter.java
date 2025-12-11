package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class ChangeSeatSeasonTicketPriceFilter implements Serializable {

    private static final long serialVersionUID = -8337874287456290298L;

    private Long sourcePriceTypeId;
    private Long targetPriceTypeId;
    private Long rateId;

    public ChangeSeatSeasonTicketPriceFilter() {
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