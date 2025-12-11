package es.onebox.event.catalog.amqp.catalogupdate;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;
import java.util.List;

public class CatalogUpdateMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    private List<Long> channelIds;

    private Long eventId;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
