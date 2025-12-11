package es.onebox.event.events.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class EventCommunicationElementDao extends DaoImpl<CpanelElementosComEventoRecord, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventCommunicationElementDao.class);

    protected EventCommunicationElementDao() {
        super(Tables.CPANEL_ELEMENTOS_COM_EVENTO);
    }

    public List<CpanelElementosComEventoRecord> getEventCommunicationElemenetsByTourId(Integer tourId) {
        return dsl.select()
                .from(Tables.CPANEL_ELEMENTOS_COM_EVENTO)
                .where(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDGIRA.eq(tourId))
                .fetch().into(CpanelElementosComEventoRecord.class);
    }

    public List<CpanelElementosComEventoRecord> getEventCommunicationElementsByEventId(Integer eventId) {
        return dsl.select()
                .from(Tables.CPANEL_ELEMENTOS_COM_EVENTO)
                .where(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDEVENTO.eq(eventId))
                .fetch().into(CpanelElementosComEventoRecord.class);
    }

    public List<CpanelElementosComEventoRecord> getEventCommunicationElementsBySessionId(Integer sessionId) {
        return dsl.select(
                Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDELEMENTO,
                Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDTAG,
                Tables.CPANEL_ELEMENTOS_COM_EVENTO.POSITION,
                Tables.CPANEL_ELEMENTOS_COM_EVENTO.VALOR,
                Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDIOMA
        ).from(Tables.CPANEL_ELEMENTOS_COM_EVENTO)
                .where(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDSESION.eq(sessionId))
                .fetch().into(CpanelElementosComEventoRecord.class);
    }

    public List<CpanelElementosComEventoRecord> getEventCommunicationElementsBySessionIds(List<Long> sessionId) {
        return dsl.select(
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDELEMENTO,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDTAG,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.POSITION,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.VALOR,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDIOMA,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDSESION,
                        Tables.CPANEL_ELEMENTOS_COM_EVENTO.ALTTEXT
                ).from(Tables.CPANEL_ELEMENTOS_COM_EVENTO)
                .where(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDSESION.in(sessionId))
                .fetch().into(CpanelElementosComEventoRecord.class);
    }

    public void bulkInsertRecords(List<CpanelElementosComEventoRecord> records) {
        dsl.batchInsert(records).execute();
    }

    public void bulkUpdateRecords(List<CpanelElementosComEventoRecord> records) {
        dsl.batchUpdate(records).execute();
    }

    public void bulkInsertRecordsWithRetries(List<CpanelElementosComEventoRecord> elements, int maxRetries){
        int attempt=0;
        boolean updated = false;
        do {
            try {
                bulkInsertRecords(elements);
                updated = true;
            } catch (PessimisticLockingFailureException e) {
                attempt++;
                if (attempt == maxRetries) {
                    throw e;
                }
            }
        }while(!updated);
    }

    public void bulkUpdateRecordsWithRetries(List<CpanelElementosComEventoRecord> elements, int maxRetries){
        int attempt=0;
        boolean updated = false;
        do {
            try {
                bulkUpdateRecords(elements);
                updated = true;
            } catch (PessimisticLockingFailureException e) {
                attempt++;
                if (attempt == maxRetries) {
                    throw e;
                }
            }
        }while(!updated);
    }

    public List<CpanelElementosComEventoRecord> findCommunicationElements(Long eventId, Set<Integer> sessionIds, Long tourId,
                                                                           EventCommunicationElementFilter filter) {
        return dsl.select()
                .from(Tables.CPANEL_ELEMENTOS_COM_EVENTO)
                .where(buildWhere(eventId, sessionIds, tourId, filter))
                .orderBy(Tables.CPANEL_ELEMENTOS_COM_EVENTO.POSITION)
                .fetch().into(CpanelElementosComEventoRecord.class);
    }

    private Condition buildWhere(Long eventId, Set<Integer> sessionIds, Long tourId, EventCommunicationElementFilter filter) {
        Condition where = DSL.trueCondition();
        if (eventId != null) {
            where = where.and(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDEVENTO.eq(eventId.intValue()));
        }
        if(CollectionUtils.isNotEmpty(sessionIds)){
            where = where.and(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDSESION.in(sessionIds));
        }
        if(tourId != null){
            where = where.and(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDGIRA.eq(tourId.intValue()));
        }
        if (filter == null) {
            return where;
        }
        if (filter.getTags() != null) {
            where = where.and(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDTAG.in(filter.getTags()
                    .stream()
                    .map(EventTagType::getId)
                    .collect(Collectors.toList())));
        }
        if (filter.getLanguageId() != null) {
            where = where.and(Tables.CPANEL_ELEMENTOS_COM_EVENTO.IDIOMA.eq(filter.getLanguageId()));
        }
        return where;
    }
}
