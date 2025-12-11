package es.onebox.event.catalog.elasticsearch.properties;

public enum EventElasticProperty implements ElasticProperty {

    ID_("id"),
    SESSION_ENTITY_ID("session.entityId"),
    ROUTING("_routing"),
    ID("event.eventId"),
    STATUS("event.eventStatus"),
    TYPE("event.eventType"),
    BEGIN_EVENT_DATE("event.beginEventDate"),
    END_EVENT_DATE("event.endEventDate"),
    TAXONOMY_ID("event.taxonomyId"),
    TAXONOMY_CODE("event.taxonomyCode"),
    TAXONOMY_PARENT_ID("event.taxonomyParentId"),
    TAXONOMY_PARENT_CODE("event.taxonomyParentCode"),
    CUSTOM_TAXONOMY_ID("event.customTaxonomyId"),
    CUSTOM_TAXONOMY_CODE("event.customTaxonomyCode"),
    ENTITY_STATUS("event.entityStatus"),
    ENTITY_ID("event.entityId"),
    TOUR_ID("event.tourId"),
    ATTRIBUTE_VALUE_ID("event.eventAttributesValueId"),
    OPERATOR_STATUS("event.operatorStatus"),
    NAME("event.eventName"),
    NAME_FPS("event.eventName.fts"),
    PROMOTER_ID("event.promoter.id"),
    VENUE("event.venues"),
    VENUE_ID("event.venues.id"),
    COMMUNICATION_ELEMENT("event.communicationElements"),
    COMMUNICATION_ELEMENT_TAG_ID("event.communicationElements.tagId"),
    COMMUNICATION_ELEMENT_VALUE("event.communicationElements.value");


    private String property;

    EventElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
