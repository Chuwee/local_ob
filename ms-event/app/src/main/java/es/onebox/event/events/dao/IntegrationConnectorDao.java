package es.onebox.event.events.dao;

import org.springframework.stereotype.Repository;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.IntegrationConnectorRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class IntegrationConnectorDao extends DaoImpl<IntegrationConnectorRecord, Integer> {

    protected IntegrationConnectorDao() {
        super(Tables.INTEGRATION_CONNECTOR);
    }

    public IntegrationConnectorRecord getByName(String name) {
        var result = dsl.select().from(Tables.INTEGRATION_CONNECTOR).where(Tables.INTEGRATION_CONNECTOR.SERVICENAME.eq(name)).fetchOne();
        if (result != null) {
            return result.into(IntegrationConnectorRecord.class);
        }
        return null;
    }

}
