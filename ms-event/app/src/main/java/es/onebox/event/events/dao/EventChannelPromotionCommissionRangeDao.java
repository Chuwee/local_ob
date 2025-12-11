package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoComservEventoCanalPromocionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelRangoComservEventoCanalPromocion.CPANEL_RANGO_COMSERV_EVENTO_CANAL_PROMOCION;

@Repository
public class EventChannelPromotionCommissionRangeDao extends DaoImpl<CpanelRangoComservEventoCanalPromocionRecord, CpanelRangoComservEventoCanalPromocionRecord> {

    protected EventChannelPromotionCommissionRangeDao() {
        super(CPANEL_RANGO_COMSERV_EVENTO_CANAL_PROMOCION);
    }

    public void deleteByEventChannelId(Integer eventChannelId) {
        dsl.delete(CPANEL_RANGO_COMSERV_EVENTO_CANAL_PROMOCION)
                .where(CPANEL_RANGO_COMSERV_EVENTO_CANAL_PROMOCION.IDEVENTOCANAL.eq(eventChannelId))
                .execute();
    }
}
