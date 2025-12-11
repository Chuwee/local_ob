package es.onebox.event.catalog.elasticsearch.context;

public enum EventIndexationType {
    FULL,
    PARTIAL_BASIC,
    PARTIAL_COM_ELEMENTS,
    SEASON_TICKET;

    public static EventIndexationType fromValue(String name) {
        if (name != null) {
            try {
                return EventIndexationType.valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return EventIndexationType.FULL;
    }
}
