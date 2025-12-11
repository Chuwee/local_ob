package es.onebox.event.catalog.elasticsearch.properties;

public enum SessionElasticProperty implements ElasticProperty {

    ID_("id"),
    ID("session.sessionId"),
    NAME("session.sessionName"),
    NAME_FTS("session.sessionName.fts"),
    EVENT_ID("session.eventId"),
    EVENT_NAME("session.eventName"),
    EVENT_NAME_FTS("session.eventName.fts"),
    PUBLISH_SESSION_DATE("session.publishSessionDate"),
    STATUS("session.sessionStatus"),
    BEGIN_DATE("session.beginSessionDate"),
    PUBLISHED("session.published"),
    VENUE_ID("session.venueId"),
    ENTITY_ID("session.entityId"),
    EVENT_TYPE("session.eventType"),
    EVENT_STATUS("session.eventStatus"),
    IS_SEASON_PACK_SESSION("session.seasonPackSession"),
    RELATED_SEASON_SESSION_IDS("session.relatedSeasonSessionIds");

    private String property;

    SessionElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
