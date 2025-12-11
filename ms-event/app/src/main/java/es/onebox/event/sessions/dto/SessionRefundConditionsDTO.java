package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;

import java.io.Serializable;
import java.util.Map;

public class SessionRefundConditionsDTO implements Serializable {

    private static final long serialVersionUID = 5507613308473790274L;

    private TicketStatus refundedSeatStatus;
    private Long refundedSeatBlockReasonId;
    private Long refundedSessionPackSeatBlockReasonId;
    private SessionRefundedSeatQuotaDTO refundedSeatQuota;
    //for session pack
    private Boolean printRefundPrice;
    private Boolean seasonPackAutomaticCalculateConditions;
    private Map<Long, SessionConditionsDTO> sessionPackRefundConditions;

    public TicketStatus getRefundedSeatStatus() {
        return refundedSeatStatus;
    }

    public void setRefundedSeatStatus(TicketStatus refundedSeatStatus) {
        this.refundedSeatStatus = refundedSeatStatus;
    }

    public Long getRefundedSeatBlockReasonId() {
        return refundedSeatBlockReasonId;
    }

    public void setRefundedSeatBlockReasonId(Long refundedSeatBlockReasonId) {
        this.refundedSeatBlockReasonId = refundedSeatBlockReasonId;
    }

    public SessionRefundedSeatQuotaDTO getRefundedSeatQuota() {
        return refundedSeatQuota;
    }

    public void setRefundedSeatQuota(SessionRefundedSeatQuotaDTO refundedSeatQuota) {
        this.refundedSeatQuota = refundedSeatQuota;
    }

    public Boolean getPrintRefundPrice() {
        return printRefundPrice;
    }

    public void setPrintRefundPrice(Boolean printRefundPrice) {
        this.printRefundPrice = printRefundPrice;
    }

    public Boolean getSeasonPackAutomaticCalculateConditions() {
        return seasonPackAutomaticCalculateConditions;
    }

    public void setSeasonPackAutomaticCalculateConditions(Boolean seasonPackAutomaticCalculateConditions) {
        this.seasonPackAutomaticCalculateConditions = seasonPackAutomaticCalculateConditions;
    }

    public Map<Long, SessionConditionsDTO> getSessionPackRefundConditions() {
        return sessionPackRefundConditions;
    }

    public void setSessionPackRefundConditions(Map<Long, SessionConditionsDTO> sessionPackRefundConditions) {
        this.sessionPackRefundConditions = sessionPackRefundConditions;
    }

    public Long getRefundedSessionPackSeatBlockReasonId() {
        return refundedSessionPackSeatBlockReasonId;
    }

    public void setRefundedSessionPackSeatBlockReasonId(Long refundedSessionPackSeatBlockReasonId) {
        this.refundedSessionPackSeatBlockReasonId = refundedSessionPackSeatBlockReasonId;
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
