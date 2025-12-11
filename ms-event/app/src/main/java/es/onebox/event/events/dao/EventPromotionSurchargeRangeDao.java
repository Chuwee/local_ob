package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoPromocionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_PROMOCION;

@Repository
public class EventPromotionSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoPromocionRecord, Integer> {

    protected EventPromotionSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_PROMOCION);
    }

}
