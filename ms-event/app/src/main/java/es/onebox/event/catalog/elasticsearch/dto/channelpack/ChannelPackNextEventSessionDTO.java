package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import es.onebox.core.serializer.dto.common.IdDTO;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelPackNextEventSessionDTO extends IdDTO implements Serializable {

    private ZonedDateTime startDate;

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
