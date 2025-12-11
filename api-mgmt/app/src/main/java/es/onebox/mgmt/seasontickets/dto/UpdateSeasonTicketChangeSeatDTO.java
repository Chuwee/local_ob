package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangedSeatQuotaDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.LimitChangeSeatQuotasDTO;
import es.onebox.mgmt.seasontickets.enums.ChangedSeatStatusDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class UpdateSeasonTicketChangeSeatDTO implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private Boolean enable;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    private ZonedDateTime endDate;
    @JsonProperty("enable_max_value")
    private Boolean enableMaxValue;
    @Min(value = 1, message = "Max value must be a positive integer")
    @JsonProperty("max_value")
    private Integer maxValue;
    @Min(value = 0, message = "Surcharges can't be negative")
    @JsonProperty("fixed_surcharge")
    private Double fixedSurcharge;
    @JsonProperty("changed_seat_quota")
    private ChangedSeatQuotaDTO changedSeatQuota;
    @JsonProperty("changed_seat_status")
    private ChangedSeatStatusDTO changedSeatStatus;
    @JsonProperty("changed_seat_block_reason_id")
    private Integer changedSeatBlockReasonId;
    @JsonProperty("limit_change_seat_quotas")
    private LimitChangeSeatQuotasDTO limitChangeSeatQuotas;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getEnableMaxValue() {
        return enableMaxValue;
    }

    public void setEnableMaxValue(Boolean enableMaxValue) {
        this.enableMaxValue = enableMaxValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Double getFixedSurcharge() {
        return fixedSurcharge;
    }

    public void setFixedSurcharge(Double fixedSurcharge) {
        this.fixedSurcharge = fixedSurcharge;
    }

    public ChangedSeatQuotaDTO getChangedSeatQuota() {
        return changedSeatQuota;
    }

    public void setChangedSeatQuota(ChangedSeatQuotaDTO changedSeatQuota) {
        this.changedSeatQuota = changedSeatQuota;
    }

    public ChangedSeatStatusDTO getChangedSeatStatus() {
        return changedSeatStatus;
    }

    public void setChangedSeatStatus(ChangedSeatStatusDTO changedSeatStatus) {
        this.changedSeatStatus = changedSeatStatus;
    }

    public Integer getChangedSeatBlockReasonId() {
        return changedSeatBlockReasonId;
    }

    public void setChangedSeatBlockReasonId(Integer changedSeatBlockReasonId) {
        this.changedSeatBlockReasonId = changedSeatBlockReasonId;
    }

    public LimitChangeSeatQuotasDTO getLimitChangeSeatQuotas() {
        return limitChangeSeatQuotas;
    }

    public void setLimitChangeSeatQuotas(LimitChangeSeatQuotasDTO limitChangeSeatQuotas) {
        this.limitChangeSeatQuotas = limitChangeSeatQuotas;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
