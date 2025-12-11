package es.onebox.event.datasources.ms.ticket.dto.seasontickets;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class UpdateRelatedSeatsRequestItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String userId;
    @NotNull
    private Long seasonTicketId;
    @NotNull
    private String renewalId;
    @NotNull
    private Long seatId;

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
}
