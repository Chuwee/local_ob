package es.onebox.mgmt.seasontickets.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketSeatLinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids is mandatory")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
