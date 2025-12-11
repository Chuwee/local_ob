package es.onebox.event.seasontickets.dto.renewals;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class UpdateRenewalRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 20)
    @Valid
    private List<UpdateRenewalRequestItem> items;

    public List<UpdateRenewalRequestItem> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalRequestItem> items) {
        this.items = items;
    }
}
