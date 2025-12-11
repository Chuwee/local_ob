package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO;

@Repository
public class EventSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoRecord, Integer> {

    protected EventSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO);
    }

}
