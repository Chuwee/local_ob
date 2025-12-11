package es.onebox.event.externalevents.dao;

import es.onebox.event.externalevents.controller.dto.ExternalEventTypeDTO;
import es.onebox.event.externalevents.dto.EventType;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EXTERNAL_EVENT;

@Repository
public class ExternalEventsDao extends DaoImpl<CpanelExternalEventRecord, Long> {

    public List<CpanelExternalEventRecord> getExternalEvents(List<Integer> entityIds, ExternalEventTypeDTO eventType) {
        return dsl.select(CPANEL_EXTERNAL_EVENT.fields())
                .from(CPANEL_EXTERNAL_EVENT)
                .where(buildWhereCondition(entityIds, eventType))
                .fetch().into(CpanelExternalEventRecord.class);
    }

    protected ExternalEventsDao() {
        super(CPANEL_EXTERNAL_EVENT);
    }

    private Condition buildWhereCondition(List<Integer> entityIds, ExternalEventTypeDTO eventType) {
        Condition conditions = DSL.trueCondition();

        if (entityIds != null && !entityIds.isEmpty()) {
            conditions = conditions.and(CPANEL_EXTERNAL_EVENT.ENTITYID.in(entityIds));
        }

        if (eventType != null) {
            conditions = conditions.and(CPANEL_EXTERNAL_EVENT.EVENTTYPE.eq(EventType.valueOf(eventType.name()).getId().byteValue()));
        }

        return conditions;
    }
}
