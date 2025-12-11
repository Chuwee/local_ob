package es.onebox.event.seasontickets.dto.changeseat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class UpdateSeasonTicketChangeSeat implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private ZonedDateTime changeSeatStartingDate;
    private ZonedDateTime changeSeatEndDate;
    private Boolean changeSeatEnabled;
    private Boolean maxChangeSeatValueEnabled;
    private Integer maxChangeSeatValue;
    private Double fixedSurcharge;
    private ChangedSeatQuota changedSeatQuota;
    private ChangedSeatStatus changedSeatStatus;
    private Integer changedSeatBlockReasonId;
    private LimitChangeSeatQuotas limitChangeSeatQuotas;

    public ZonedDateTime getChangeSeatStartingDate() {
        return changeSeatStartingDate;
    }

    public void setChangeSeatStartingDate(ZonedDateTime changeSeatStartingDate) {
        this.changeSeatStartingDate = changeSeatStartingDate;
    }

    public ZonedDateTime getChangeSeatEndDate() {
        return changeSeatEndDate;
    }

    public void setChangeSeatEndDate(ZonedDateTime changeSeatEndDate) {
        this.changeSeatEndDate = changeSeatEndDate;
    }

    public Boolean getChangeSeatEnabled() {
        return changeSeatEnabled;
    }

    public void setChangeSeatEnabled(Boolean changeSeatEnabled) {
        this.changeSeatEnabled = changeSeatEnabled;
    }

    public Boolean getMaxChangeSeatValueEnabled() {
        return maxChangeSeatValueEnabled;
    }

    public void setMaxChangeSeatValueEnabled(Boolean maxChangeSeatValueEnabled) {
        this.maxChangeSeatValueEnabled = maxChangeSeatValueEnabled;
    }

    public Integer getMaxChangeSeatValue() {
        return maxChangeSeatValue;
    }

    public void setMaxChangeSeatValue(Integer maxChangeSeatValue) {
        this.maxChangeSeatValue = maxChangeSeatValue;
    }

    public Double getFixedSurcharge() {
        return fixedSurcharge;
    }

    public void setFixedSurcharge(Double fixedSurcharge) {
        this.fixedSurcharge = fixedSurcharge;
    }

    public ChangedSeatQuota getChangedSeatQuota() {
        return changedSeatQuota;
    }

    public void setChangedSeatQuota(ChangedSeatQuota changedSeatQuota) {
        this.changedSeatQuota = changedSeatQuota;
    }

    public ChangedSeatStatus getChangedSeatStatus() {
        return changedSeatStatus;
    }

    public void setChangedSeatStatus(ChangedSeatStatus changedSeatStatus) {
        this.changedSeatStatus = changedSeatStatus;
    }

    public Integer getChangedSeatBlockReasonId() {
        return changedSeatBlockReasonId;
    }

    public void setChangedSeatBlockReasonId(Integer changedSeatBlockReasonId) {
        this.changedSeatBlockReasonId = changedSeatBlockReasonId;
    }

    public LimitChangeSeatQuotas getLimitChangeSeatQuotas() {
        return limitChangeSeatQuotas;
    }

    public void setLimitChangeSeatQuotas(LimitChangeSeatQuotas limitChangeSeatQuotas) {
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
