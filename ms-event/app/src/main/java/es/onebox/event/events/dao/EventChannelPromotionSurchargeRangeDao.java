package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCanalPromocionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelRangoRecargoEventoCanalPromocion.CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION;

@Repository
public class EventChannelPromotionSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoCanalPromocionRecord, CpanelRangoRecargoEventoCanalPromocionRecord> {

    protected EventChannelPromotionSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION);
    }

    public void deleteByEventChannelId(Integer eventChannelId) {
        dsl.delete(CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION)
                .where(CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION.IDEVENTOCANAL.eq(eventChannelId))
                .execute();
    }
}
