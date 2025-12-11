package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelTagElementosComRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class TagCommunicationElementDao extends DaoImpl<CpanelTagElementosComRecord, Integer> {

    protected TagCommunicationElementDao() {
        super(Tables.CPANEL_TAG_ELEMENTOS_COM);
    }

}
