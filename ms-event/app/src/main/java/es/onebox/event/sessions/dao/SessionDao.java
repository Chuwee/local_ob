package es.onebox.event.sessions.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.SessionsGroupDataRecord;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.enums.SessionGroupType;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.request.HourPeriod;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.request.SessionsGroupsSearchFilter;
import es.onebox.event.sorting.SessionField;
import es.onebox.jooq.cpanel.tables.records.CpanelPoliticasShardRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.RecordMapper;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static es.onebox.event.common.utils.ConverterUtils.intToByte;
import static es.onebox.event.common.utils.ConverterUtils.longToInt;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static org.apache.commons.lang3.ObjectUtils.anyNotNull;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.groupConcat;

@Repository
public class SessionDao extends SessionDaoSupport {

    protected SessionDao() {
        super(CPANEL_SESION);
    }

    private static final List<Integer> ALLOWED_SESSION_STATUS = Arrays.asList(SessionStatus.SCHEDULED.getId(), SessionStatus.READY.getId());

    private static final Integer NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES = 14;

    public List<CpanelSesionRecord> findFlatSessions(SessionSearchFilter filter) {
        Condition where = buildWhereClause(filter, true);
        return dsl.select(sesion.fields())
                .from(sesion)
                .where(where)
                .fetch().into(CpanelSesionRecord.class);
    }

    public List<SessionForCatalogRecord> findSessionsForCatalog(SessionSearchFilter filter) {
        Condition where = buildWhereClause(filter, true);
        return dsl.select(ArrayUtils.addAll(sesion.fields(), JOIN_CATALOG_FIELDS))
                .from(sesion)
                .leftJoin(ticketTax).on(ticketTax.IDIMPUESTO.eq(sesion.IDIMPUESTO))
                .leftJoin(chargesTax).on(chargesTax.IDIMPUESTO.eq(sesion.IDIMPUESTORECARGO))
                .where(where)
                .fetch().map(this::buildSessionsForCatalog);
    }

    public List<SessionRecord> findSessionsByEventId(Integer eventId) {
        SelectFieldOrAsterisk[] fields = {sesion.IDSESION, sesion.ESTADO, sesion.FECHAPUBLICACION, sesion.PUBLICADO, sesion.FECHAFINSESION,
                sesion.FECHAINICIORESERVA, sesion.FECHAFINRESERVA, sesion.ENVENTA, sesion.FECHAVENTA};

        return dsl.select(fields)
                .from(sesion)
                .where(sesion.IDEVENTO.eq(eventId).and(sesion.ESTADO.ne(SessionStatus.DELETED.getId())))
                .fetch()
                .into(SessionRecord.class);
    }

    public List<SessionRecord> findActiveSessionsByEventId(Integer eventId) {
        SelectFieldOrAsterisk[] fields = {sesion.IDSESION, sesion.NOMBRE, sesion.NUMMAXLOCALIDADESCOMPRA};

        return dsl.select(fields)
                .from(sesion)
                .where(sesion.IDEVENTO.eq(eventId)
                        .and(sesion.ESTADO.in(SessionStatus.READY.getId(), SessionStatus.IN_PROGRESS.getId(),
                                SessionStatus.PREVIEW.getId())))
                .fetch()
                .into(SessionRecord.class);
    }

    public List<SessionRecord> findSessions(SessionSearchFilter filter, Long internalLimit) {
        SelectFieldOrAsterisk[] fields = buildFields(filter);

        SelectJoinStep<Record> query = dsl.select(fields).from(sesion);
        buildJoinClauses(query, filter, fields);

        SessionSearchFilter timezoneSessionSearchFilter = null;
        if(filter.getEndDate() != null
                || filter.getRangeDateFrom() != null
                || filter.getRangeDateTo() != null
                || filter.getStartDate() != null
                || filter.getStartDateFrom() != null
                || filter.getStartDateTo() != null
                || (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty())) {
            timezoneSessionSearchFilter = new SessionSearchFilter();
            timezoneSessionSearchFilter = SerializationUtils.clone(filter);
        }

        query.where(buildWhereClause(filter, false));

        query.orderBy(SortUtils.buildSort(filter.getSort(), SessionField::byName));

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        if(filter.getEndDate() == null
                && filter.getRangeDateFrom() == null
                && filter.getRangeDateTo() == null
                && filter.getStartDate() == null
                && filter.getStartDateFrom() == null
                && filter.getStartDateTo() == null
                && (filter.getDaysOfWeek() == null || filter.getDaysOfWeek().isEmpty())
        ) {
            return query.fetch().map(r -> buildSessionRecord(r, fields));
        }

        if (internalLimit != null) {
            query.limit(internalLimit);
        }
        query.offset(0L);
        Table<Record> records = DSL.table(query);
        SelectJoinStep<Record> timezoneSubquery = dsl.select(records.fields()).from(records);
        if(timezoneSessionSearchFilter != null) {
            timezoneSubquery.where(buildTimezoneWhereClause(timezoneSessionSearchFilter));
        } else {
            timezoneSubquery.where(buildTimezoneWhereClause(filter));
        }

        if (filter.getLimit() != null) {
            timezoneSubquery.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            timezoneSubquery.offset(filter.getOffset().intValue());
        }
        return timezoneSubquery.fetch().map(r -> buildSessionRecord(r, fields));
    }

    private Condition buildTimezoneWhereClause(SessionSearchFilter filter) {
        Condition conditions = DSL.trueCondition();

        Field fieldStart = field("timezoneStartDate");
        Field fieldRealEnd = field("timezoneEndRealDate");
        Field fieldEnd = field("timezoneEndDate");
        Field weekDayNumber = field("weekDayNumber");
        conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, filter.getStartDate(), fieldStart, Operator.AND);
        conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, filter.getEndDate(), fieldEnd, Operator.AND, fieldStart);

        ZonedDateTime startDateFrom = filter.getStartDateFrom();
        ZonedDateTime startDateTo = filter.getStartDateTo();
        conditions = JooqUtils.addConditionGreaterOrEquals(conditions, fieldStart, startDateFrom);
        conditions = JooqUtils.addConditionLessOrEquals(conditions, fieldStart, startDateTo);

        if (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty()) {
            conditions = JooqUtils.addConditionIn(conditions, weekDayNumber, filter.getDaysOfWeek().stream().map(DayOfWeek::getValue).toList());
        }

        if (filter.getRangeDateFrom() != null && filter.getRangeDateTo() != null) {
            if (!filter.getRangeDateFrom().isAfter(filter.getRangeDateTo())) {
                conditions = conditions.and(fieldStart.le(Timestamp.from(filter.getRangeDateTo().toInstant())));
                conditions = conditions.and(fieldRealEnd.ge(Timestamp.from(filter.getRangeDateFrom().toInstant()))
                        .or(fieldEnd.ge(Timestamp.from(filter.getRangeDateFrom().toInstant()))));
            } else {
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "bad period definition", null);
            }
        }
        return conditions;
    }

    public List<SessionsGroupDataRecord> searchSessionsGroups(SessionsGroupsSearchFilter filter) {
        Field fieldStart = field("CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneStartDate");
        Field fieldRealDate = field("CONVERT_TZ(" + getQuotedField(SessionField.REAL_END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndRealDate");
        Field fieldEnd = field("CONVERT_TZ(" + getQuotedField(SessionField.END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndDate");
        Field weekDayNumber = field("WEEKDAY(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")) + 1").as("weekDayNumber");

        SelectJoinStep<Record6<Timestamp, Timestamp, Timestamp, Timestamp, Integer, Integer>> query = dsl.
                select(sesion.FECHAINICIOSESION.as("date"),
                        fieldStart.as("timezoneStartDate"),
                        fieldRealDate.as("timezoneEndRealDate"),
                        fieldEnd.as("timezoneEndDate"),
                        weekDayNumber.as("weekDayNumber"),
                        count().as("total")).from(sesion).
                innerJoin(evento).on(evento.IDEVENTO.eq(sesion.IDEVENTO)).
                innerJoin(entidad).on(entidad.IDENTIDAD.eq(evento.IDENTIDAD));

        query.innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO));
        query.innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION));
        query.innerJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO));
        query.innerJoin(recintoTZ).on(recintoTZ.ZONEID.eq(recinto.TIMEZONE));

        SessionSearchFilter timezoneSessionSearchFilter = SerializationUtils.clone(filter);
        query.where(buildWhereClause(filter, false));

        query.groupBy(fieldStart);

        query.orderBy(SortUtils.buildSort(filter.getSort(), SessionField::byName));

        List<SessionsGroupDataRecord> result = new ArrayList<>();

        List<Record6<Timestamp, Timestamp, Timestamp, Timestamp, Integer, Integer>> records = new ArrayList<>();
        Table<Record6<Timestamp, Timestamp, Timestamp, Timestamp, Integer, Integer>> records1 = DSL.table(query);
        @NotNull SelectJoinStep<? extends Record6<?, ?, ?, ?, ?, ?>> timezoneSubquery =
                dsl.select(records1.field("date"), records1.field("timezoneStartDate"), records1.field("timezoneEndRealDate"), records1.field("timezoneEndDate"),
                        records1.field("weekDayNumber"),
                        records1.field("total")).from(records1);
        if(timezoneSessionSearchFilter != null) {
            timezoneSubquery.where(buildTimezoneWhereClause(timezoneSessionSearchFilter));
        } else {
            timezoneSubquery.where(buildTimezoneWhereClause(filter));
        }

        records = (List<Record6<Timestamp, Timestamp, Timestamp, Timestamp, Integer, Integer>>) timezoneSubquery.fetch();

        Map<String, List<SessionsGroupDataRecord>> aggregatedData = new HashMap<>();
        for (Record record : records) {
            Timestamp sessionDate = record.get("timezoneStartDate", Timestamp.class);
            String aggregated = "";
            if (filter.getOlsonId() != null) {
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(sessionDate.toInstant(), ZoneId.of(filter.getOlsonId()));
                aggregated = findAggregated(zonedDateTime, filter.getGroupType());
                sessionDate = Timestamp.from(zonedDateTime.toInstant());
            } else {
                aggregated = findAggregated(sessionDate.toInstant().atZone(ZoneOffset.UTC), filter.getGroupType());
            }

            SessionsGroupDataRecord data = new SessionsGroupDataRecord();
            data.setDate(sessionDate);
            data.setTotal(record.get("total", Integer.class));
            if(aggregatedData.containsKey(aggregated)) {
                aggregatedData.get(aggregated).add(data);
            } else {
                aggregatedData.put(aggregated, new ArrayList<>());
                aggregatedData.get(aggregated).add(data);
            }
        }

        for (Map.Entry<String, List<SessionsGroupDataRecord>> entry : aggregatedData.entrySet()) {
            SessionsGroupDataRecord sessionsGroupDataRecord = new SessionsGroupDataRecord();
            sessionsGroupDataRecord.setDate(entry.getValue().get(0).getDate());
            final Integer total = entry.getValue().stream().mapToInt(i -> i.getTotal()).sum();
            sessionsGroupDataRecord.setTotal(total);
            result.add(sessionsGroupDataRecord);
        }

        Collections.sort(result, Comparator.comparing(SessionsGroupDataRecord::getDate));
        return result;
    }

    private String findAggregated(ZonedDateTime zonedDateTime, SessionGroupType sessionGroupType) {
        if(SessionGroupType.DAY.equals(sessionGroupType)) {
            return zonedDateTime.getYear() + "" + zonedDateTime.getMonthValue() + "" + zonedDateTime.getDayOfMonth();
        }
        if(SessionGroupType.WEEK.equals(sessionGroupType)) {
            return zonedDateTime.getYear() + "" + zonedDateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
        if(SessionGroupType.MONTH.equals(sessionGroupType)) {
            return zonedDateTime.getYear() + "" + zonedDateTime.getMonthValue();
        }
        return "";
    }

    public List<String> countDifferentOlsonIds(SessionSearchFilter filter) {
        return dsl.selectDistinct(recintoTZ.OLSONID)
                .from(sesion)
                .innerJoin(evento).on(evento.IDEVENTO.eq(sesion.IDEVENTO))
                .innerJoin(entidad).on(entidad.IDENTIDAD.eq(evento.IDENTIDAD))
                .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .innerJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .innerJoin(recintoTZ).on(recintoTZ.ZONEID.eq(recinto.TIMEZONE))
                .where(buildWhereClause(filter, true))
                .fetch().map(r -> r.getValue(recintoTZ.OLSONID));
    }

    public Long countByFilter(SessionSearchFilter filter) {
        if(filter.getEndDate() == null
                && filter.getRangeDateFrom() == null
                && filter.getRangeDateTo() == null
                && filter.getStartDate() == null
                && filter.getStartDateFrom() == null
                && filter.getStartDateTo() == null
                && (filter.getDaysOfWeek() == null || filter.getDaysOfWeek().isEmpty())
        ) {
            SelectJoinStep<Record1<Integer>> query = dsl.selectCount().from(sesion);
            buildJoinClauses(query, filter, buildFields(filter));
            return query
                    .where(buildWhereClause(filter, true))
                    .fetchOne(0, Long.class);
        }

        SelectFieldOrAsterisk[] fields = buildCountFields();

        SelectJoinStep<Record> query = dsl.select(fields).from(sesion);
        buildJoinClauses(query, filter, fields);

        SessionSearchFilter timezoneSessionSearchFilter = new SessionSearchFilter();
        if(filter.getEndDate() != null
                || filter.getRangeDateFrom() != null
                || filter.getRangeDateTo() != null
                || filter.getStartDate() != null
                || filter.getStartDateFrom() != null
                || filter.getStartDateTo() != null
                || (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty())) {
            timezoneSessionSearchFilter = SerializationUtils.clone(filter);
        }

        query.where(buildWhereClause(filter, false));

        query.orderBy(SortUtils.buildSort(filter.getSort(), SessionField::byName));

        Table<Record> records = DSL.table(query);
        SelectJoinStep<Record1<Integer>> timezoneSubquery = dsl.selectCount().from(records);

        if(timezoneSessionSearchFilter != null) {
            timezoneSubquery.where(buildTimezoneWhereClause(timezoneSessionSearchFilter));
        } else {
            timezoneSubquery.where(buildTimezoneWhereClause(filter));
        }

        return timezoneSubquery.fetchOne(0, Long.class);
    }

    public Long countByFilterWithTimezones(SessionSearchFilter filter) {

        SelectFieldOrAsterisk[] fields = buildCountFields();
        SelectJoinStep<Record1<Integer>> query = dsl.selectCount().from(sesion);
        buildJoinClauses(query, filter, fields);

        SessionSearchFilter timezoneSessionSearchFilter = SerializationUtils.clone(filter);
        query.where(buildWhereClause(timezoneSessionSearchFilter, false));
        return query.fetchOne(0, Long.class);
    }

    public Map<Long, Long> getSessionVenueIds(List<Long> sessionIds) {
        return dsl.select(sesion.IDSESION, configRecinto.IDRECINTO)
                .from(sesion)
                .innerJoin(entidadRecintoConfig).on(sesion.IDRELACIONENTIDADRECINTO.eq(entidadRecintoConfig.IDRELACIONENTRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .where(sesion.IDSESION.in(sessionIds))
                .fetchMap(
                        (RecordMapper<Record, Long>) r -> r.getValue(sesion.IDSESION).longValue(),
                        (RecordMapper<Record, Long>) r -> r.getValue(configRecinto.IDRECINTO).longValue()
                );
    }

    public Map<Long, Long> getSessionVenueTemplateIds(List<Long> sessionIds) {
        return dsl.select(sesion.IDSESION, configRecinto.IDCONFIGURACION)
                .from(sesion)
                .innerJoin(entidadRecintoConfig).on(sesion.IDRELACIONENTIDADRECINTO.eq(entidadRecintoConfig.IDRELACIONENTRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .where(sesion.IDSESION.in(sessionIds))
                .fetchMap(
                        (RecordMapper<Record, Long>) r -> r.getValue(sesion.IDSESION).longValue(),
                        (RecordMapper<Record, Long>) r -> r.getValue(configRecinto.IDCONFIGURACION).longValue()
                );
    }

    public Map<Long, VenueTemplateInfo> getVenueTemplateInfos(List<Long> venueConfigId) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(configRecinto.IDCONFIGURACION, configRecinto.ESGRAFICA, configRecinto.TIPOPLANTILLA,
                configRecintoMultimediaContent.MULTIMEDIACONTENTCODE, configRecintoMultimediaContent.EXTERNALMINIMAPID, configRecinto.EXTERNALVENUETEMPLATEID));
        fields.add(groupConcat(multimediaContentPluginExterno.IDPLUGIN).separator(", ").as(PLUGINS));
        return dsl.select(fields)
                .from(configRecinto)
                .leftJoin(configRecintoMultimediaContent).on(configRecinto.CONFIGURACIONASOCIADA.eq(configRecintoMultimediaContent.IDCONFIGURACION))
                .leftJoin(multimediaContentPluginExterno).on(configRecintoMultimediaContent.IDCONFIGURACION.eq(multimediaContentPluginExterno.IDCONFIGURACION))
                .where(configRecinto.IDCONFIGURACION.in(venueConfigId))
                .groupBy(configRecinto.IDCONFIGURACION)
                .fetch()
                .map(r -> {
                    String pluginString = r.get(PLUGINS, String.class);
                    List<Long> pluginIds = (pluginString == null || pluginString.isEmpty()) ? new ArrayList<>() :
                            Arrays.stream(pluginString.split(", "))
                                    .map(String::trim)
                                    .map(Long::parseLong)
                                    .distinct()
                                    .collect(Collectors.toList());
                    List<ExternalPlugin> plugins = getPluginDetails(pluginIds);
                    VenueTemplateInfo value = new VenueTemplateInfo(
                            r.get(configRecinto.IDCONFIGURACION),
                            CommonUtils.isTrue(r.get(configRecinto.ESGRAFICA)),
                            r.get(configRecinto.TIPOPLANTILLA),
                            r.get(configRecintoMultimediaContent.MULTIMEDIACONTENTCODE),
                            r.get(configRecintoMultimediaContent.EXTERNALMINIMAPID),
                            r.get(configRecinto.EXTERNALVENUETEMPLATEID),
                            plugins);
                    Long id = r.get(configRecinto.IDCONFIGURACION).longValue();
                    return new AbstractMap.SimpleEntry<>(id, value);
                }).stream().collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public List<ExternalPlugin> getPluginDetails(List<Long> pluginIds) {
        return dsl.select(pluginExterno.ID, pluginExterno.NOMBRE, pluginExterno.TIPO)
                .from(pluginExterno)
                .where(pluginExterno.ID.in(pluginIds))
                .fetch()
                .map(r -> new ExternalPlugin(
                        r.get(pluginExterno.NOMBRE),
                        r.get(pluginExterno.TIPO)
                ));
    }


    public SessionRecord findSession(Long sessionId) {
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setId(sessionId);
        List<SessionRecord> sessions = findSessions(filter, null);
        if (sessions == null || sessions.size() != 1) {
            return null;
        }
        return sessions.iterator().next();
    }

    public SessionRecord findSession(Long eventId, Long sessionId) {
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setId(sessionId);
        filter.setEventId(Arrays.asList(eventId));
        List<SessionRecord> sessions = findSessions(filter, null);
        if (sessions == null || sessions.size() != 1) {
            return null;
        }
        return sessions.iterator().next();
    }


    public Long getEventId(Long sessionId) {
        return dsl.select(sesion.IDEVENTO)
                .from(sesion)
                .where(sesion.IDSESION.eq(sessionId.intValue()))
                .fetchOne(0, long.class);
    }

    public void disableBookingByEvent(Long eventId) {
        dsl.update(sesion).set(sesion.RESERVASACTIVAS, (byte) 0).where(sesion.IDEVENTO.eq(eventId.intValue())).execute();
    }

    public List<Session> getFinalizedEventSessions(ZonedDateTime archivedDate, List<Long> excludeEntityIds) {
        return dsl.select(sesion.IDSESION, sesion.IDEVENTO, sesion.ESTADO)
                .from(sesion).innerJoin(evento).onKey()
                .where(sesion.ARCHIVADO.ne((byte) 1)
                        .and(evento.IDENTIDAD.notIn(excludeEntityIds))
                        .and(evento.ESTADO.in(EventStatus.FINISHED.getId(), EventStatus.DELETED.getId()))
                        .and(DSL.or(
                                evento.FECHAINICIO.lt(Timestamp.from(archivedDate.toInstant())).and(evento.FECHAFIN.lt(Timestamp.from(archivedDate.toInstant()))),
                                evento.FECHAINICIO.isNull().or(evento.FECHAFIN.isNull()))))
                .fetch(r -> {
                    Session session = new Session();
                    session.setSessionId(r.get(sesion.IDSESION).longValue());
                    session.setEventId(r.get(sesion.IDEVENTO).longValue());
                    session.setStatus(r.get(sesion.ESTADO));
                    return session;
                });
    }

    public List<Session> findSessionsById(List<Long> sessionIds) {
        return dsl.select(sesion.IDSESION, sesion.IDEVENTO, sesion.ESTADO, sesion.NOMBRE, sesion.FECHAINICIOSESION)
                .from(sesion)
                .where(sesion.IDSESION.in(sessionIds))
                .fetch(r -> {
                    Session session = new Session();
                    session.setSessionId(r.get(sesion.IDSESION).longValue());
                    session.setEventId(r.get(sesion.IDEVENTO).longValue());
                    session.setStatus(r.get(sesion.ESTADO));
                    session.setName(r.get(sesion.NOMBRE));
                    session.setSessionStartDate(CommonUtils.timestampToZonedDateTime(r.get(sesion.FECHAINICIOSESION)));
                    return session;
                });
    }

    public void archiveSessions(List<Long> sessionIds) {
        if (CollectionUtils.isNotEmpty(sessionIds)) {
            dsl.update(sesion).set(sesion.ARCHIVADO, (byte) 1).where(sesion.IDSESION.in(sessionIds)).execute();
        }
    }

    public List<Integer> getSessionAvailableSpaces(Integer sessionId) {
        return dsl.select(configRecintoEspacio.IDESPACIO)
                .from(sesion)
                .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .leftJoin(configRecintoEspacio).on(configRecintoEspacio.IDRECINTO.eq(configRecinto.IDRECINTO))
                .where(sesion.IDSESION.eq(sessionId))
                .fetchInto(Integer.class);
    }

    public List<SessionRecord> findSessionsByIDs(Set<Long> sessions) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(EVENT_ID_FIELDS);

        SelectJoinStep<Record> query =
                dsl.select(fields)
                        .from(sesion);

        query.where(buildWhereClause(sessions));
        return query.fetch().into(SessionRecord.class);
    }

    private Condition buildWhereClause(Set<Long> filter) {
        Condition conditions = DSL.trueCondition();

        List<Long> targetList = new ArrayList<>(filter);

        conditions = JooqUtils.addConditionIn(conditions, sesion.IDSESION, targetList);
        conditions = JooqUtils.addConditionIn(conditions, sesion.ESTADO, ALLOWED_SESSION_STATUS);
        return conditions;
    }

    public Integer getSessionShard(Long sessionId) {
        List<CpanelPoliticasShardRecord> shards = dsl.select(politicasShard.fields())
                .from(sesion)
                .innerJoin(evento).on(evento.IDEVENTO.eq(sesion.IDEVENTO))
                .innerJoin(entidad).on(entidad.IDENTIDAD.eq(evento.IDENTIDAD))
                .innerJoin(politicasShard).on(
                        politicasShard.IDEVENTO.eq(evento.IDEVENTO).or(
                                politicasShard.IDENTIDAD.eq(entidad.IDENTIDAD).or(
                                        politicasShard.IDOPERADORA.eq(entidad.IDOPERADORA))))
                .where(sesion.IDSESION.eq(sessionId.intValue()))
                .fetchInto(CpanelPoliticasShardRecord.class);

        return processSessionShard(shards);
    }

    public List<Long> bulkInsertSessions(List<Session> sessions) {
        InsertSetStep<CpanelSesionRecord> inserts = dsl.insertInto(CPANEL_SESION);
        final InsertSetMoreStep<CpanelSesionRecord> insertStep = inserts.set(CPANEL_SESION.IDEVENTO, 0);
        boolean first = true;
        for (Session session : sessions) {
            if (!first) {
                insertStep.newRecord();
            }
            insertStep.set(CPANEL_SESION.IDEVENTO, longToInt(session.getEventId()))
                    .set(CPANEL_SESION.NOMBRE, session.getName())
                    .set(CPANEL_SESION.ESABONO, checkBooleanValues(session.getSeasonPass(), false))
                    .set(CPANEL_SESION.PUBLICADO, checkBooleanValues(session.getPublished(), false))
                    .set(CPANEL_SESION.ENVENTA, checkBooleanValues(session.getOnSale(), false))
                    .set(CPANEL_SESION.RESERVASACTIVAS, checkBooleanValues(session.getBookings(), false))
                    .set(CPANEL_SESION.ESPACIOVALIDACIONACCESO, longToInt(session.getAccessValidationSpaceId()))
                    .set(CPANEL_SESION.USAACCESOSPLANTILLA, session.getUseTemplateAccess())
                    .set(CPANEL_SESION.USALIMITESCUPOSPLANTILLAEVENTO, checkBooleanValues(session.getUseLimitsQuotasTemplateEvent(), false))
                    .set(CPANEL_SESION.IDRELACIONENTIDADRECINTO, longToInt(session.getVenueEntityConfigId()))
                    .set(CPANEL_SESION.FECHAINICIOSESION, checkZonedDateTimeValues(session.getSessionStartDate()))
                    .set(CPANEL_SESION.FECHAPUBLICACION, checkZonedDateTimeValues(session.getPublishDate()))
                    .set(CPANEL_SESION.FECHAVENTA, checkZonedDateTimeValues(session.getSalesDate()))
                    .set(CPANEL_SESION.FECHAFINSESION, checkZonedDateTimeValues(session.getSessionEndDate()))
                    .set(CPANEL_SESION.FECHAREALFINSESION, checkZonedDateTimeValues(session.getSessionRealEndDate()))
                    .set(CPANEL_SESION.FECHAINICIORESERVA, checkZonedDateTimeValues(session.getBookingStartDate()))
                    .set(CPANEL_SESION.FECHAFINRESERVA, checkZonedDateTimeValues(session.getBookingEndDate()))
                    .set(CPANEL_SESION.TIPOVENTA, session.getSaleType())
                    .set(CPANEL_SESION.TIPOHORARIOACCESOS, intToByte(session.getTypeScheduleAccess()))
                    .set(CPANEL_SESION.IDIMPUESTO, longToInt(session.getTaxId()))
                    .set(CPANEL_SESION.IDIMPUESTORECARGO, longToInt(session.getChargeTaxId()))
                    .set(CPANEL_SESION.ESTADO, session.getStatus())
                    .set(CPANEL_SESION.AFORO, session.getCapacity())
                    .set(CPANEL_SESION.ESTADOGENERACIONAFORO, session.getCapacityGenerationStatus())
                    .set(CPANEL_SESION.IDEXTERNO, longToInt(session.getExternalId()))
                    .set(CPANEL_SESION.ISEXTERNAL, session.getExternal())
                    .set(CPANEL_SESION.FECHANODEFINITIVA, checkBooleanValues(session.getFinalDate(), true))
                    .set(CPANEL_SESION.COLOR, session.getColor())
                    .set(CPANEL_SESION.ALLOWPARTIALREFUND, checkBooleanValues(session.getAllowPartialRefund(), false))
                    .set(CPANEL_SESION.REFERENCE, session.getReference())
                    .set(CPANEL_SESION.USARDATOSFISCALESPRODUCTOR, checkBooleanValues(session.getUseProducerTaxData(), false))
                    .set(CPANEL_SESION.IDPROMOTOR, longToInt(session.getProducerId()))
                    .set(CPANEL_SESION.INVOICEPREFIXID, longToInt(session.getInvoicePrefixId()))
                    .set(CPANEL_SESION.CHECKORPHANSEATS, checkBooleanValues(session.getEnableOrphanSeats(), false));

            first = false;
        }
        return insertStep.returningResult(CPANEL_SESION.IDSESION).fetch().map(sessionId -> sessionId.value1().longValue());
    }

    public int bulkUpdateSessions(List<Long> sessionIds, UpdateSessionRequestDTO session) {

        UpdateSetMoreStep<CpanelSesionRecord> updateStep = dsl.update(CPANEL_SESION).
                set(CPANEL_SESION.IDEVENTO, CPANEL_SESION.IDEVENTO);

        if (session.getName() != null) {
            updateStep = updateStep.set(CPANEL_SESION.NOMBRE, session.getName());
        }
        if (session.getStatus() != null) {
            if (SessionStatus.PREVIEW.getId().equals(session.getStatus().getId())) {
                updateStep = updateStep.set(CPANEL_SESION.ESTADO, SessionStatus.READY.getId());
                updateStep = updateStep.set(CPANEL_SESION.ISPREVIEW, true);
            } else {
                updateStep = updateStep.set(CPANEL_SESION.ESTADO, session.getStatus().getId());
                updateStep = updateStep.set(CPANEL_SESION.ISPREVIEW, false);
            }
        }
        if (session.getTicketTax() != null) {
            updateStep = updateStep.set(CPANEL_SESION.IDIMPUESTO, longToInt(session.getTicketTax().getId()));
        }
        if (session.getUnpublishReason() != null) {
            updateStep = updateStep.set(CPANEL_SESION.RAZONCANCELACIONPUBLICACION, session.getUnpublishReason());
        } else if (session.getEnableSales() != null && Boolean.TRUE.equals(session.getEnableSales())) {
            updateStep = updateStep.set(CPANEL_SESION.RAZONCANCELACIONPUBLICACION, session.getUnpublishReason());
        }

        if (session.getChargesTax() != null) {
            updateStep = updateStep.set(CPANEL_SESION.IDIMPUESTORECARGO, longToInt(session.getChargesTax().getId()));
        }
        if (session.getEnableBookings() != null) {
            updateStep = updateStep.set(CPANEL_SESION.RESERVASACTIVAS, checkBooleanValues(session.getEnableBookings(), false));
        }
        if (session.getEnableChannels() != null) {
            updateStep = updateStep.set(CPANEL_SESION.PUBLICADO, checkBooleanValues(session.getEnableChannels(), false));
        }
        if (session.getEnableSales() != null) {
            updateStep = updateStep.set(CPANEL_SESION.ENVENTA, checkBooleanValues(session.getEnableSales(), false));
        }
        if (session.getAccessScheduleType() != null) {
            updateStep = updateStep.set(CPANEL_SESION.TIPOHORARIOACCESOS, intToByte(session.getAccessScheduleType().getType()));
        }
        if (session.getSpace() != null) {
            updateStep = updateStep.set(CPANEL_SESION.ESPACIOVALIDACIONACCESO, longToInt(session.getSpace().getId()));
        }
        if (session.getSaleType() != null) {
            updateStep = updateStep.set(CPANEL_SESION.TIPOVENTA, session.getSaleType());
        }
        if (session.getExternal() != null) {
            updateStep = updateStep.set(CPANEL_SESION.ISEXTERNAL, session.getExternal());
        }
        if (session.getPresaleEnabled() != null) {
            updateStep = updateStep.set(CPANEL_SESION.PRESALEENABLED, checkBooleanValues(session.getPresaleEnabled(), false));
        }
        if (session.getEnableOrphanSeats() != null) {
            updateStep = updateStep.set(CPANEL_SESION.CHECKORPHANSEATS, checkBooleanValues(session.getEnableOrphanSeats(), false));
        }
        updateStep = updateSessionDates(updateStep, session.getDate());
        return updateStep.where(CPANEL_SESION.IDSESION.in(sessionIds)).execute();
    }

    public <T> int updateField(final Integer sessionId, final Field<T> field, final T value) {
        return dsl.update(CPANEL_SESION).set(field, value).where(CPANEL_SESION.IDSESION.eq(sessionId))
                .execute();
    }

    public <T> void updateFieldBySessions(final List<Integer> sessions, final Field<T> field, final T value) {
        dsl.update(CPANEL_SESION).set(field, value).where(CPANEL_SESION.IDSESION.in(sessions)).execute();
    }

    public List<SessionRecord> findSessionsFromSessionPackOrderByFechaInicio(Integer sessionPackId) {
        return dsl.select(sesion.fields())
                .from(sesion)
                .leftJoin(sesionesAbono).on(sesionesAbono.IDSESION.eq(sesion.IDSESION))
                .where(sesionesAbono.IDABONO.eq(sessionPackId)
                        .and(sesion.ESTADO.notEqual(0)))
                .orderBy(sesion.FECHAINICIOSESION.asc())
                .fetchInto(SessionRecord.class);
    }

    private UpdateSetMoreStep<CpanelSesionRecord> updateSessionDates(UpdateSetMoreStep<CpanelSesionRecord> updateStep, SessionDateDTO dates) {
        if (dates != null) {
            if (dates.getChannelPublication() != null) {
                updateStep = updateStep.set(CPANEL_SESION.FECHAPUBLICACION, checkZonedDateTimeValues(dates.getChannelPublication()));
            }
            if (dates.getSalesStart() != null) {
                updateStep = updateStep.set(CPANEL_SESION.FECHAVENTA, checkZonedDateTimeValues(dates.getSalesStart()));
            }
            if (dates.getSalesEnd() != null) {
                updateStep = updateStep.set(CPANEL_SESION.FECHAFINSESION, checkZonedDateTimeValues(dates.getSalesEnd()));
            }
            if (dates.getAdmissionStart() != null) {
                updateStep = updateStep.set(CPANEL_SESION.APERTURAACCESOS, checkZonedDateTimeValues(dates.getAdmissionStart()));
            }
            if (dates.getAdmissionEnd() != null) {
                updateStep = updateStep.set(CPANEL_SESION.CIERREACCESOS, checkZonedDateTimeValues(dates.getAdmissionEnd()));
            }
            if (dates.getBookingsStart() != null) {
                updateStep = updateStep.set(CPANEL_SESION.FECHAINICIORESERVA, checkZonedDateTimeValues(dates.getBookingsStart()));
            }
            if (dates.getBookingsEnd() != null) {
                updateStep = updateStep.set(CPANEL_SESION.FECHAFINRESERVA, checkZonedDateTimeValues(dates.getBookingsEnd()));
            }
        }
        return updateStep;
    }

    private void buildJoinClauses(SelectJoinStep<?> query, SessionSearchFilter filter, SelectFieldOrAsterisk[] fields) {
        if (containsJoinFields(fields) || anyNotNull(filter.getEventId(), filter.getEntityId(), filter.getOperatorId())) {
            query.innerJoin(evento).on(evento.IDEVENTO.eq(sesion.IDEVENTO));
            query.innerJoin(entidad).on(entidad.IDENTIDAD.eq(evento.IDENTIDAD));
        }

        if ( containsJoinFields(fields) || anyNotNull(
                filter.getVenueConfigId(),
                filter.getVenueId(),
                filter.getVenueEntityId(),
                filter.getInventoryProvider(),
                filter.getEndDate(),
                filter.getRangeDateFrom(),
                filter.getRangeDateTo(),
                filter.getStartDate(),
                filter.getStartDateFrom(),
                filter.getStartDateTo())) {
            query.innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO));
            query.innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION)); // filter.getVenueConfigId() and filter.getInventoryProvider()
            query.leftJoin(configRecintoEspacio).on(configRecintoEspacio.IDESPACIO.eq(configRecinto.ESPACIORECINTO));
            query.innerJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO)); // filter.getVenueId()
            query.innerJoin(recintoTZ).on(recintoTZ.ZONEID.eq(recinto.TIMEZONE));
            query.leftJoin(espacio).on(espacio.IDESPACIO.eq(sesion.ESPACIOVALIDACIONACCESO));
        }

        if (containsJoinFields(fields)) {
            query.leftJoin(ticketTax).on(ticketTax.IDIMPUESTO.eq(sesion.IDIMPUESTO));
            query.leftJoin(chargesTax).on(chargesTax.IDIMPUESTO.eq(sesion.IDIMPUESTORECARGO));
        }
    }

    private Condition buildWhereClause(SessionSearchFilter filter, boolean filterWeekdayWithoutOlson) {
        Condition conditions = DSL.trueCondition();
        if (!BooleanUtils.isTrue(filter.getIncludeDeleted())) {
            conditions = conditions.and(sesion.ESTADO.ne(SessionStatus.DELETED.getId()));
        }
        conditions = JooqUtils.addConditionIn(conditions, sesion.IDEVENTO, filter.getEventId());
        conditions = JooqUtils.addConditionEquals(conditions, sesion.IDSESION, filter.getId());
        conditions = JooqUtils.addConditionIn(conditions, sesion.IDSESION, filter.getIds());
        conditions = resolveStatusCondition(filter, conditions);
        conditions = JooqUtils.addConditionIn(conditions, sesion.ESTADOGENERACIONAFORO, filter.getGenerationStatus().stream().map(SessionGenerationStatus::getId).map(Integer::longValue).collect(Collectors.toList()));

        if (filterWeekdayWithoutOlson && filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty() && filter.getOlsonId() != null) {
            List<Integer> daysOfWeek = filter.getDaysOfWeek().stream().map(DayOfWeek::getValue).collect(Collectors.toList());
            Field field = field("(WEEKDAY(CONVERT_TZ(" + sesion.FECHAINICIOSESION.getName() + ", '" + OLSON_UTC + SEPARATOR + filter.getOlsonId() + "')) + 1)");
            conditions = conditions.and(field.in(daysOfWeek));
        }

        conditions = JooqUtils.addConditionEquals(conditions, entidad.IDENTIDAD, filter.getEntityId());
        conditions = JooqUtils.addConditionEquals(conditions, entidad.IDOPERADORA, filter.getOperatorId());
        conditions = JooqUtils.addConditionEquals(conditions, configRecinto.IDCONFIGURACION, filter.getVenueConfigId());
        conditions = JooqUtils.addConditionIn(conditions, configRecinto.IDRECINTO, filter.getVenueId());
        conditions = JooqUtils.addConditionEquals(conditions, recinto.IDENTIDAD, filter.getVenueEntityId());

        conditions = resolveSessionType(filter, conditions);
        if (filter.getSessionPack() != null) {
            conditions = filter.getSessionPack() ? conditions.and(sesion.ESABONO.isTrue()) :
                    conditions.and(sesion.ESABONO.isFalse());
        }
        if (filter.getAllowPartialRefund() != null) {
            conditions = filter.getAllowPartialRefund() ? conditions.and(sesion.ALLOWPARTIALREFUND.isTrue()) :
                    conditions.and(sesion.ALLOWPARTIALREFUND.isFalse());
        }
        if (!CommonUtils.isEmpty(filter.getSaleType())) {
            conditions = conditions.and(sesion.TIPOVENTA.in(
                    filter.getSaleType().stream().map(SessionSalesType::getType).collect(Collectors.toList())));
        }
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            String freeSearch = filter.getFreeSearch();
            conditions = conditions.and(sesion.NOMBRE.like("%" + freeSearch + "%"));
        }

        if(filter.getEndDate() != null
                || filter.getRangeDateFrom() != null
                || filter.getRangeDateTo() != null
                || filter.getStartDate() != null
                || filter.getStartDateFrom() != null
                || filter.getStartDateTo() != null) {

            List<FilterWithOperator<ZonedDateTime>> itemsToRemove = new ArrayList<>();
            if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
                for (FilterWithOperator<ZonedDateTime> item : filter.getStartDate()) {
                    if (item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.LESS_THAN) || item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.LESS_THAN_OR_EQUALS)) {
                        item.setValue(item.getValue().plusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
                    } else if (item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.GREATER_THAN) || item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.GREATER_THAN_OR_EQUALS)) {
                        item.setValue(item.getValue().minusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
                    } else if (item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.EQUALS) || item.getOperator().equals(es.onebox.core.serializer.dto.request.Operator.NOT_EQUALS)) {
                        itemsToRemove.add(item);
                    }
                }
                filter.getStartDate().removeAll(itemsToRemove);
            }

            if (filter.getEndDate() != null) {
                if (filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.LESS_THAN) || filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.LESS_THAN_OR_EQUALS)) {
                    filter.getEndDate().setValue(filter.getEndDate().getValue().plusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
                } else if (filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.GREATER_THAN) || filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.GREATER_THAN_OR_EQUALS)) {
                    filter.getEndDate().setValue(filter.getEndDate().getValue().minusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
                } else if (filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.EQUALS) || filter.getEndDate().getOperator().equals(es.onebox.core.serializer.dto.request.Operator.NOT_EQUALS)) {
                    filter.setEndDate(null);
                }
            }

            if (filter.getStartDateFrom() != null) {
                filter.setStartDateFrom(filter.getStartDateFrom().minusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
            }

            if (filter.getStartDateTo() != null) {
                filter.setStartDateTo(filter.getStartDateTo().plusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
            }

            if (filter.getRangeDateFrom() != null) {
                filter.setRangeDateFrom(filter.getRangeDateFrom().minusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
            }

            if (filter.getRangeDateTo() != null) {
                filter.setRangeDateTo(filter.getRangeDateTo().plusHours(NUMBER_OF_MAX_HOURS_OF_DIFFERENCE_ON_TIMEZONES));
            }
        }

        conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, filter.getStartDate(), sesion.FECHAINICIOSESION, Operator.AND);
        conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, filter.getEndDate(), sesion.FECHAREALFINSESION, Operator.AND, sesion.FECHAINICIOSESION);

        ZonedDateTime startDateFrom = filter.getStartDateFrom();
        ZonedDateTime startDateTo = filter.getStartDateTo();
        conditions = JooqUtils.addConditionGreaterOrEquals(conditions, sesion.FECHAINICIOSESION, startDateFrom);
        conditions = JooqUtils.addConditionLessOrEquals(conditions, sesion.FECHAINICIOSESION, startDateTo);

        if (filter.getRangeDateFrom() != null && filter.getRangeDateTo() != null) {
            if (!filter.getRangeDateFrom().isAfter(filter.getRangeDateTo())) {
                conditions = conditions.and(sesion.FECHAINICIOSESION.le(Timestamp.from(filter.getRangeDateTo().toInstant())));
                conditions = conditions.and(sesion.FECHAREALFINSESION.ge(Timestamp.from(filter.getRangeDateFrom().toInstant()))
                        .or(sesion.FECHAFINSESION.ge(Timestamp.from(filter.getRangeDateFrom().toInstant()))));
            } else {
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "bad period definition", null);
            }
        }

        conditions = filterPeriods(filter, conditions);
        conditions = resolveAdmissionStartDateCondition(filter, conditions);
        if (CollectionUtils.isNotEmpty(filter.getInValidationDate())) {
            conditions = conditions.and(resolveInValidationRange(filter));
        }

        if (CollectionUtils.isNotEmpty(filter.getEventStatus())) {
            conditions = JooqUtils.addConditionIn(conditions, evento.ESTADO, filter.getEventStatus().stream().map(EventStatus::getId).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(filter.getEventType())) {
            conditions = JooqUtils.addConditionIn(conditions, evento.TIPOEVENTO, filter.getEventType().stream().map(EventType::getId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(filter.getAccessValidationSpaceId())) {
            Condition orCondition = DSL.or(
                    sesion.ESPACIOVALIDACIONACCESO.isNotNull().and(sesion.ESPACIOVALIDACIONACCESO.in(filter.getAccessValidationSpaceId())),
                    sesion.ESPACIOVALIDACIONACCESO.isNull().and(configRecinto.ESPACIORECINTO.in(filter.getAccessValidationSpaceId())));
            conditions = conditions.and(orCondition);
        }

        if (StringUtils.isNotBlank(filter.getInventoryProvider())) {
            conditions = JooqUtils.addConditionEquals(conditions, configRecinto.INVENTORYPROVIDER, filter.getInventoryProvider());
        }

        return conditions;
    }

    private Condition resolveSessionType(SessionSearchFilter filter, Condition conditions) {
        if (CollectionUtils.isNotEmpty(filter.getType())) {
            List<SessionType> types = filter.getType();
            if (types.contains(SessionType.SEASON_FREE) || types.contains(SessionType.SEASON_RESTRICTIVE)) {
                Condition typeCondition = DSL.and(sesion.ESABONO.isTrue().and(evento.TIPOABONO.in(types.stream().map(el -> el.getType().byteValue()).collect(Collectors.toList()))));
                if (types.contains(SessionType.SESSION)) {
                    typeCondition = typeCondition.or(sesion.ESABONO.isNull().or(sesion.ESABONO.isFalse()));
                }
                conditions = conditions.and(typeCondition);
            } else {
                conditions = conditions.and(sesion.ESABONO.isNull().or(sesion.ESABONO.isFalse()));
            }
        }
        return conditions;
    }

    private Condition resolveStatusCondition(SessionSearchFilter filter, Condition conditions) {
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            if (filter.getStatus().contains(SessionStatus.PREVIEW) || filter.getStatus().contains(SessionStatus.READY)) {
                if (filter.getStatus().contains(SessionStatus.PREVIEW) && filter.getStatus().contains(SessionStatus.READY)) {
                    conditions = getAllSessionStatus(filter, conditions);
                } else if (filter.getStatus().contains(SessionStatus.READY)) {
                    conditions = resolveReady(filter, conditions, sesion.ISPREVIEW.isFalse());
                } else {
                    conditions = resolveReady(filter, conditions, sesion.ISPREVIEW.isTrue());
                }

            } else {
                conditions = getAllSessionStatus(filter, conditions);
            }
        }
        return conditions;
    }

    private Condition resolveReady(SessionSearchFilter filter, Condition conditions, Condition preview) {
        List<Long> inStatus = filter.getStatus().stream().filter(status -> !SessionStatus.READY.equals(status)).map(SessionStatus::getId)
                .map(Integer::longValue).collect(Collectors.toList());
        conditions = conditions.and(sesion.ESTADO.in(inStatus).or(sesion.ESTADO.eq(SessionStatus.READY.getId()).and(preview)));
        return conditions;
    }

    private Condition getAllSessionStatus(SessionSearchFilter filter, Condition conditions) {
        return JooqUtils.addConditionIn(conditions, sesion.ESTADO, filter.getStatus().stream().map(SessionStatus::getId)
                .map(Integer::longValue).collect(Collectors.toList()));
    }

    private Condition resolveInValidationRange(SessionSearchFilter filter) {
        Condition conditions = DSL.noCondition();
        Condition inCourse = CaseStatement.ADMISSION_START_NULLABLE.lessThan(today()).and(CaseStatement.ADMISSION_END_NULLABLE.greaterThan(today()));
        for (FilterWithOperator<ZonedDateTime> operator : filter.getInValidationDate()) {
            conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, operator, CaseStatement.ADMISSION_START_NULLABLE, Operator.AND);
        }
        return conditions.or(inCourse);
    }

    private Condition resolveAdmissionStartDateCondition(SessionSearchFilter filter, Condition conditions) {
        if (CollectionUtils.isNotEmpty(filter.getAdmissionDate())) {
            conditions = JooqUtils.addDateWithListOperatorConditions(conditions, filter.getAdmissionDate(), CaseStatement.ADMISSION_START_NULLABLE, Operator.AND);
        }
        return conditions;
    }

    private SelectFieldOrAsterisk[] buildFields(SessionSearchFilter filter) {
        if (filter == null || CollectionUtils.isEmpty(filter.getFields())) {
            Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = new HashSet<>();
            selectFieldOrAsterisks.addAll(Arrays.asList(sesion.fields()));
            selectFieldOrAsterisks.addAll(Arrays.asList(JOIN_FIELDS));
            if(filter.getEndDate() != null || filter.getRangeDateFrom() != null || filter.getRangeDateTo() != null || filter.getStartDate() != null
                    || filter.getStartDateFrom() != null || filter.getStartDateTo() != null || (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty())) {
                selectFieldOrAsterisks.add(recintoTZ.OLSONID);
                Field fieldStart = field("CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneStartDate");
                selectFieldOrAsterisks.add(fieldStart);
                Field fieldRealDate = field("CONVERT_TZ(" + getQuotedField(SessionField.REAL_END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndRealDate");
                selectFieldOrAsterisks.add(fieldRealDate);
                Field fieldEnd = field("CONVERT_TZ(" + getQuotedField(SessionField.END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndDate");
                selectFieldOrAsterisks.add(fieldEnd);
                Field weekDayNumber = field("WEEKDAY(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")) + 1").as("weekDayNumber");
                selectFieldOrAsterisks.add(weekDayNumber);
            }
            return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
        } else {
            Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = buildFilteredFields(filter);
            return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
        }
    }

    private SelectFieldOrAsterisk[] buildCountFields() {
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = new HashSet<>();
        selectFieldOrAsterisks.add(recintoTZ.OLSONID);
        Field fieldStart = field("CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneStartDate");
        selectFieldOrAsterisks.add(fieldStart);
        Field fieldRealDate = field("CONVERT_TZ(" + getQuotedField(SessionField.REAL_END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndRealDate");
        selectFieldOrAsterisks.add(fieldRealDate);
        Field fieldEnd = field("CONVERT_TZ(" + getQuotedField(SessionField.END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndDate");
        selectFieldOrAsterisks.add(fieldEnd);
        Field weekDayNumber = field("WEEKDAY(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")) + 1").as("weekDayNumber");
        selectFieldOrAsterisks.add(weekDayNumber);
        return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
    }

    private Set<SelectFieldOrAsterisk> buildFilteredFields(SessionSearchFilter filter) {
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = new HashSet<>();
        this.requiredFields(filter, selectFieldOrAsterisks);
        Set<SessionField> processedFields = new HashSet<>();
        for (String field : filter.getFields()) {
            SessionField sessionField = SessionField.getByRequestField(field);
            if (sessionField != null) {
                Field<?> dbField = sessionField.getField();
                if (dbField != null && !dbField.equals(sesion.IDSESION) && !processedFields.contains(sessionField)) {
                    selectFieldOrAsterisks.add(dbField);
                    processedFields.add(sessionField);
                } else if (dbField == null) {
                    if (SessionField.STATUS_FLAGS.equals(sessionField)) {
                        addNecessaryFieldsForStatusFlagsSession(processedFields, selectFieldOrAsterisks);
                    }
                }
                // the field "type" not only requires selecting the column sesion.esAbono but also the column evento.tipoAbono for later conversion to enum
                if (field.equals(SessionField.TYPE.getRequestField())) {
                    selectFieldOrAsterisks.add(JOIN_EVENT_PACK_TYPE);
                }
            }
        }
        return selectFieldOrAsterisks;
    }

    private void addNecessaryFieldsForStatusFlagsSession(Set<SessionField> processedFields, Set<SelectFieldOrAsterisk> selectFieldOrAsterisks) {
        List<SessionField> requiredSessionFields = List.of(SessionField.STATUS, SessionField.RELEASE_ENABLED,
                SessionField.RELEASE_DATE, SessionField.SALE_DATE, SessionField.SALE_END_DATE, SessionField.IS_PREVIEW,
                SessionField.EVENT_STATUS, SessionField.EVENT_ID, SessionField.SALE, SessionField.EVENT_ENTITY_ID);

        requiredSessionFields.stream()
                .filter(field -> !processedFields.contains(field))
                .filter(Objects::nonNull)
                .forEach(field -> {
                    if (field.getField() != null) {
                        selectFieldOrAsterisks.add(field.getField());
                        processedFields.add(field);
                    }
                });

    }

    private void requiredFields(SessionSearchFilter filter, Set<SelectFieldOrAsterisk> selectFieldOrAsterisks) {
        selectFieldOrAsterisks.add(sesion.IDSESION);
        if(filter.getEndDate() != null || filter.getRangeDateFrom() != null || filter.getRangeDateTo() != null || filter.getStartDate() != null
            || filter.getStartDateFrom() != null || filter.getStartDateTo() != null || (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty())) {
            selectFieldOrAsterisks.add(recintoTZ.OLSONID);
            Field fieldStart = field("CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneStartDate");
            selectFieldOrAsterisks.add(fieldStart);
            Field fieldRealDate = field("CONVERT_TZ(" + getQuotedField(SessionField.REAL_END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndRealDate");
            selectFieldOrAsterisks.add(fieldRealDate);
            Field fieldEnd = field("CONVERT_TZ(" + getQuotedField(SessionField.END_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")").as("timezoneEndDate");
            selectFieldOrAsterisks.add(fieldEnd);
            Field weekDayNumber = field("WEEKDAY(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ", 'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")) + 1").as("weekDayNumber");
            selectFieldOrAsterisks.add(weekDayNumber);
        }
    }

    private Condition filterPeriods(SessionSearchFilter filter, Condition conditions) {
        if (CollectionUtils.isNotEmpty(filter.getHourPeriods())) {
            StringBuilder condition;
            Condition periodsConditions = DSL.falseCondition();
            for (HourPeriod hourPeriod : filter.getHourPeriods()) {
                condition = new StringBuilder()
                        .append("('" + hourPeriod.getFrom().toLocalTime().toString() + "' <= ")
                        .append(" CONCAT(LPAD(HOUR(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ",'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")),2,0), ")
                        .append("':', ")
                        .append("LPAD(MINUTE(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ",'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")),2,0))  AND ")
                        .append("'" + hourPeriod.getTo().toLocalTime().toString() + "' >= ")
                        .append(" CONCAT(LPAD(HOUR(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ",'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")),2,0), ")
                        .append("':', ")
                        .append("LPAD(MINUTE(CONVERT_TZ(" + getQuotedField(SessionField.START_DATE) + ",'UTC'," + getQuotedField(SessionField.VENUE_TEMPLATE_VENUE_TZ) + ")),2,0)))");
                periodsConditions = periodsConditions.or(condition.toString());
            }
            conditions = conditions.and(periodsConditions);
        }
        return conditions;
    }

    private String getQuotedField(SessionField field) {
        return field.getField().toString().replaceAll("\"", "`");
    }

    private Timestamp checkZonedDateTimeValues(ZonedDateTime date) {
        if (date == null) {
            return null;
        }
        return Timestamp.from(date.toInstant());
    }

    private Timestamp checkZonedDateTimeValues(ZonedDateTimeWithRelative date) {
        if (date == null) {
            return null;
        }
        return Timestamp.from(date.absolute().toInstant());
    }

    private Byte checkBooleanValues(Boolean value, Boolean invert) {
        if (Boolean.TRUE.equals(invert)) {
            return (byte) BooleanUtils.toInteger(value, 0, 1, 0);
        } else {
            return (byte) BooleanUtils.toInteger(value, 1, 0, 0);
        }
    }

    private SessionRecord buildSessionRecord(Record record, SelectFieldOrAsterisk[] fields) {
        SessionRecord session = buildFieldsThatMustBePresent(record);
        //Add join fields only if has been added to base session fields
        if (containsJoinFields(fields)) {
            setValue(session::setEventName, record, JOIN_EVENT_NAME, fields);
            setValue(session::setEventType, record, JOIN_EVENT_TYPE, fields);
            setValue(session::setEventStatus, record, JOIN_EVENT_STATUS, fields);
            setValue(session::setEntityId, record, JOIN_ENTITY_ID, fields);
            setValue(session::setEntityName, record, JOIN_ENTITY_NAME, fields);
            setValue(session::setOperatorId, record, JOIN_OPERATOR_ID, fields);
            setValue(session::setVenueTemplateId, record, JOIN_VENUETEMPLATE_ID, fields);
            setValue(session::setVenueTemplateName, record, JOIN_VENUETEMPLATE_NAME, fields);
            setValue(session::setVenueTemplateType, record, JOIN_VENUETEMPLATE_TYPE, fields);
            setValue(session::setVenueTemplateGraphic, record, JOIN_VENUETEMPLATE_GRAPHIC, fields);
            setValue(session::setVenueTemplateSpaceId, record, JOIN_VENUETEMPLATE_SPACE_ID, fields);
            setValue(session::setVenueTemplateSpaceName, record, JOIN_VENUETEMPLATE_SPACE_NAME, fields);
            setValue(session::setVenueId, record, JOIN_VENUE_ID, fields);
            setValue(session::setVenueName, record, JOIN_VENUE_NAME, fields);
            setValue(session::setVenueCity, record, JOIN_VENUE_CITY, fields);
            setValue(session::setVenueCountryId, record, JOIN_VENUE_COUNTRY, fields);
            setValue(session::setVenueTZ, record, JOIN_VENUE_TZ_OLSON, fields);
            setValue(session::setVenueTZName, record, JOIN_VENUE_TZ_NAME, fields);
            setValue(session::setVenueTZOffset, record, JOIN_VENUE_TZ_OFFSET, fields);
            setValue(session::setTaxTicketName, record, JOIN_TAX_TICKET_NAME, fields);
            setValue(session::setTaxChargesName, record, JOIN_TAX_CHARGES_NAME, fields);
            setValue(session::setSpaceName, record, JOIN_SPACE_NAME, fields);
        }
        return session;
    }

    private SessionRecord buildFieldsThatMustBePresent(Record record) {
        SessionRecord session = record.into(sesion.fields()).into(SessionRecord.class);
        session.setEventPackType(record.field(JOIN_EVENT_PACK_TYPE) != null ? record.getValue(JOIN_EVENT_PACK_TYPE) : (byte) 0);
        return session;
    }

    private Integer processSessionShard(List<CpanelPoliticasShardRecord> shards) {
        String shard = DEFAULT_SESSION_SHARD;
        if (!CommonUtils.isEmpty(shards)) {
            CpanelPoliticasShardRecord eventShard = shards.stream().filter(s -> s.getIdevento() != null).findFirst().orElse(null);
            if (eventShard != null) {
                shard = eventShard.getShard();
            } else {
                CpanelPoliticasShardRecord entityShard = shards.stream().filter(s -> s.getIdentidad() != null).findFirst().orElse(null);
                if (entityShard != null) {
                    shard = entityShard.getShard();
                } else {
                    CpanelPoliticasShardRecord operatorShard = shards.stream().filter(s -> s.getIdoperadora() != null).findFirst().orElse(null);
                    if (operatorShard != null) {
                        shard = operatorShard.getShard();
                    }
                }
            }
        }
        return Integer.valueOf(shard.split("_")[1]);
    }

    private boolean containsJoinFields(SelectFieldOrAsterisk[] fields) {
        if (fields.length > sesion.fields().length) {
            return true;
        }
        for (SelectFieldOrAsterisk field : fields) {
            for (SelectFieldOrAsterisk joinField : JOIN_FIELDS) {
                if (field.equals(joinField)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T> void setValue(Consumer<T> target, Record record, Field<T> field, SelectFieldOrAsterisk[] selectedFields) {
        for (SelectFieldOrAsterisk selectedField : selectedFields) {
            if (field.equals(selectedField)) {
                target.accept(record.getValue(field));
            } else if(field.toString().replace("\"", "").equals(selectedField.toString().replace("\"", ""))) {
                target.accept((T) record.getValue(field.toString().replace("\"", "")));
            }
        }
    }

    public List<CpanelSesionRecord> getSessionsInfo(List<Integer> ids) {
        return dsl.select(sesion.IDSESION, sesion.NOMBRE)
                .from(sesion)
                .where(sesion.IDSESION.in(ids))
                .fetchInto(CpanelSesionRecord.class);
    }

	public List<SessionRecord> findActiveSessionsWithTemplateByEventId(Integer eventId) {

		SelectFieldOrAsterisk[] fields = {
				sesion.IDSESION,
				sesion.NOMBRE,
				sesion.NUMMAXLOCALIDADESCOMPRA,
				configRecinto.IDCONFIGURACION.as("venueTemplateId")
		};

		return dsl.select(fields)
				.from(sesion)
				.innerJoin(entidadRecintoConfig)
				.on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO))
				.innerJoin(configRecinto)
				.on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
				.where(sesion.IDEVENTO.eq(eventId)
						.and(sesion.ESTADO.in(SessionStatus.READY.getId(), SessionStatus.IN_PROGRESS.getId(),
								SessionStatus.PREVIEW.getId())))
                .fetch(r -> {
                    SessionRecord record = r.into(SessionRecord.class);
                    record.setVenueTemplateId(r.get("venueTemplateId", Integer.class));
                    return record;
                });
    }

    public record VenueTemplateInfo(Integer id, Boolean isGraphical, Integer venueTemplateType,
                                    String venueProviderCode, String venueProviderMinimapCode,
                                    Integer externalVenueConfigId, List<ExternalPlugin> plugins) {
    }

    public Integer getVenueTemplateTypeBySessionId(Integer idsesion) {
        return dsl.select(configRecinto.TIPOPLANTILLA)
                .from(sesion)
                .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .where(sesion.IDSESION.eq(idsesion))
                .fetchOne().into(Integer.class);
    }



    private SessionForCatalogRecord buildSessionsForCatalog(Record r) {
        SessionForCatalogRecord out = r.into(SessionForCatalogRecord.class);
        out.setTicketTaxId(r.get(JOIN_TAX_TICKET_ID, Long.class));
        out.setTicketTaxName(r.get(JOIN_TAX_TICKET_NAME, String.class));
        out.setTicketTaxValue(r.get(JOIN_TAX_TICKET_VALUE, Double.class));
        out.setSurchargesTaxId(r.get(JOIN_TAX_CHARGES_ID, Long.class));
        out.setSurchargesTaxName(r.get(JOIN_TAX_CHARGES_NAME, String.class));
        out.setSurchargesTaxValue(r.get(JOIN_TAX_CHARGES_VALUE, Double.class));
        return out;
    }
}
