package es.onebox.mgmt.common.ticketcontents;

public enum TicketContentTextPassbookType implements TicketContentItemType {
    TITLE,
    ADDITIONAL_DATA_1,
    ADDITIONAL_DATA_2,
    ADDITIONAL_DATA_3;

    @Override
    public String getTag() {
        return name();
    }
}
