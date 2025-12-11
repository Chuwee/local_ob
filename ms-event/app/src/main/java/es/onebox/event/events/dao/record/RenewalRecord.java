package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelRenewalRecord;

import java.io.Serializable;

public class RenewalRecord extends CpanelRenewalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originSeasonTicketName;
    private Boolean isExternalEvent;
    private String originExternalEventName;

    public String getOriginSeasonTicketName() {
        return originSeasonTicketName;
    }

    public void setOriginSeasonTicketName(String originSeasonTicketName) {
        this.originSeasonTicketName = originSeasonTicketName;
    }

    public Boolean getIsExternalEvent() {
        return isExternalEvent;
    }

    public void setIsExternalEvent(Boolean isExternalEvent) {
        this.isExternalEvent = isExternalEvent;
    }

    public String getOriginExternalEventName() {
        return originExternalEventName;
    }

    public void setOriginExternalEventName(String originExternalEventName) {
        this.originExternalEventName = originExternalEventName;
    }
}
