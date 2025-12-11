package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPreciosGruposRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRECIOS_GRUPOS;

@Repository
public class GroupPricesDao extends DaoImpl<CpanelPreciosGruposRecord, Integer> {

    protected GroupPricesDao() {
        super(CPANEL_PRECIOS_GRUPOS);
    }

    public void deleteByRateId(Integer rateId) {
        dsl.delete(CPANEL_PRECIOS_GRUPOS)
                .where(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(rateId))
                .execute();
    }

}
