package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class PriceTypeConfigCustomRecord extends CpanelZonaPreciosConfigRecord {

    private CpanelConfigRecintoRecord venueConfig;

    public CpanelConfigRecintoRecord getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(CpanelConfigRecintoRecord venueConfig) {
        this.venueConfig = venueConfig;
    }
}
