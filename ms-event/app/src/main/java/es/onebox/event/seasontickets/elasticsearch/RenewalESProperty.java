package es.onebox.event.seasontickets.elasticsearch;

import es.onebox.event.catalog.elasticsearch.properties.ElasticProperty;

public enum RenewalESProperty implements ElasticProperty {
    NAME("name"),
    NAME_FTS("name.fts"),
    SURNAME("surname"),
    BIRTHDAY("birthday"),
    SURNAME_FTS("surname.fts"),
    MEMBER_ID("memberId"),
    MEMBER_ID_FTS("memberId.fts"),
    EMAIL("email"),
    EMAIL_FTS("email.fts"),
    ADDRESS("address"),
    ADDRESS_FTS("address.fts"),
    POSTAL_CODE("postalCode"),
    POSTAL_CODE_FTS("postalCode.fts"),
    PHONE_NUMBER("phoneNumber"),
    PHONE_NUMBER_FTS("phoneNumber.fts"),
    SEASONT_TICKET_NAME("seasonTicketName"),
    SEASONT_TICKET_NAME_FTS("seasonTicketName.fts"),
    MANAGER("manager"),
    MANAGER_FTS("manager.fts"),
    MAPPING_STATUS("mappingStatus"),
    RENEWAL_STATUS("renewalStatus"),
    SEASON_TICKET_ID("seasonTicketId"),
    ENTITY_ID("entityId"),
    USER_ID("userId"),
    ACTUAL_RATE_ID("actualRateId"),
    AUTO_RENEWAL("autoRenewal"),
    RENEWAL_SUBSTATUS("renewalSubstatus");

    private final String property;

    RenewalESProperty(String property) {
        this.property = property;
    }

    @Override
    public String getProperty() {
        return property;
    }
}
