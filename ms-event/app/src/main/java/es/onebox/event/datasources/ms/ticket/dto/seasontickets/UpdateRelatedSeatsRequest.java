package es.onebox.event.datasources.ms.ticket.dto.seasontickets;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class UpdateRelatedSeatsRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 1000)
    private List<UpdateRelatedSeatsRequestItem> unblockSeats;

    @Size(max = 1000)
    private List<UpdateRelatedSeatsRequestItem> blockSeats;

    public List<UpdateRelatedSeatsRequestItem> getUnblockSeats() {
        return unblockSeats;
    }

    public void setUnblockSeats(List<UpdateRelatedSeatsRequestItem> unblockSeats) {
        this.unblockSeats = unblockSeats;
    }

    public List<UpdateRelatedSeatsRequestItem> getBlockSeats() {
        return blockSeats;
    }

    public void setBlockSeats(List<UpdateRelatedSeatsRequestItem> blockSeats) {
        this.blockSeats = blockSeats;
    }
}
