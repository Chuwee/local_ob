package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.event.events.request.TourEventsFilter;
import es.onebox.event.events.request.ToursFilter;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.sorting.EventField;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEntidadAdminEntidades;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelGira;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TourDao extends DaoImpl<CpanelGiraRecord, Integer> {

    private static final CpanelGira TOUR = CpanelGira.CPANEL_GIRA.as("tour");
    private static final CpanelEntidad TOUR_ENTITY = CpanelEntidad.CPANEL_ENTIDAD.as(EventField.Alias.ENTIDAD);
    private static final CpanelEntidadAdminEntidades ENTITY_ADMIN = Tables.CPANEL_ENTIDAD_ADMIN_ENTIDADES.as("entityAdmin");
    private static final CpanelEvento TOUR_EVENT = CpanelEvento.CPANEL_EVENTO.as(EventField.Alias.EVENTO);

    private static final Field<String> JOIN_ENTITY_NAME = TOUR_ENTITY.NOMBRE.as("entityName");
    private static final Field<Integer> JOIN_OPERATOR_ID = TOUR_ENTITY.IDOPERADORA.as("operatorId");
    private static final Field<Integer> JOIN_EVENT_ID = TOUR_EVENT.IDEVENTO.as("eventId");
    private static final Field<String> JOIN_EVENT_NAME = TOUR_EVENT.NOMBRE.as("eventName");
    private static final Field<Integer> JOIN_EVENT_STATE = TOUR_EVENT.ESTADO.as("eventState");
    private static final Field<Byte> JOIN_EVENT_ARCHIVED = TOUR_EVENT.ARCHIVADO.as("eventArchived");
    private static final Field<Integer> JOIN_EVENT_CAPACITY = TOUR_EVENT.AFORO.as("eventCapacity");
    private static final Field<Timestamp> JOIN_EVENT_START_DATE = TOUR_EVENT.FECHAINICIO.as("eventStartDate");

    private static final Field<?>[] fields = ArrayUtils.addAll(TOUR.fields(), JOIN_ENTITY_NAME, JOIN_OPERATOR_ID, JOIN_EVENT_ID,
            JOIN_EVENT_NAME, JOIN_EVENT_STATE, JOIN_EVENT_ARCHIVED, JOIN_EVENT_CAPACITY, JOIN_EVENT_START_DATE);

    protected TourDao() {
        super(Tables.CPANEL_GIRA);
    }

    public Map.Entry<TourRecord, List<EventRecord>> findWithEvents(Integer tourId, TourEventsFilter filter) {

        SelectJoinStep<Record> query = dsl.select(fields)
                .from(TOUR)
                .innerJoin(TOUR_ENTITY).on(TOUR.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD))
                .leftJoin(TOUR_EVENT).on(TOUR.IDGIRA.eq(TOUR_EVENT.IDGIRA).and(TOUR_EVENT.ESTADO.ne(EventStatus.DELETED.getId())));

        query.where(TOUR.IDGIRA.eq(tourId));

        if (filter.getSort() != null) {
            query.orderBy(SortUtils.buildSort(filter.getSort(), EventField::byName));
        }
        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        Map<TourRecord, List<EventRecord>> tourEvents = query.fetchGroups(this::buildTourRecord, this::buildTourEvent);
        Map.Entry<TourRecord, List<EventRecord>> entry = null;
        if (tourEvents != null && tourEvents.size() == 1) {
            entry = tourEvents.entrySet().iterator().next();
            if (entry.getValue().size() == 1 && entry.getValue().get(0) == null) {
                entry.setValue(new ArrayList<>());
            }
        }
        return entry;
    }

    public List<Long> findTourEvents(Integer tourId) {
        return dsl.select(TOUR_EVENT.IDEVENTO)
                .from(TOUR_EVENT)
                .where(TOUR_EVENT.IDGIRA.eq(tourId))
                .fetchInto(Long.class);
    }

    public TourRecord find(Integer tourId) {
        Field<?>[] fields = ArrayUtils.addAll(TOUR.fields(), JOIN_ENTITY_NAME, JOIN_OPERATOR_ID);
        return dsl.select(fields).
                from(TOUR).
                innerJoin(TOUR_ENTITY).on(TOUR.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD)).
                where(TOUR.IDGIRA.eq(tourId)).
                fetchOne(this::buildTourRecord);
    }

    public List<TourRecord> find(ToursFilter filter) {
        Condition where = buildWhere(filter);
        return dsl.select(ArrayUtils.addAll(TOUR.fields(), JOIN_ENTITY_NAME, JOIN_OPERATOR_ID))
                .from(TOUR)
                .innerJoin(TOUR_ENTITY).on(TOUR.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD))
                .leftJoin(ENTITY_ADMIN).on(ENTITY_ADMIN.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD))
                .where(where)
                .fetch(this::buildTourRecord);
    }

    public Long countByFilter(ToursFilter filter) {
        Condition where = buildWhere(filter);
        return dsl.selectCount()
                .from(TOUR)
                .innerJoin(TOUR_ENTITY).on(TOUR.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD))
                .leftJoin(ENTITY_ADMIN).on(ENTITY_ADMIN.IDENTIDAD.eq(TOUR_ENTITY.IDENTIDAD))
                .where(where)
                .fetchOne()
                .into(Long.class);
    }

    public Long countByNameAndEntity(String name, Integer entityId) {
        return dsl.selectCount()
                .from(TOUR)
                .where(TOUR.NOMBRE.eq(name).and(
                        TOUR.IDENTIDAD.eq(entityId)))
                .and(TOUR.ESTADO.ne(TourStatus.DELETED.getId()))
                .fetchOne()
                .into(Long.class);
    }

    private TourRecord buildTourRecord(Record r) {
        TourRecord tour = r.into(TourRecord.class);
        tour.setEntityName(r.getValue(JOIN_ENTITY_NAME));
        tour.setOperatorId(r.getValue(JOIN_OPERATOR_ID));
        return tour;
    }

    private EventRecord buildTourEvent(Record r) {
        if (r.get(JOIN_EVENT_ID) == null) {
            return null;
        }
        EventRecord event = r.into(EventRecord.class);
        event.setIdevento(r.get(JOIN_EVENT_ID));
        event.setNombre(r.get(JOIN_EVENT_NAME));
        event.setArchivado(r.get(JOIN_EVENT_ARCHIVED));
        event.setEstado(r.get(JOIN_EVENT_STATE));
        event.setFechainicio(r.get(JOIN_EVENT_START_DATE));
        event.setAforo(r.get(JOIN_EVENT_CAPACITY));
        return event;
    }

    private Condition buildWhere(ToursFilter filter) {
        Condition where = TOUR.ESTADO.notEqual(TourStatus.DELETED.getId()).and(
                TOUR_ENTITY.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        if (filter.getEntityId() != null) {
            where = where.and(TOUR.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }
        if (filter.getStatus() != null) {
            where = where.and(TOUR.ESTADO.eq(filter.getStatus().getId()));
        }
        if (filter.getEntityAdminId() != null) {
            where = where.and(ENTITY_ADMIN.IDENTIDADADMIN.eq(filter.getEntityAdminId().intValue()));
        }
        return where;
    }
}
