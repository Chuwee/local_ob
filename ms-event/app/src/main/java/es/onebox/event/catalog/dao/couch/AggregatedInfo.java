package es.onebox.event.catalog.dao.couch;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AggregatedInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1776709811122542016L;

    private List<Integer> templatesZonesIds;

    public List<Integer> getTemplatesZonesIds() {
        return templatesZonesIds;
    }

    public void setTemplatesZonesIds(List<Integer> templatesZonesIds) {
        this.templatesZonesIds = templatesZonesIds;
    }
}
