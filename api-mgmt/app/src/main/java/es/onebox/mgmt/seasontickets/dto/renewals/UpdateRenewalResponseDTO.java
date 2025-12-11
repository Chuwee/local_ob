package es.onebox.mgmt.seasontickets.dto.renewals;

import java.io.Serializable;
import java.util.List;

public class UpdateRenewalResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UpdateRenewalResponseItemDTO> items;

    public List<UpdateRenewalResponseItemDTO> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalResponseItemDTO> items) {
        this.items = items;
    }
}
