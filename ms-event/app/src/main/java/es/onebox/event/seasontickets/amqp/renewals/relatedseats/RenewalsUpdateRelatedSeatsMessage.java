package es.onebox.event.seasontickets.amqp.renewals.relatedseats;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class RenewalsUpdateRelatedSeatsMessage extends AbstractNotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long renewalSeasonTicketSessionId;

    @Size(min = 1, max = 1000)
    private List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats;

    @Size(min = 1, max = 1000)
    private List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats;

    public Long getRenewalSeasonTicketSessionId() {
        return renewalSeasonTicketSessionId;
    }

    public void setRenewalSeasonTicketSessionId(Long renewalSeasonTicketSessionId) {
        this.renewalSeasonTicketSessionId = renewalSeasonTicketSessionId;
    }

    public List<RenewalsUpdateRelatedSeatsRequestItem> getUnblockSeats() {
        return unblockSeats;
    }

    public void setUnblockSeats(List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats) {
        this.unblockSeats = unblockSeats;
    }

    public List<RenewalsUpdateRelatedSeatsRequestItem> getBlockSeats() {
        return blockSeats;
    }

    public void setBlockSeats(List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats) {
        this.blockSeats = blockSeats;
    }
}
