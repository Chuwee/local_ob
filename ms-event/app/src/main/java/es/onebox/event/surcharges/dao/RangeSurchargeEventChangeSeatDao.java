package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCambioLocalidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD;

@Repository
public class RangeSurchargeEventChangeSeatDao extends DaoImpl<CpanelRangoRecargoEventoCambioLocalidadRecord, Integer> {

    protected RangeSurchargeEventChangeSeatDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD);
    }

    public List<CpanelRangoRecord> getByEventId(int eventoId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD.IDEVENTO.eq(eventoId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetchInto(CpanelRangoRecord.class);
    }

    public int deleteByEventId(int eventId) {
        return this.dsl.deleteFrom(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD)
                .where(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD.IDEVENTO.eq(eventId))
                .execute();
    }
}