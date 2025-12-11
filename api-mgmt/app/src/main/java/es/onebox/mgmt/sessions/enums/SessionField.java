package es.onebox.mgmt.sessions.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum SessionField implements FiltrableField {
    // In the future we should split name and dtoName in different layers, to disallow RELEASEDATE at API level

    ID("id", "id"),
    NAME("name", "name"),
    STATUS("status", "status"),
    TYPE("type", "type"),
    STATUS_FLAGS("status.flags", "status.flags"),
    GENERATION_STATUS("generation_status", "generationStatus"),
    PUBLICATION_CANCELLED_REASON("publication_cancelled_reason","publicationCancelledReason"),
    RELEASE_ENABLED("release_enabled", "releaseEnabled"),
    STARTDATE("start_date", "date.start"),
    ENTITYID("entity.id", "entityId"),
    CAPACITY("capacity", "capacity"),
    VENUE_TEMPLATE_ID("venue_template.id", "venueTemplate.id"),
    VENUE_TEMPLATE_NAME("venue_template.name", "venueTemplate.name"),
    VENUE_TEMPLATE_TEMPLATE_TYPE("venue_template.type", "venueTemplate.templateType"),
    VENUE_TEMPLATE_VENUE_ID("venue_template.venue.id", "venueTemplate.venue.id"),
    VENUE_TEMPLATE_VENUE_NAME("venue_template.venue.name", "venueTemplate.venue.name"),
    VENUE_TEMPLATE_TIMEZONE("venue_template.venue.timezone", "venueTemplate.venue.timezone"),
    // If RELEASEDATE ever gets to be available at API level, don't forget to add the documentation for all affected endpoints
    RELEASEDATE("settings.release.date", "date.release"),
    SMART_BOOKING_SETTING_STATUS("settings.smart_booking.status", "smartBooking.status"),
    SMART_BOOKING_SETTING_RELATED_SESSION("settings.smart_booking.related_session", "smartBooking.relatedSession"),
    SETTINGS_ENABLE_ORPHAN_SEATS("settings.enable_orphan_seats", "settings.enableOrphanSeats");


    private static final long serialVersionUID = 1L;

    private final String name;
    private final String dtoName;

    SessionField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getName() {
        return this.name;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static SessionField byName(String name) {
        return Stream.of(SessionField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
