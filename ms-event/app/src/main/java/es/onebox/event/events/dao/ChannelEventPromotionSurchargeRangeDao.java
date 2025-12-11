package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoPromocionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.tables.CpanelRangoRecargoCanalEventoPromocion.CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION;

@Repository
public class ChannelEventPromotionSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoCanalEventoPromocionRecord, CpanelRangoRecargoCanalEventoPromocionRecord> {

    protected ChannelEventPromotionSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION);
    }

    public void deleteByChannelEventId(Integer channelEventId) {
        dsl.delete(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION)
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION.IDCANALEVENTO.eq(channelEventId))
                .execute();
    }

    public List<CpanelRangoRecord> getByChannelEventId(Integer channelEventId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION).on(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION.IDRANGO.eq(CPANEL_RANGO.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION.IDCANALEVENTO.eq(channelEventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }
}
