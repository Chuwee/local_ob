package es.onebox.event.events.postbookingquestions.dto;

import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PostBookingQuestionsChannelsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Integer> ids;
    private EventChannelsPBQType type;

    public PostBookingQuestionsChannelsDTO(){}

    public PostBookingQuestionsChannelsDTO(List<Integer> channelIds, EventChannelsPBQType selectionType) {
        this.ids = channelIds;
        this.type = selectionType;
    }

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
