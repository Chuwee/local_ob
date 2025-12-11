package es.onebox.mgmt.events.eventchannel;


import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

import java.util.function.BiFunction;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

import static java.util.Objects.isNull;

public class EventChannelValidations {

    private EventChannelValidations() { throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static EventChannel GetEventChannelAndcheckPermissions(Long eventId, Long channelId, LongFunction<Event> getEvent,
                                                          BiFunction<Long, Long, EventChannel> getEventChannel, LongConsumer checkEntityAccessible) {
        if (isNull(eventId) || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        if (isNull(channelId) || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        Event event = getEvent.apply(eventId);
        if (isNull(event) || event.getStatus() == EventStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        checkEntityAccessible.accept(event.getEntityId());

        EventChannel eventChannel = getEventChannel.apply(eventId, channelId);
        if (isNull(eventChannel)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }
        return eventChannel;
    }
}
