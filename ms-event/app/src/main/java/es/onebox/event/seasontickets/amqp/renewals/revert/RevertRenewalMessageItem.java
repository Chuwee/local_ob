package es.onebox.event.seasontickets.amqp.renewals.revert;

import java.io.Serializable;

public class RevertRenewalMessageItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private String userId;
    private String id;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
