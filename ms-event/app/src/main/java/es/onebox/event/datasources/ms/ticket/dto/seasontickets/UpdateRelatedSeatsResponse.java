package es.onebox.event.datasources.ms.ticket.dto.seasontickets;

import java.io.Serializable;
import java.util.List;

public class UpdateRelatedSeatsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UpdateRelatedSeatsResponseItem> unblockSeatsResponse;

    private List<UpdateRelatedSeatsResponseItem> blockSeatsResponse;

    public List<UpdateRelatedSeatsResponseItem> getUnblockSeatsResponse() {
        return unblockSeatsResponse;
    }

    public void setUnblockSeatsResponse(List<UpdateRelatedSeatsResponseItem> unblockSeatsResponse) {
        this.unblockSeatsResponse = unblockSeatsResponse;
    }

    public List<UpdateRelatedSeatsResponseItem> getBlockSeatsResponse() {
        return blockSeatsResponse;
    }

    public void setBlockSeatsResponse(List<UpdateRelatedSeatsResponseItem> blockSeatsResponse) {
        this.blockSeatsResponse = blockSeatsResponse;
    }
}
