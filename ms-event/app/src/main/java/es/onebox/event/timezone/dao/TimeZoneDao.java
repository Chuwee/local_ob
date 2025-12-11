package es.onebox.event.timezone.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIME_ZONE_GROUP;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

@Repository
public class TimeZoneDao extends DaoImpl<CpanelTimeZoneGroupRecord, Integer> {

    protected TimeZoneDao() {
        super(CPANEL_TIME_ZONE_GROUP);
    }

    public CpanelTimeZoneGroupRecord findByPriceZone(Integer priceZoneId) {
        return dsl.select(CPANEL_TIME_ZONE_GROUP.fields())
                .from(CPANEL_TIME_ZONE_GROUP)
                .join(CPANEL_RECINTO).on(CPANEL_TIME_ZONE_GROUP.ZONEID.eq(CPANEL_RECINTO.TIMEZONE))
                .join(CPANEL_CONFIG_RECINTO).on(CPANEL_RECINTO.IDRECINTO.eq(CPANEL_CONFIG_RECINTO.IDRECINTO))
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION))
                .and(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(priceZoneId))
                .fetchOneInto(CpanelTimeZoneGroupRecord.class);
    }

}
