package es.onebox.event.catalog.elasticsearch.properties;

public enum EventVenueElasticProperty implements ElasticProperty {

    VENUES_PATH("event.venues"),
    VENUES_COUNTRY_CODE("event.venues.countryCode"),
    VENUES_PROVINCE_CODE("event.venues.provinceCode"),
    VENUES_MUNICIPALITY("event.venues.municipality"),
    VENUES_ID("event.venues.id"),
    VENUES_NAME("event.venues.name");

    private String property;

    EventVenueElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
