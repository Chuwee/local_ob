package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PostBookingQuestionsChannels implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Integer> ids;
    private EventChannelsPBQType type;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public EventChannelsPBQType getType() {
        return type;
    }

    public void setType(EventChannelsPBQType type) {
        this.type = type;
    }
}
