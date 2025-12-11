package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;
import java.util.List;

public class UpdateSessionsData implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;

    private Session value;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Session getValue() {
        return value;
    }

    public void setValue(Session value) {
        this.value = value;
    }
}
