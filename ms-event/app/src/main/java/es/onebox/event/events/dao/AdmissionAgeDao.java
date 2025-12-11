package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelCalificacionEdadRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class AdmissionAgeDao extends DaoImpl<CpanelCalificacionEdadRecord, String> {

    protected AdmissionAgeDao() {
        super(Tables.CPANEL_CALIFICACION_EDAD);
    }

}
