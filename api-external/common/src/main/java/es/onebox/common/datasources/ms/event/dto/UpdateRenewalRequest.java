package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;
import java.util.List;

public class UpdateRenewalRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UpdateRenewalRequestItem> items;

    public List<UpdateRenewalRequestItem> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalRequestItem> items) {
        this.items = items;
    }
}
