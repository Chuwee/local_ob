package es.onebox.event.catalog.elasticsearch.dto.attributes;

import java.io.Serializable;
import java.util.List;

public class EventsCustomisedOrder implements Serializable {
    private Boolean useCustomOrder;
    private List<Integer> eventsOrder;

    public Boolean getUseCustomOrder() {
        return useCustomOrder;
    }

    public void setUseCustomOrder(Boolean useCustomOrder) {
        this.useCustomOrder = useCustomOrder;
    }

    public List<Integer> getEventsOrder() {
        return eventsOrder;
    }

    public void setEventsOrder(List<Integer> eventsOrder) {
        this.eventsOrder = eventsOrder;
    }
}
