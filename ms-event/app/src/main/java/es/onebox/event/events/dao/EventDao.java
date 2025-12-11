package es.onebox.event.events.dao;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityState;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.VenueStatusDTO;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sorting.EventField;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEntidadAdminEntidades;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelGira;
import es.onebox.jooq.cpanel.tables.CpanelModeloTicket;
import es.onebox.jooq.cpanel.tables.CpanelPromotor;
import es.onebox.jooq.cpanel.tables.CpanelRecinto;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaBase;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaPropia;
import es.onebox.jooq.cpanel.tables.CpanelTimeZoneGroup;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO_CANAL;

@Repository
public class EventDao extends DaoImpl<CpanelEventoRecord, Integer> {


    protected EventDao() {
        super(CPANEL_EVENTO);
    }

    private static final String EVENT_TEMPLATE_CACHE_KEY = "eventTemplate";

    private static final CpanelEvento evento = CPANEL_EVENTO.as("evento");
    private static final CpanelEntidad entity = Tables.CPANEL_ENTIDAD.as("entity");
    private static final CpanelEntidadAdminEntidades entityAdmin = Tables.CPANEL_ENTIDAD_ADMIN_ENTIDADES.as("entityAdmin");
    private static final CpanelEntidadRecintoConfig entidadRecintoConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG.as("entidadRecintoConfig");
    private static final CpanelConfigRecinto configRecinto = Tables.CPANEL_CONFIG_RECINTO.as("configRecinto");
    private static final CpanelRecinto recinto = Tables.CPANEL_RECINTO.as("recinto");
    private static final CpanelTimeZoneGroup startDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("startDateTZ");
    private static final CpanelTimeZoneGroup endDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("endDateTZ");
    private static final CpanelSesion sesion = Tables.CPANEL_SESION.as("sesion");
    private static final CpanelTaxonomiaBase taxonomiaBase = Tables.CPANEL_TAXONOMIA_BASE.as("taxonomiaBase");
    private static final CpanelTaxonomiaPropia taxonomiaPropia = Tables.CPANEL_TAXONOMIA_PROPIA.as("taxonomiaPropia");
    private static final CpanelGira gira = CpanelGira.CPANEL_GIRA.as("gira");
    private static final CpanelPromotor promotor = CpanelPromotor.CPANEL_PROMOTOR.as("promotor");

    private static final Field<String> JOIN_ENTITY_NAME = entity.NOMBRE.as("entityName");
    private static final Field<String> JOIN_PROMOTER_NAME = promotor.NOMBRE.as("promoterName");
    private static final Field<Byte> JOIN_PROMOTER_SIMPLIFIED_INVOICE = promotor.USESIMPLIFIEDINVOICE.as("useSimplifiedInvoice");
    private static final Field<String> JOIN_TOUR_NAME = gira.NOMBRE.as("tourName");
    private static final Field<Integer> JOIN_OPERATOR_ID = entity.IDOPERADORA.as("operatorId");
    private static final Field<String> JOIN_START_TZ = startDateTZ.OLSONID.as("startTZ");
    private static final Field<String> JOIN_START_TZ_DESC = startDateTZ.DISPLAYNAME.as("startTZDesc");
    private static final Field<Integer> JOIN_START_TZ_OFFSET = startDateTZ.RAWOFFSETMINS.as("startTZOffset");
    private static final Field<String> JOIN_END_TZ = endDateTZ.OLSONID.as("endTZ");
    private static final Field<String> JOIN_END_TZ_DESC = endDateTZ.DISPLAYNAME.as("endTZDesc");
    private static final Field<Integer> JOIN_END_TZ_OFFSET = endDateTZ.RAWOFFSETMINS.as("endTZOffset");
    private static final Field<String> JOIN_CATEGORY_DESC = taxonomiaBase.DESCRIPCION.as("categoryDesc");
    private static final Field<String> JOIN_CATEGORY_CODE = taxonomiaBase.CODIGO.as("categoryCode");
    private static final Field<String> JOIN_CUSTOM_CATEGORY_DESC = taxonomiaPropia.DESCRIPCION.as("customCategoryDesc");
    private static final Field<String> JOIN_CUSTOM_CATEGORY_REF = taxonomiaPropia.REFERENCIA.as("customCategoryRef");
    private static final Field<Integer> JOIN_VENUE_CONFIG_ID = configRecinto.IDCONFIGURACION.as("venueConfigId");
    private static final Field<Integer> JOIN_VENUE_CONFIG_TYPE = configRecinto.TIPOPLANTILLA.as("venueConfigType");
    private static final Field<String> JOIN_VENUE_CONFIG_NAME = configRecinto.NOMBRECONFIGURACION.as("venueConfigName");
    private static final Field<Integer> JOIN_VENUE_ID = recinto.IDRECINTO.as("venueId");
    private static final Field<String> JOIN_VENUE_NAME = recinto.NOMBRE.as("venueName");
    private static final Field<String> JOIN_VENUE_GOOGLE_PLACE_ID = recinto.GOOGLEPLACEID.as("googlePlaceId");
    private static final Field<Integer> JOIN_VENUE_COUNTRY_ID = recinto.PAIS.as("venueCountryId");
    private static final Field<String> JOIN_VENUE_CITY = recinto.MUNICIPIO.as("venueCity");

    public static final SelectFieldOrAsterisk[] JOIN_DATE_FIELDS = {
            JOIN_START_TZ,
            JOIN_START_TZ_DESC,
            JOIN_START_TZ_OFFSET,
            JOIN_END_TZ,
            JOIN_END_TZ_DESC,
            JOIN_END_TZ_OFFSET};

    public static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_ENTITY_NAME,
            JOIN_OPERATOR_ID,
            JOIN_CATEGORY_DESC,
            JOIN_CATEGORY_CODE,
            JOIN_CUSTOM_CATEGORY_DESC,
            JOIN_CUSTOM_CATEGORY_REF,
            JOIN_TOUR_NAME,
            JOIN_PROMOTER_NAME,
            JOIN_PROMOTER_SIMPLIFIED_INVOICE};

    public Map<EventRecord, List<VenueRecord>> findEvents(EventSearchFilter filter) {
        SelectFieldOrAsterisk[] fields = buildFields(filter);

        SelectJoinStep<Record> query = dsl.select(fields).from(evento);
        buildJoinClauses(query, filter, fields);

        query.where(builderWhereClause(filter));

        query.orderBy(SortUtils.buildSort(filter.getSort(), EventField::byName));
        query.groupBy(evento.IDEVENTO);
        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        //Get paginated events
        List<EventRecord> events = query.fetch(r -> buildEventRecord(r, fields.length));

        Map<Integer, List<VenueRecord>> eventVenues = new HashMap<>();
        if (CollectionUtils.isEmpty(filter.getFields())) {
            //Get venue configs for obtained events
            eventVenues = dsl.select(evento.IDEVENTO,
                            JOIN_VENUE_CONFIG_ID,
                            JOIN_VENUE_CONFIG_NAME,
                            JOIN_VENUE_ID,
                            JOIN_VENUE_NAME,
                            JOIN_VENUE_GOOGLE_PLACE_ID,
                            JOIN_VENUE_COUNTRY_ID,
                            JOIN_VENUE_CONFIG_TYPE,
                            JOIN_VENUE_CITY)
                    .from(evento)
                    .leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO))
                    .leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                    .where(evento.IDEVENTO.in(events.stream().map(EventRecord::getIdevento).collect(Collectors.toList())))
                    .and(configRecinto.ESTADO.ne(VenueStatusDTO.DELETED.getId()))
                    .fetchGroups(r -> r.get(evento.IDEVENTO),
                            r -> buildVenueRecord(r, fields.length));
        }


        //Build response mixing events with its venue configs
        Map<EventRecord, List<VenueRecord>> eventWithVenueConfigs = new LinkedHashMap<>();
        for (EventRecord event : events) {
            eventWithVenueConfigs.put(event, eventVenues.get(event.getIdevento()));
        }
        return eventWithVenueConfigs;
    }

    private void buildJoinClauses(SelectJoinStep<Record> query, EventSearchFilter filter, SelectFieldOrAsterisk[] fields) {
        query.join(entity).on(entity.IDENTIDAD.eq(evento.IDENTIDAD));
        if (containsJoinFields(fields, JOIN_DATE_FIELDS)) {
            query.leftJoin(startDateTZ).on(startDateTZ.ZONEID.eq(evento.FECHAINICIOTZ));
            query.leftJoin(endDateTZ).on(endDateTZ.ZONEID.eq(evento.FECHAFINTZ));
        }
        if (containsJoinFields(fields, JOIN_FIELDS)) {
            query.leftJoin(promotor).on(promotor.IDPROMOTOR.eq(evento.IDPROMOTOR));
            query.leftJoin(gira).on(gira.IDGIRA.eq(evento.IDGIRA));
            query.leftJoin(taxonomiaBase).on(taxonomiaBase.IDTAXONOMIA.eq(evento.IDTAXONOMIA));
            query.leftJoin(taxonomiaPropia).on(taxonomiaPropia.IDTAXONOMIA.eq(evento.IDTAXONOMIAPROPIA));
        }
        if (filter.getEntityAdminId() != null) {
            query.leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(evento.IDENTIDAD));
        }
        if (hasSessionFilters(filter)) {
            query.leftJoin(sesion).on(sesion.IDEVENTO.eq(evento.IDEVENTO));
        }
        if (hasVenueFilters(filter)) {
            query.leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO));
            query.leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO));
        }
    }

    private static boolean containsJoinFields(SelectFieldOrAsterisk[] fields, SelectFieldOrAsterisk[] fieldsGroup) {
        if (fields.length > evento.fields().length) {
            return true;
        }
        if (fieldsGroup != null) {
            for (SelectFieldOrAsterisk field : fields) {
                for (SelectFieldOrAsterisk joinField : fieldsGroup) {
                    if (field.equals(joinField)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasSessionFilters(EventSearchFilter filter) {
        return ObjectUtils.anyNotNull(filter.getSessionStatus(), filter.getSessionStartDate(), filter.getSessionEndDate());
    }


    private boolean hasVenueFilters(EventSearchFilter filter) {
        return filter.getVenueId() != null ||
                filter.getVenueConfigId() != null ||
                filter.getVenueEntityId() != null ||
                filter.getCountryId() != null ||
                filter.getCity() != null ||
                filter.getVenueTemplateIds() != null;
    }

    public int updateField(final Integer eventId, final Field field, final Object value) {
        return dsl.update(Tables.CPANEL_EVENTO).set(field, value).where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId)).execute();
    }

    @Cached(key = EVENT_TEMPLATE_CACHE_KEY)
    public CpanelConfigRecintoRecord getEventVenueTemplate(@CachedArg Long eventId,
                                                           @CachedArg Long venueConfigId,
                                                           @CachedArg Long venueEntityConfigId) {
        SelectConditionStep<Record> query = dsl.select(configRecinto.fields())
                .from(evento)
                .leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO))
                .leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .leftJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                .where(evento.IDEVENTO.eq(eventId.intValue()));

        if (venueConfigId != null) {
            query.and(configRecinto.IDCONFIGURACION.eq(venueConfigId.intValue()));
        } else if (venueEntityConfigId != null) {
            query.and(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(venueEntityConfigId.intValue()));
        }

        return CommonUtils.ifNotNull(query.fetchOne(), item -> item.into(CpanelConfigRecintoRecord.class));
    }

    public Map.Entry<EventRecord, List<VenueRecord>> findEvent(Long eventId) {
        EventSearchFilter filter = new EventSearchFilter();
        filter.setId(Arrays.asList(eventId));
        filter.setIncludeArchived(true);
        filter.setIncludeSeasonTickets(true);
        Map<EventRecord, List<VenueRecord>> events = findEvents(filter);
        if (events == null || events.size() != 1) {
            return null;
        }
        return events.entrySet().iterator().next();
    }

    public Long countByFilter(EventSearchFilter filter) {
        SelectOnConditionStep<Record1<Integer>> query = dsl.select(DSL.countDistinct(evento.IDEVENTO))
                .from(evento)
                .join(entity).on(entity.IDENTIDAD.eq(evento.IDENTIDAD))
                .leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(evento.IDENTIDAD));

        if (hasSessionFilters(filter)) {
            query.leftJoin(sesion).on(sesion.IDEVENTO.eq(evento.IDEVENTO));
        }
        if (hasVenueFilters(filter)) {
            query.leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO));
            query.leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO));
        }

        return query
                .where(builderWhereClause(filter))
                .fetchOne(0, long.class);
    }

    public List<Long> getFinalizedEventIds() {
        return dsl.select(evento.IDEVENTO).from(evento).where(evento.ESTADO.eq(7)).fetchInto(Long.class);
    }

    public int archiveEvents(Set<Long> eventsToArchive) {
        return dsl.update(evento).set(evento.ARCHIVADO, (byte) 1).where(evento.IDEVENTO.in(eventsToArchive)).execute();
    }

    public int updateEventDatesFromSessionCriteria(Long eventId) {
        Condition conditions = sesion.IDEVENTO.eq(eventId.intValue()).and(sesion.ESTADO.ne(SessionStatus.DELETED.getId()));

        ArrayList<Pair<TableField<CpanelSesionRecord, Timestamp>, String>> updateDatePairs = new ArrayList<>();
        updateDatePairs.add(Pair.of(sesion.FECHAINICIOSESION, "min"));
        updateDatePairs.add(Pair.of(sesion.FECHAFINSESION, "max"));
        updateDatePairs.add(Pair.of(sesion.FECHAINICIORESERVA, "min"));
        updateDatePairs.add(Pair.of(sesion.FECHAFINRESERVA, "max"));
        updateDatePairs.add(Pair.of(sesion.FECHAPUBLICACION, "min"));
        updateDatePairs.add(Pair.of(sesion.FECHAVENTA, "min"));

        SelectJoinStep<Record> updateDatesSelect = dsl.select().from("(SELECT 1 a) a");
        updateDatePairs.stream()
                .map(pair -> generateDateFieldQuery(pair, conditions))
                .forEach(join -> updateDatesSelect.leftJoin(join).on("1=1"));

        final CpanelEventoRecord updateDates = updateDatesSelect.fetchOneInto(CpanelEventoRecord.class);

        return dsl.update(evento)
                .set(evento.FECHAINICIO, updateDates.getFechainicio())
                .set(evento.FECHAINICIOTZ, updateDates.getFechainiciotz())
                .set(evento.FECHAFIN, updateDates.getFechafin())
                .set(evento.FECHAFINTZ, updateDates.getFechafintz())
                .set(evento.FECHAINICIORESERVA, updateDates.getFechainicioreserva())
                .set(evento.FECHAINICIORESERVATZ, updateDates.getFechainicioreservatz())
                .set(evento.FECHAFINRESERVA, updateDates.getFechafinreserva())
                .set(evento.FECHAFINRESERVATZ, updateDates.getFechafinreservatz())
                .set(evento.FECHAPUBLICACION, updateDates.getFechapublicacion())
                .set(evento.FECHAPUBLICACIONTZ, updateDates.getFechapublicaciontz())
                .set(evento.FECHAVENTA, updateDates.getFechaventa())
                .set(evento.FECHAVENTATZ, updateDates.getFechaventatz())
                .where(evento.IDEVENTO.eq(eventId.intValue())).execute();
    }


    public List<TicketTemplateRecord> getTicketTemplates(int eventId) {
        return dsl.select(
                        Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA,
                        Tables.CPANEL_PLANTILLA_TICKET.NOMBRE,
                        Tables.CPANEL_MODELO_TICKET.FORMATO)
                .from(CpanelEvento.CPANEL_EVENTO)
                .innerJoin(Tables.CPANEL_PLANTILLA_TICKET)
                .on(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKET.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA)
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETGRUPOS.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETINVITACION.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETINVITACIONGRUPOS.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETTAQUILLA.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETTAQUILLAGRUPOS.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETTAQUILLAINVITACION.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA))
                        .or(CpanelEvento.CPANEL_EVENTO.IDPLANTILLATICKETTAQUILLAINVITACIONGRUPOS.eq(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA)))
                .innerJoin(Tables.CPANEL_MODELO_TICKET).on(CpanelModeloTicket.CPANEL_MODELO_TICKET.IDMODELO.eq(Tables.CPANEL_PLANTILLA_TICKET.IDMODELO))
                .where(CpanelEvento.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .fetch()
                .map(record -> {
                    TicketTemplateRecord ticketTemplateRecord = new TicketTemplateRecord();
                    ticketTemplateRecord.setIdplantilla(record.get(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA));
                    ticketTemplateRecord.setNombre(record.get(Tables.CPANEL_PLANTILLA_TICKET.NOMBRE));
                    ticketTemplateRecord.setModelFormat(record.get(Tables.CPANEL_MODELO_TICKET.FORMATO).intValue());
                    return ticketTemplateRecord;
                });
    }

    public Boolean existEventById(Long eventId) {
        return dsl.fetchExists(
                dsl.selectOne().from(evento)
                        .where(evento.IDEVENTO.eq(eventId.intValue()))
                        .and(evento.ESTADO.ne(EventStatus.DELETED.getId()))
                        .and(evento.ARCHIVADO.ne((byte) 1)));
    }

    private static EventRecord buildEventRecord(Record record, int fields) {
        EventRecord event = record.into(EventRecord.class);

        //Add join fields only if has been added to base event fields
        if (fields > evento.fields().length) {
            event.setEntityName(record.getValue(JOIN_ENTITY_NAME));
            event.setOperatorId(record.getValue(JOIN_OPERATOR_ID));
            event.setPromoterName(record.getValue(JOIN_PROMOTER_NAME));
            event.setUseSimplifiedInvoice(record.getValue(JOIN_PROMOTER_SIMPLIFIED_INVOICE));
            event.setTourName(record.getValue(JOIN_TOUR_NAME));
            event.setStartDateTZ(record.getValue(JOIN_START_TZ));
            event.setStartDateTZDesc(record.getValue(JOIN_START_TZ_DESC));
            event.setStartDateTZOffset(record.getValue(JOIN_START_TZ_OFFSET));
            event.setEndDateTZ(record.getValue(JOIN_END_TZ));
            event.setEndDateTZDesc(record.getValue(JOIN_END_TZ_DESC));
            event.setEndDateTZOffset(record.getValue(JOIN_END_TZ_OFFSET));
            event.setCategoryDescription(record.getValue(JOIN_CATEGORY_DESC));
            event.setCategoryCode(record.getValue(JOIN_CATEGORY_CODE));
            event.setCustomCategoryDescription(record.getValue(JOIN_CUSTOM_CATEGORY_DESC));
            event.setCustomCategoryRef(record.getValue(JOIN_CUSTOM_CATEGORY_REF));
        } else {
            if (record.indexOf(JOIN_ENTITY_NAME) > 0) {
                event.setEntityName(record.getValue(JOIN_ENTITY_NAME));
            }
            if (record.indexOf(JOIN_START_TZ) > 0) {
                event.setStartDateTZ(record.getValue(JOIN_START_TZ));
            }
            if (record.indexOf(JOIN_END_TZ) > 0) {
                event.setEndDateTZ(record.getValue(JOIN_END_TZ));
            }
        }


        return event;
    }

    private static VenueRecord buildVenueRecord(Record record, int fields) {
        VenueRecord venue = new VenueRecord();
        if (fields > evento.fields().length && record.getValue(JOIN_VENUE_CONFIG_ID) != null) {
            venue.setVenueConfigId(record.getValue(JOIN_VENUE_CONFIG_ID).longValue());
            venue.setVenueConfigName(record.getValue(JOIN_VENUE_CONFIG_NAME));
            venue.setIdrecinto(record.getValue(JOIN_VENUE_ID));
            venue.setNombre(record.getValue(JOIN_VENUE_NAME));
            venue.setGoogleplaceid(record.getValue(JOIN_VENUE_GOOGLE_PLACE_ID));
            venue.setMunicipio(record.getValue(JOIN_VENUE_CITY));
            venue.setPais(record.getValue(JOIN_VENUE_COUNTRY_ID));
            venue.setVenueConfigType(VenueTemplateType.byId(record.getValue(JOIN_VENUE_CONFIG_TYPE)));
        }
        return venue;
    }

    private Condition builderWhereClause(EventSearchFilter filter) {
        Condition conditions = DSL.trueCondition();

        //Event/Operator/Entity filter first due to performance issues
        if (filter.getId() != null) {
            conditions = conditions.and(evento.IDEVENTO.in(filter.getId()));
        }
        if (filter.getOperatorId() != null) {
            conditions = conditions.and(entity.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        }
        if (filter.getEntityAdminId() != null) {
            conditions = conditions.and(entityAdmin.IDENTIDADADMIN.eq(filter.getEntityAdminId().intValue()));
        }
        if (filter.getEntityId() != null) {
            conditions = conditions.and(evento.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }

        //Status filtering
        conditions = conditions.and(evento.ESTADO.ne(EventStatus.DELETED.getId()));
        conditions = conditions.and(entity.ESTADO.ne(EntityState.DELETED.getState()));
        if (filter.getStatus() != null) {
            List<Integer> status = filter.getStatus().stream().map(EventStatus::getId).collect(Collectors.toList());
            conditions = conditions.and(evento.ESTADO.in(status));
        }
        if (filter.getSessionStatus() != null) {
            List<Integer> sessionStatus = filter.getSessionStatus().stream().map(SessionStatus::getId).collect(Collectors.toList());
            conditions = conditions.and(sesion.ESTADO.in(sessionStatus));
        }
        if (!CommonUtils.isTrue(filter.getIncludeArchived())) {
            conditions = conditions.and(evento.ARCHIVADO.notEqual((byte) 1));
        }

        //Custom filters
        if (BooleanUtils.isNotTrue(filter.getIncludeSeasonTickets())) {
            conditions = conditions.and(evento.TIPOEVENTO.ne(EventType.SEASON_TICKET.getId()));
        }
        if (filter.getType() != null) {
            List<Integer> types = filter.getType().stream().map(EventType::getId).collect(Collectors.toList());
            conditions = conditions.and(evento.TIPOEVENTO.in(types));
        }
        if (filter.getName() != null) {
            conditions = conditions.and(evento.NOMBRE.eq(filter.getName()));
        }
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            String freeSearch = filter.getFreeSearch();
            conditions = conditions.and(evento.NOMBRE.like("%" + freeSearch + "%"));
        }
        if (filter.getExternalReference() != null) {
            conditions = conditions.and(evento.REFERENCIAPROMOTOR.eq(filter.getExternalReference()));
        }
        if (filter.getProducerId() != null) {
            conditions = conditions.and(evento.IDPROMOTOR.eq(filter.getProducerId().intValue()));
        }
        if (filter.getAvetCompetitions() != null) {
            conditions = conditions.and(evento.IDEXTERNO.in(filter.getAvetCompetitions()));
        }
        if (filter.getCurrencyId() != null) {
            conditions = conditions.and(evento.IDCURRENCY.eq(filter.getCurrencyId().intValue()));
        }

        conditions = addDatesConditions(filter, conditions);
        conditions = buildVenueJoinFilters(filter, conditions);

        return conditions;
    }

    private Condition addDatesConditions(EventSearchFilter filter, Condition conditions) {
        if (!CommonUtils.isEmpty(filter.getStartDate())) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : filter.getStartDate()) {
                conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, startDateFilter, evento.FECHAINICIO, Operator.AND, null);
            }
        }
        if (!CommonUtils.isEmpty(filter.getEndDate())) {
            for (FilterWithOperator<ZonedDateTime> endDateFilter : filter.getEndDate()) {
                conditions = JooqUtils.filterDateWithOperatorToCondition(conditions, endDateFilter, evento.FECHAFIN, Operator.AND, null);
            }
        }
        if (filter.getSessionStartDate() != null && filter.getSessionEndDate() != null) {
            Condition orCondition = DSL.or(
                    sesion.FECHAREALFINSESION.isNull()
                            .and(sesion.FECHAINICIOSESION.ge(Timestamp.valueOf(filter.getSessionStartDate().toLocalDateTime())))
                            .and(sesion.FECHAINICIOSESION.le(Timestamp.valueOf(filter.getSessionEndDate().toLocalDateTime()))),
                    sesion.FECHAREALFINSESION.isNotNull()
                            .and(sesion.FECHAREALFINSESION.ge(Timestamp.valueOf(filter.getSessionStartDate().toLocalDateTime())))
                            .and(sesion.FECHAINICIOSESION.le(Timestamp.valueOf(filter.getSessionEndDate().toLocalDateTime()))));

            conditions = conditions.and(orCondition);
        } else if (filter.getSessionStartDate() != null) {
            Condition orCondition = DSL.or(
                    sesion.FECHAREALFINSESION.isNotNull()
                            .and(sesion.FECHAREALFINSESION.ge(Timestamp.valueOf(filter.getSessionStartDate().toLocalDateTime()))),
                    sesion.FECHAINICIOSESION.ge(Timestamp.valueOf(filter.getSessionStartDate().toLocalDateTime())));
            conditions = conditions.and(orCondition);
        } else if (filter.getSessionEndDate() != null) {
            conditions = conditions.and(sesion.FECHAINICIOSESION.le(Timestamp.valueOf(filter.getSessionEndDate().toLocalDateTime())));
        }

        return conditions;
    }

    private Condition buildVenueJoinFilters(EventSearchFilter filter, Condition conditions) {
        if (filter.getVenueId() != null) {
            conditions = conditions.and(configRecinto.IDRECINTO.in(filter.getVenueId()));
        }
        if (filter.getVenueConfigId() != null) {
            conditions = conditions.and(configRecinto.IDCONFIGURACION.eq(filter.getVenueConfigId().intValue()));
        }
        if (filter.getVenueTemplateIds() != null) {
            conditions = conditions.and(configRecinto.IDCONFIGURACION.in(filter.getVenueTemplateIds()));
        }
        if (filter.getVenueEntityId() != null) {
            conditions = conditions.and(recinto.IDENTIDAD.eq(filter.getVenueEntityId().intValue()));
        }
        if (filter.getCountryId() != null) {
            conditions = conditions.and(recinto.PAIS.eq(filter.getCountryId().intValue()));
            conditions = conditions.and(configRecinto.ESTADO.ne(VenueStatusDTO.DELETED.getId()));
        }
        if (filter.getCity() != null) {
            conditions = conditions.and(recinto.MUNICIPIO.eq(filter.getCity()));
            conditions = conditions.and(configRecinto.ESTADO.ne(VenueStatusDTO.DELETED.getId()));
        }

        return conditions;
    }

    private SelectFieldOrAsterisk[] buildFields(EventSearchFilter filter) {
        if (filter == null || CommonUtils.isEmpty(filter.getFields())) {
            SelectFieldOrAsterisk[] joinFields = ArrayUtils.addAll(JOIN_DATE_FIELDS, JOIN_FIELDS);
            return ArrayUtils.addAll(evento.fields(), joinFields);
        }
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = buildFilteredFields(filter.getFields());
        return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
    }

    private Set<SelectFieldOrAsterisk> buildFilteredFields(List<String> fields) {
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = new HashSet<>();
        selectFieldOrAsterisks.add(evento.IDEVENTO);
        for (String field : fields) {
            Field dbField = EventField.byName(field);
            if (dbField != null) {
                selectFieldOrAsterisks.add(dbField);
            }
        }
        return selectFieldOrAsterisks;
    }

    private Table<Record2<Timestamp, Integer>> generateDateFieldQuery(Pair<TableField<CpanelSesionRecord, Timestamp>, String> datePair, Condition condition) {
        TableField<CpanelSesionRecord, Timestamp> dateField = datePair.getKey();
        String sortMethod = datePair.getValue();
        // .as normalizes column names so it can be fetched into a CpanelEventoRecord object.
        return dsl.select(dateField.as(dateField.getName().replace("Sesion", "")),
                        recinto.TIMEZONE.as((dateField.getName() + "TZ").replace("Sesion", "")))
                .from(sesion)
                .join(entidadRecintoConfig).on(sesion.IDRELACIONENTIDADRECINTO.eq(entidadRecintoConfig.IDRELACIONENTRECINTO))
                .join(configRecinto).on(entidadRecintoConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                .join(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .where(condition).and(dateField.isNotNull())
                .orderBy("max".equals(sortMethod) ? dateField.desc() : dateField.asc(),
                        "max".equals(sortMethod) ? recinto.TIMEZONE.desc() : recinto.TIMEZONE.asc())
                .limit(1).asTable()
                .as("sub_" + dateField.getName());
    }

    public Long getCurrencyIdByChannel(Long saleRequestId) {
        return dsl.select(CPANEL_EVENTO.IDCURRENCY)
                .from(CPANEL_EVENTO)
                .join(CPANEL_EVENTO_CANAL)
                .on(CPANEL_EVENTO.IDEVENTO
                        .eq(CPANEL_EVENTO_CANAL.IDEVENTO))
                .where(CPANEL_EVENTO_CANAL.IDEVENTOCANAL
                        .eq(saleRequestId.intValue()))
                .fetchOne(0, Long.class);
    }
}
