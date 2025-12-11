package es.onebox.mgmt.venues.enums;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

import java.util.stream.Stream;

public enum VenueTemplatesFilterOption {

    VENUE_ENTITY("venue-entities"), 
    VENUE_COUNTRY("venue-countries"), 
    VENUE_CITY("venue-cities");

    private final String key;

    VenueTemplatesFilterOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static void validateFilter(String key) {
        if (Stream.of(values()).noneMatch(it -> it.key.equals(key))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.FILTER_NOT_FOUND, key);
        }
    }
}
