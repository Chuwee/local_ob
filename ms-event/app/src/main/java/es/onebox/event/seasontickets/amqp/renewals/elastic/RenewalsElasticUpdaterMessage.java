package es.onebox.event.seasontickets.amqp.renewals.elastic;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

public class RenewalsElasticUpdaterMessage extends AbstractNotificationMessage {
    private static final long serialVersionUID = 1L;

    private String userId;
    private Long seasonTicketId;
    private Long totalRenewals;
    private Long rateId;

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

    public Long getTotalRenewals() {
        return totalRenewals;
    }

    public void setTotalRenewals(Long totalRenewals) {
        this.totalRenewals = totalRenewals;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }
}
