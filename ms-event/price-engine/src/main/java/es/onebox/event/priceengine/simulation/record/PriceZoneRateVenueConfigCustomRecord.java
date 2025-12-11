package es.onebox.event.priceengine.simulation.record;

import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class PriceZoneRateVenueConfigCustomRecord extends CpanelAsignacionZonaPreciosRecord {

    private static final long serialVersionUID = -5787408743188817261L;

    private CpanelTarifaRecord rate;
    private CpanelZonaPreciosConfigRecord priceZoneConfig;
    private CpanelConfigRecintoRecord venueConfig;

    public CpanelTarifaRecord getRate() {
        return rate;
    }

    public void setRate(CpanelTarifaRecord rate) {
        this.rate = rate;
    }

    public CpanelZonaPreciosConfigRecord getPriceZoneConfig() {
        return priceZoneConfig;
    }

    public void setPriceZoneConfig(CpanelZonaPreciosConfigRecord priceZoneConfig) {
        this.priceZoneConfig = priceZoneConfig;
    }

    public CpanelConfigRecintoRecord getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(CpanelConfigRecintoRecord venueConfig) {
        this.venueConfig = venueConfig;
    }
}
