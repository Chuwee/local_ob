package es.onebox.event.venues.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

@Repository
public class VenueTemplateDao extends DaoImpl<CpanelConfigRecintoRecord, Integer> {

    protected VenueTemplateDao() {
        super(CPANEL_CONFIG_RECINTO);
    }

    public Map<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> getEventVenueTemplatesWithPriceZones(Integer eventId) {
        return dsl.select().
                from(Tables.CPANEL_CONFIG_RECINTO).
                innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION)).
                where(Tables.CPANEL_CONFIG_RECINTO.ESTADO.notEqual(0)).
                and(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId)).
                fetchGroups(
                        cr -> cr.into(CPANEL_CONFIG_RECINTO).into(CpanelConfigRecintoRecord.class),
                        zp -> zp.into(CPANEL_ZONA_PRECIOS_CONFIG).into(CpanelZonaPreciosConfigRecord.class)
                );
    }

    public List<CpanelZonaPreciosConfigRecord> getVenueTemplatePriceZones(Integer templateId) {
        return dsl.select().
                from(Tables.CPANEL_CONFIG_RECINTO).
                innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION)).
                where(Tables.CPANEL_CONFIG_RECINTO.ESTADO.notEqual(0)).
                and(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(templateId)).
                fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    public long countActiveVenueTemplates(Integer eventId) {
        return dsl.selectCount().from(Tables.CPANEL_CONFIG_RECINTO).
                where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId)).
                and(Tables.CPANEL_CONFIG_RECINTO.ESTADO.eq(1)).
                and(Tables.CPANEL_CONFIG_RECINTO.CURRENTSEQUENCE.isNotNull()).fetchOne(0, Long.class);
    }

    public long countActiveGraphicalVenueTemplates(Integer eventId) {
        return dsl.selectCount().from(Tables.CPANEL_CONFIG_RECINTO).
                where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId)).
                and(Tables.CPANEL_CONFIG_RECINTO.ESTADO.eq(1)).
                and(CPANEL_CONFIG_RECINTO.ESGRAFICA.eq((byte) 1)).
                and(Tables.CPANEL_CONFIG_RECINTO.CURRENTSEQUENCE.isNotNull()).fetchOne(0, Long.class);
    }

    public CpanelConfigRecintoRecord findByPriceTypeId(Integer priceTypeId) {
        return dsl.select(CPANEL_CONFIG_RECINTO.fields())
                .from(CPANEL_CONFIG_RECINTO)
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION)
                        .and(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(priceTypeId)))
                .fetchOneInto(CpanelConfigRecintoRecord.class);
    }


    public List<CpanelZonaPreciosConfigRecord> getActiveVenueTemplateIdsAndTemplateType(Long eventId, VenueTemplateType templateType) {
        return CommonUtils.ifNotNull(
         dsl.select(CPANEL_CONFIG_RECINTO.IDCONFIGURACION)
                .from(Tables.CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId.intValue()))
                .and(Tables.CPANEL_CONFIG_RECINTO.ESTADO.eq(1))
                .and(CPANEL_CONFIG_RECINTO.TIPOPLANTILLA.eq(templateType.getId()))
                .fetch(),
                result -> result.into(CpanelZonaPreciosConfigRecord.class));
    }
}
