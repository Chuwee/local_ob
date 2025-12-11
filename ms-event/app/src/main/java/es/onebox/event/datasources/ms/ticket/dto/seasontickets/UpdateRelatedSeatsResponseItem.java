package es.onebox.event.datasources.ms.ticket.dto.seasontickets;

import java.io.Serializable;

public class UpdateRelatedSeatsResponseItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private Long seasonTicketId;
    private String renewalId;
    private Long seatId;
    private Boolean result;

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

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
