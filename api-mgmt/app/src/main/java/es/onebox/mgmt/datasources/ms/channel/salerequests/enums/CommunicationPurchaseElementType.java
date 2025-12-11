package es.onebox.mgmt.datasources.ms.channel.salerequests.enums;

public enum CommunicationPurchaseElementType {
    PROMOTER_BANNER(CommunicationElementType.IMAGE),
    CHANNEL_HEADER_BANNER(CommunicationElementType.IMAGE),
    CHANNEL_BANNER(CommunicationElementType.IMAGE),
    CHANNEL_HEADER_LINK(CommunicationElementType.LINK),
    CHANNEL_LINK(CommunicationElementType.LINK),
    DISCLAIMER(CommunicationElementType.TEXT);

    private CommunicationElementType type;

    CommunicationPurchaseElementType(CommunicationElementType type) {
        this.type = type;
    }

    public CommunicationElementType getType() {
        return type;
    }
}
