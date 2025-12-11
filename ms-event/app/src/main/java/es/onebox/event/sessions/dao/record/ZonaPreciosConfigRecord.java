package es.onebox.event.sessions.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class ZonaPreciosConfigRecord extends CpanelZonaPreciosConfigRecord {

    private Long gateId;

    public Long getGateId() {
        return gateId;
    }

    public void setGateId(Long gateId) {
        this.gateId = gateId;
    }

}
