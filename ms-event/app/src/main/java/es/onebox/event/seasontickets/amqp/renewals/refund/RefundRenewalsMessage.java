package es.onebox.event.seasontickets.amqp.renewals.refund;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

public class RefundRenewalsMessage extends AbstractNotificationMessage {
    private static final long serialVersionUID = 1L;

    private String userId;
    private Long seasonTicketId;
    private String renewalId;
    private String orderCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public String getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(String renewalId) {
        this.renewalId = renewalId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
