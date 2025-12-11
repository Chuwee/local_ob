package es.onebox.event.sessions.domain.sessionconfig;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;

import java.io.Serializable;
import java.util.Map;

public class SessionRefundConditions implements Serializable {

    private static final long serialVersionUID = -7104279849222582191L;

    private TicketStatus refundedSeatStatus;
    private Long refundedSeatBlockReasonId;
    private Long refundedSessionPackSeatBlockReasonId;
    private SessionRefundedSeatQuota refundedSeatQuota;
    //for session pack
    private Boolean printRefundPrice;
    private Boolean seasonPackAutomaticCalculateConditions;
    private Map<Long, SessionConditionsMap> seasonPassRefundConditions;

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

    public Map<Long, SessionConditionsMap> getSeasonPassRefundConditions() {
        return seasonPassRefundConditions;
    }

    public void setSeasonPassRefundConditions(Map<Long, SessionConditionsMap> seasonPassRefundConditions) {
        this.seasonPassRefundConditions = seasonPassRefundConditions;
    }

    public Long getRefundedSessionPackSeatBlockReasonId() {
        return refundedSessionPackSeatBlockReasonId;
    }

    public void setRefundedSessionPackSeatBlockReasonId(Long refundedSessionPackSeatBlockReasonId) {
        this.refundedSessionPackSeatBlockReasonId = refundedSessionPackSeatBlockReasonId;
    }
}
