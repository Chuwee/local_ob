package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTarifaGrupoTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA_GRUPO_TARIFA;

@Repository
public class RateGroupRateDao extends DaoImpl<CpanelTarifaGrupoTarifaRecord, Integer> {

    protected RateGroupRateDao() {
        super(CPANEL_TARIFA_GRUPO_TARIFA);
    }
}
