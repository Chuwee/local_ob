package es.onebox.mgmt.seasontickets.dto.renewals;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateRenewalRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 20)
    @Valid
    private List<UpdateRenewalRequestItemDTO> items;

    public List<UpdateRenewalRequestItemDTO> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalRequestItemDTO> items) {
        this.items = items;
    }
}
