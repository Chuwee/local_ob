package es.onebox.mgmt.datasources.ms.channel.salerequests.enums;

public enum CommunicationPaymentBenefitElementType {

    EVENT_BADGE(CommunicationElementType.TAG);

    private final CommunicationElementType type;

    CommunicationPaymentBenefitElementType(CommunicationElementType type) {
        this.type = type;
    }

    public CommunicationElementType getType() {
        return type;
    }
}
