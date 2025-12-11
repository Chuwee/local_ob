package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoInvRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_INV;

@Repository
public class EventInvSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoInvRecord, Integer> {

    protected EventInvSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_INV);
    }

}
