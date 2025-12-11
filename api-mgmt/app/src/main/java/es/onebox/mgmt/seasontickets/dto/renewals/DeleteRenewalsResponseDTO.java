package es.onebox.mgmt.seasontickets.dto.renewals;

import java.io.Serializable;
import java.util.List;

public class DeleteRenewalsResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<DeleteRenewalsResponseItemDTO> items;

    public List<DeleteRenewalsResponseItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DeleteRenewalsResponseItemDTO> items) {
        this.items = items;
    }
}
