package es.onebox.internal.xmlsepa.eip.sepa;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

public class SEPADirectDebitMessage extends AbstractNotificationMessage {
    private Long seasonTicketId;
    private Long userId;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
