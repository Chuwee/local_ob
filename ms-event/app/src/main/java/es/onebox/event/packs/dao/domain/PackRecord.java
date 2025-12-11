package es.onebox.event.packs.dao.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;

public class PackRecord extends CpanelPackRecord {

    private CpanelEntidadRecord entity;

    public CpanelEntidadRecord getEntity() {
        return entity;
    }

    public void setEntity(CpanelEntidadRecord entity) {
        this.entity = entity;
    }
}
