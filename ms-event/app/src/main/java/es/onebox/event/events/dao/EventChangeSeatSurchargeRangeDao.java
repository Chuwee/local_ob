package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCambioLocalidadRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD;

@Repository
public class EventChangeSeatSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoCambioLocalidadRecord, Integer> {

    protected EventChangeSeatSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_CAMBIO_LOCALIDAD);
    }

}
