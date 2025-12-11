package es.onebox.mgmt.common.ticketcontents;

import java.io.Serializable;

public enum TicketContentTextType implements TicketContentItemType, Serializable {
    TITLE,
    SUBTITLE,
    TERMS,
    ADDITIONAL_DATA;

    @Override
    public String getTag() {
        return name();
    }
}
