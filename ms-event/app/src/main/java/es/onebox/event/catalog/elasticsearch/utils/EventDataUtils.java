package es.onebox.event.catalog.elasticsearch.utils;

public class EventDataUtils {

    private EventDataUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static final String EVENT_INDEX = "eventdata";

    public static final String KEY_EVENT = "event";
    public static final String KEY_CHANNEL_EVENT = "channelEvent";
    public static final String KEY_CHANNEL_EVENT_AGENCY = "channelEventAgency";
    public static final String KEY_SESSION = "session";
    public static final String KEY_CHANNEL_SESSION = "channelSession";
    public static final String KEY_CHANNEL_SESSION_AGENCY = "channelSessionAgency";
    public static final String KEY_SEASON_TICKET = "seasonTicket";

    private static final String GENERIC_SEPARATOR = "|";

    public static String getEventKey(Long eventId) {
        if (eventId != null) {
            return KEY_EVENT + GENERIC_SEPARATOR + eventId;
        }
        return null;
    }

    public static String getSessionKey(Long sessionId) {
        return KEY_SESSION + GENERIC_SEPARATOR + sessionId;
    }

    public static String getChannelEventKey(Long channelId, Long eventId) {
        return KEY_CHANNEL_EVENT + GENERIC_SEPARATOR + channelId + GENERIC_SEPARATOR + eventId;
    }

    public static String getChannelEventAgencyKey(Long channelId, Long eventId, Long agencyId) {
        return KEY_CHANNEL_EVENT_AGENCY + GENERIC_SEPARATOR + channelId + GENERIC_SEPARATOR + eventId + GENERIC_SEPARATOR + agencyId;
    }


    public static String getChannelSessionKey(Long channelId, Long sessionId) {
        return KEY_CHANNEL_SESSION + GENERIC_SEPARATOR + channelId + GENERIC_SEPARATOR + sessionId;
    }

    public static String getChannelSessionAgencyKey(Long channelId, Long sessionId, Long agencyId) {
        return KEY_CHANNEL_SESSION_AGENCY + GENERIC_SEPARATOR + channelId + GENERIC_SEPARATOR + sessionId + GENERIC_SEPARATOR + agencyId;
    }

    public static String getSeasonTicketKey(Long seasonTicketId) {
        return KEY_SEASON_TICKET + GENERIC_SEPARATOR + seasonTicketId;
    }
}
