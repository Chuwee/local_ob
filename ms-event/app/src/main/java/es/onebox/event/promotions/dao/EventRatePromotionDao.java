package es.onebox.event.promotions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPromocionEventoTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PROMOCION_EVENTO_TARIFA;

@Repository
public class EventRatePromotionDao extends DaoImpl<CpanelPromocionEventoTarifaRecord, Integer> {

    protected EventRatePromotionDao() {
        super(CPANEL_PROMOCION_EVENTO_TARIFA);
    }

    public Long countByRateId(Integer rateId) {
        return dsl.selectCount()
                .from(CPANEL_PROMOCION_EVENTO_TARIFA)
                .where(CPANEL_PROMOCION_EVENTO_TARIFA.IDTARIFA.eq(rateId))
                .fetchOne(0, Long.class);
    }
}
