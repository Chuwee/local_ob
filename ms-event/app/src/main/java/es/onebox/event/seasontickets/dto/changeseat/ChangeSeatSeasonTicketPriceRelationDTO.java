package es.onebox.event.seasontickets.dto.changeseat;

import java.io.Serializable;

public class ChangeSeatSeasonTicketPriceRelationDTO implements Serializable {

    private static final long serialVersionUID = -1990625723418990274L;

    private Long relationId;

    private Long seasonTicketId;

    private Long sourcePriceTypeId;

    private String sourcePriceTypeName;

    private Long targetPriceTypeId;

    private String targetPriceTypeName;

    private Long rateId;

    private String rateName;

    private Double value;

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(Long sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public String getSourcePriceTypeName() {
        return sourcePriceTypeName;
    }

    public void setSourcePriceTypeName(String sourcePriceTypeName) {
        this.sourcePriceTypeName = sourcePriceTypeName;
    }

    public Long getTargetPriceTypeId() {
        return targetPriceTypeId;
    }

    public void setTargetPriceTypeId(Long targetPriceTypeId) {
        this.targetPriceTypeId = targetPriceTypeId;
    }

    public String getTargetPriceTypeName() {
        return targetPriceTypeName;
    }

    public void setTargetPriceTypeName(String targetPriceTypeName) {
        this.targetPriceTypeName = targetPriceTypeName;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}