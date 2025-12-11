package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoPromocionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_PROMOCION;

@Repository
public class RangeSurchargeEventPromotionDao extends DaoImpl<CpanelRangoRecargoEventoPromocionRecord, Integer> {

    protected RangeSurchargeEventPromotionDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION);
    }

    public List<CpanelRangoRecord> getByEventId(int eventoId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_EVENTO_PROMOCION.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION.IDEVENTO.eq(eventoId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }

    public int deleteByEventId(int eventId) {
        return this.dsl.deleteFrom(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION)
                .where(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION.IDEVENTO.eq(eventId))
                .execute();
    }
}