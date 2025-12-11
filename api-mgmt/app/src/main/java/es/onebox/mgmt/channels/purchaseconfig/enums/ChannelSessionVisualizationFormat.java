package es.onebox.mgmt.channels.purchaseconfig.enums;

public enum ChannelSessionVisualizationFormat {
    LIST(Boolean.TRUE),
    CALENDAR(Boolean.FALSE);

    private final Boolean isList;

    ChannelSessionVisualizationFormat(Boolean isList) {
        this.isList = isList;
    }

    public Boolean isList() {
        return isList;
    }

    public static ChannelSessionVisualizationFormat fromBoolean(boolean b) {
        return b ? LIST : CALENDAR;
    }
}
