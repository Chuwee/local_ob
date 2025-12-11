package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_ENTIDAD;

@Repository
public class RangeSurchargeEntityDao extends DaoImpl<CpanelRangoRecargoEntidadRecord, Integer> {

    protected RangeSurchargeEntityDao() {
        super(CPANEL_RANGO_RECARGO_ENTIDAD);
    }

    public List<CpanelRangoRecord> getByEntityId(Integer entityId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_ENTIDAD)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_ENTIDAD.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_ENTIDAD.IDENTIDAD.eq(entityId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }
    public List<CpanelRangoRecord> getByEntityIdAndCurrencyId(Integer entityId, Integer currencyId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_ENTIDAD)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_ENTIDAD.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_ENTIDAD.IDENTIDAD.eq(entityId))
                .and(CPANEL_RANGO.IDCURRENCY.eq(currencyId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }

}

