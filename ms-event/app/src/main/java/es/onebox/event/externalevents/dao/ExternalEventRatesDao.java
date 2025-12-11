package es.onebox.event.externalevents.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRatesRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EXTERNAL_EVENT_RATES;

@Repository
public class ExternalEventRatesDao extends DaoImpl<CpanelExternalEventRatesRecord, String> {

    protected ExternalEventRatesDao() {
        super(CPANEL_EXTERNAL_EVENT_RATES);
    }

    public List<CpanelExternalEventRatesRecord> getRatesForExternalEvents(Collection<Integer> internalIds) {
        return dsl.select(CPANEL_EXTERNAL_EVENT_RATES.fields())
                .from(CPANEL_EXTERNAL_EVENT_RATES)
                .where(CPANEL_EXTERNAL_EVENT_RATES.EXTERNALEVENTINTERNALID.in(internalIds))
                .fetch().into(CpanelExternalEventRatesRecord.class);
    }
}
