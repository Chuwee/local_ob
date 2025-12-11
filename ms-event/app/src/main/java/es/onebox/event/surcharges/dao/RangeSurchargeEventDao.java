package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO;

@Repository
public class RangeSurchargeEventDao extends DaoImpl<CpanelRangoRecargoEventoRecord, Integer> {

    protected RangeSurchargeEventDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO);
    }

    public List<CpanelRangoRecord> getByEventId(Integer eventId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_EVENTO)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_EVENTO.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_EVENTO.IDEVENTO.eq(eventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }

    public int deleteByEventId(Integer eventId) {
        return this.dsl.deleteFrom(CPANEL_RANGO_RECARGO_EVENTO)
                .where(CPANEL_RANGO_RECARGO_EVENTO.IDEVENTO.eq(eventId))
                .execute();
    }
}
