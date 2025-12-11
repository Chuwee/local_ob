package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionRefundConditionsTicketStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionRefundConditionsBaseDTO implements Serializable {

    private static final long serialVersionUID = -1973415132488317390L;

    @JsonProperty("refunded_seat_status")
    private SessionRefundConditionsTicketStatus refundedSeatStatus;
    @JsonProperty("refunded_seat_block_reason_id")
    private Long refundedSeatBlockReasonId;
    @JsonProperty("refunded_seat_quota")
    private SessionRefundedSeatQuotaDTO refundedSeatQuota;
    //for session pack
    @JsonProperty("print_refund_price_in_ticket")
    private Boolean printRefundPrice;
    @JsonProperty("session_pack_automatically_calculate_conditions")
    private Boolean seasonPackAutomaticCalculateConditions;

    public SessionRefundConditionsTicketStatus getRefundedSeatStatus() {
        return refundedSeatStatus;
    }

    public void setRefundedSeatStatus(SessionRefundConditionsTicketStatus refundedSeatStatus) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
