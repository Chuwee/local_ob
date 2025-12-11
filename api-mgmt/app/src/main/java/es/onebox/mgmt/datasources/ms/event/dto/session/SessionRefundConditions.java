package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.datasources.ms.event.dto.TicketStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class SessionRefundConditions implements Serializable {

    private static final long serialVersionUID = -8263921499098923722L;

    private TicketStatus refundedSeatStatus;
    private Long refundedSeatBlockReasonId;
    private SessionRefundedSeatQuota refundedSeatQuota;
    //for session pack
    private Boolean printRefundPrice;
    private Boolean seasonPackAutomaticCalculateConditions;
    private Map<Long, SessionConditions> sessionPackRefundConditions;

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

    public SessionRefundedSeatQuota getRefundedSeatQuota() {
        return refundedSeatQuota;
    }

    public void setRefundedSeatQuota(SessionRefundedSeatQuota refundedSeatQuota) {
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

    public Map<Long, SessionConditions> getSessionPackRefundConditions() {
        return sessionPackRefundConditions;
    }

    public void setSessionPackRefundConditions(Map<Long, SessionConditions> sessionPackRefundConditions) {
        this.sessionPackRefundConditions = sessionPackRefundConditions;
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
