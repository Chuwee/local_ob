package es.onebox.event.packs.dao.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;

public class PackRateRecord extends CpanelTarifaPackRecord {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
