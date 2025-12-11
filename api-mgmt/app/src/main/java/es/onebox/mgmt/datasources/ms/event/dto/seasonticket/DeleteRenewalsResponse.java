package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class DeleteRenewalsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<DeleteRenewalsResponseItem> items;

    public List<DeleteRenewalsResponseItem> getItems() {
        return items;
    }

    public void setItems(List<DeleteRenewalsResponseItem> items) {
        this.items = items;
    }
}
