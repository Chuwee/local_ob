package es.onebox.common.datasources.distribution.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class InvitationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5867580087208674193L;

    private List<Long> items;

    public List<Long> getItems() {
        return items;
    }

    public void setItems(List<Long> items) {
        this.items = items;
    }
}
