package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private EventChannelsPBQType type;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public EventChannelsPBQType getType() {
        return type;
    }

    public void setType(EventChannelsPBQType type) {
        this.type = type;
    }
}
