package es.onebox.mgmt.salerequests.enums;

public enum SaleRequestFilter {

    CHANNELS("channels"),
    CHANNELS_ENTITIES("channel-entities"),
    EVENT_ENTITIES("event-entities");

    private String filterType;

    SaleRequestFilter(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterType() {
        return filterType;
    }
}
