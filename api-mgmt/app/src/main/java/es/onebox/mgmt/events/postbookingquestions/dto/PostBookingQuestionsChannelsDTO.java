package es.onebox.mgmt.events.postbookingquestions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.postbookingquestions.enums.EventChannelsPBQType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PostBookingQuestionsChannelsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Integer> ids;
    @JsonProperty("selection_type")
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
