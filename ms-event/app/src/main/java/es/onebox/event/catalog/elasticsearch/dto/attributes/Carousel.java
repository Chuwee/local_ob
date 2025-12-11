package es.onebox.event.catalog.elasticsearch.dto.attributes;

import java.io.Serializable;
import java.util.List;

public class Carousel implements Serializable {

    public static final Integer MAX_SIZE = 5;

    private List<Integer> eventsOrder;

    public List<Integer> getEventsOrder() {
        return eventsOrder;
    }

    public void setEventsOrder(List<Integer> eventsOrder) {
        this.eventsOrder = eventsOrder;
    }
}
