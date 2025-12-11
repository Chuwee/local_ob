package es.onebox.event.catalog.dao.venue;

import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CUPOS_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

@Repository
public class VenueConfigurationDao extends DaoImpl<CpanelConfigRecintoRecord, Integer> {

    public VenueConfigurationDao() {
        super(CPANEL_CONFIG_RECINTO);
    }

    public List<CpanelConfigRecintoRecord> getByIds(List<Integer> ids) {
        return dsl.select(CPANEL_CONFIG_RECINTO.fields())
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.in(ids))
                .fetchInto(CpanelConfigRecintoRecord.class);
    }

    public List<CpanelZonaPreciosConfigRecord> getPriceZonesByVenueConfigIds(List<Integer> ids) {
        return dsl.select(CPANEL_ZONA_PRECIOS_CONFIG.fields())
                .from(CPANEL_ZONA_PRECIOS_CONFIG)
                .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.in(ids))
                .fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    public List<CpanelZonaPreciosConfigRecord> getPriceZonesByVenueConfigId(Integer id) {
        return dsl.select(CPANEL_ZONA_PRECIOS_CONFIG.fields())
                .from(CPANEL_ZONA_PRECIOS_CONFIG)
                .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.in(Arrays.asList(id)))
                .fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    public List<CpanelCuposConfigRecord> getQuotasByConfigId(Integer id) {
        return dsl.select(CPANEL_CUPOS_CONFIG.fields())
                .from(CPANEL_CUPOS_CONFIG)
                .where(CPANEL_CUPOS_CONFIG.IDCONFIGURACION.in(Arrays.asList(id)))
                .fetchInto(CpanelCuposConfigRecord.class);
    }

    public Integer getVenueIdByVenueConfigId(Integer venueConfigId) {
        return dsl
                .select(CPANEL_CONFIG_RECINTO.IDRECINTO)
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(venueConfigId))
                .and(CPANEL_CONFIG_RECINTO.ESTADO.eq(VenueConfigurationStatus.ACTIVE.getStatus()))
                .fetchOne(CPANEL_CONFIG_RECINTO.IDRECINTO);
    }
}
