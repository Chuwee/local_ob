package es.onebox.external.dto;

import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ExternalSyncRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 100)
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
