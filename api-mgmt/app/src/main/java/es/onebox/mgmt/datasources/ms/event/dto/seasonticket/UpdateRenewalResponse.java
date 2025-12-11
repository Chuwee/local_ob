package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class UpdateRenewalResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UpdateRenewalResponseItem> items;

    public List<UpdateRenewalResponseItem> getItems() {
        return items;
    }

    public void setItems(List<UpdateRenewalResponseItem> items) {
        this.items = items;
    }
}
