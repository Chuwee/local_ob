package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelPaisRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CountryDao extends DaoImpl<CpanelPaisRecord, Integer> {
    protected CountryDao() {
        super(Tables.CPANEL_PAIS);
    }
}
