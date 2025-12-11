package es.onebox.mgmt.datasources.ms.channel.salerequests.enums;

public enum CommunicationChannelElementType {
    SEAT_SELECTION_DISCLAIMER(CommunicationElementType.TEXT);

    private CommunicationElementType type;

    CommunicationChannelElementType(CommunicationElementType type) {
        this.type = type;
    }

    public CommunicationElementType getType() {
        return type;
    }
}
