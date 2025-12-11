package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class UpdateRenewalRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private List<UpdateRenewalRequestItem> items;

    public List<UpdateRenewalRequestItem> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalRequestItem> items) {
        this.items = items;
    }
}
