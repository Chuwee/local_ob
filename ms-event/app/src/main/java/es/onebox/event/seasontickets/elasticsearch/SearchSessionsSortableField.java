package es.onebox.event.seasontickets.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortableField;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;

public enum SearchSessionsSortableField implements SortableField {
    @JsonProperty("session_starting_date")
    SESSION_STARTING_DATE(SessionElasticProperty.BEGIN_DATE.getProperty()),

    @JsonProperty("event_name")
    EVENT_NAME(SessionElasticProperty.EVENT_NAME.getProperty()),

    @JsonProperty("session_name")
    SESSION_NAME(SessionElasticProperty.NAME.getProperty()),

    @JsonProperty("assignation_status")
    ASSIGNATION_STATUS(SessionElasticProperty.RELATED_SEASON_SESSION_IDS.getProperty());

    private String name;

    SearchSessionsSortableField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
