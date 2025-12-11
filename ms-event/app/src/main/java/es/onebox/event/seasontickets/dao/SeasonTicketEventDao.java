package es.onebox.event.seasontickets.dao;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.seasontickets.converter.SeasonTicketStatusConverter;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sorting.EventField;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEntidadAdminEntidades;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelGira;
import es.onebox.jooq.cpanel.tables.CpanelPromotor;
import es.onebox.jooq.cpanel.tables.CpanelRecinto;
import es.onebox.jooq.cpanel.tables.CpanelSeasonTicket;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaBase;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaPropia;
import es.onebox.jooq.cpanel.tables.CpanelTimeZoneGroup;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SEASON_TICKET;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;


@Repository
public class SeasonTicketEventDao extends DaoImpl<CpanelEventoRecord, Integer> {

    private static final CpanelEvento evento = CPANEL_EVENTO.as("evento");
    private static final CpanelEntidadAdminEntidades entityAdmin = Tables.CPANEL_ENTIDAD_ADMIN_ENTIDADES.as("entityAdmin");
    private static final CpanelEntidad entity = Tables.CPANEL_ENTIDAD.as("entity");
    private static final CpanelConfigRecinto configRecinto = Tables.CPANEL_CONFIG_RECINTO.as("configRecinto");
    private static final CpanelRecinto recinto = Tables.CPANEL_RECINTO.as("recinto");
    private static final CpanelTimeZoneGroup startDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("startDateTZ");
    private static final CpanelTimeZoneGroup endDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("endDateTZ");
    private static final CpanelTaxonomiaBase taxonomiaBase = Tables.CPANEL_TAXONOMIA_BASE.as("taxonomiaBase");
    private static final CpanelTaxonomiaPropia taxonomiaPropia = Tables.CPANEL_TAXONOMIA_PROPIA.as("taxonomiaPropia");
    private static final CpanelGira gira = CpanelGira.CPANEL_GIRA.as("gira");
    private static final CpanelPromotor promotor = CpanelPromotor.CPANEL_PROMOTOR.as("promotor");
    private static final CpanelSesion sesion = CPANEL_SESION.as("sesion");
    private static final CpanelSeasonTicket seasonTicket = CPANEL_SEASON_TICKET.as("seasonTicket");

    private static final Field<String> JOIN_ENTITY_NAME = entity.NOMBRE.as("entityName");
    private static final Field<String> JOIN_PROMOTER_NAME = promotor.NOMBRE.as("promoterName");
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
    private static final Field<String> JOIN_VENUE_CONFIG_NAME = configRecinto.NOMBRECONFIGURACION.as("venueConfigName");
    private static final Field<Integer> JOIN_VENUE_ID = recinto.IDRECINTO.as("venueId");
    private static final Field<String> JOIN_VENUE_NAME = recinto.NOMBRE.as("venueName");
    private static final Field<Integer> JOIN_VENUE_COUNTRY_ID = recinto.PAIS.as("venueCountryId");
    private static final Field<String> JOIN_VENUE_CITY = recinto.MUNICIPIO.as("venueCity");
    private static final Field<Boolean> JOIN_SEASON_TICKET_MEMBER_MANDATORY = seasonTicket.ISMEMBERMANDATORY.as("isMemberMandatory");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ALLOW_RENEWAL = seasonTicket.ALLOWRENEWAL.as("allowRenewal");
    private static final Field<Timestamp> JOIN_SEASON_TICKET_RENEWAL_INIT_DATE = seasonTicket.RENEWALINITDATE.as("renewalInitDate");
    private static final Field<Timestamp> JOIN_SEASON_TICKET_RENEWAL_END_DATE = seasonTicket.RENEWALENDDATE.as("renewalEndDate");
    private static final Field<Boolean> JOIN_SEASON_TICKET_RENEWAL_ENABLED = seasonTicket.RENEWALENABLED.as("renewalEnabled");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ALLOW_CHANGE_SEAT = seasonTicket.ALLOWCHANGESEAT.as("allowChangeSeat");
    private static final Field<Boolean> JOIN_SEASON_TICKET_CHANGE_SEAT_ENABLED = seasonTicket.CHANGESEATENABLED.as("changeSeatEnabled");
    private static final Field<Timestamp> JOIN_SEASON_TICKET_CHANGE_SEAT_INIT_DATE = seasonTicket.CHANGESEATINITDATE.as("changeSeatInitDate");
    private static final Field<Timestamp> JOIN_SEASON_TICKET_CHANGE_SEAT_END_DATE = seasonTicket.CHANGESEATENDDATE.as("changeSeatEndDate");
    private static final Field<Boolean> JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE_ENABLED = seasonTicket.MAXCHANGESEATVALUEENABLED.as("maxChangeSeatValueEnabled");
    private static final Field<Integer> JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE = seasonTicket.MAXCHANGESEATVALUE.as("maxChangeSeatValue");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ENABLE_CHANGED_SEAT_QUOTA = seasonTicket.ENABLECHANGEDSEATQUOTA.as("enableChangedSeatQuota");
    private static final Field<Integer> JOIN_SEASON_TICKET_CHANGED_SEAT_QUOTA_ID = seasonTicket.CHANGEDSEATQUOTAID.as("changedSeatQuotaId");
    private static final Field<Integer> JOIN_SEASON_TICKET_CHANGED_SEAT_STATUS = seasonTicket.CHANGEDSEATSTATUS.as("changedSeatStatus");
    private static final Field<Integer> JOIN_SEASON_TICKET_CHANGED_SEAT_BLOCK_REASON_ID = seasonTicket.CHANGEDSEATBLOCKREASONID.as("changedSeatBlockReasonId");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ALLOW_TRANSFER_TICKET = seasonTicket.ALLOWTRANSFERTICKET.as("allowTransferTicket");
    private static final Field<Integer> JOIN_SEASON_TICKET_TRANSFER_TICKET_MAX_DELAY_TIME = seasonTicket.TRANSFERTICKETMAXDELAYTIME.as("transferTicketMaxDelayTime");
    private static final Field<Integer> JOIN_SEASON_TICKET_RECOVERY_TICKET_MAX_DELAY_TIME = seasonTicket.RECOVERYTICKETMAXDELAYTIME.as("recoveryTicketMaxDelayTime");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ENABLE_MAX_TICKET_TRANSFERS = seasonTicket.ENABLEMAXTICKETTRANSFERS.as("enableMaxTicketTransfers");
    private static final Field<Integer> JOIN_SEASON_TICKET_MAX_TICKET_TRANSFERS = seasonTicket.MAXTICKETTRANSFERS.as("maxTicketTransfers");
    private static final Field<Boolean> JOIN_SEASON_TICKET_ALLOW_RELEASE_SEAT = seasonTicket.ALLOWRELEASESEAT.as("allowReleaseSeat");
    private static final Field<Integer> JOIN_SEASON_TICKET_TRANSFER_TICKET_MIN_DELAY_TIME = seasonTicket.TRANSFERTICKETMINDELAYTIME.as("transferTicketMinDelayTime");
    private static final Field<Byte> JOIN_SEASON_TICKET_TRANSFER_POLICY = seasonTicket.TRANSFERPOLICY.as("transferPolicy");
    private static final Field<Integer> JOIN_SEASON_TICKET_CUSTOMER_MAX_SEATS = seasonTicket.CUSTOMERMAXSEATS.as("customerMaxSeats");
    private static final Field<Boolean> JOIN_SEASON_TICKET_REGISTER_MANDATORY = seasonTicket.REGISTERMANDATORY.as("registerMandatory");
    private static final Field<Boolean> JOIN_SEASON_TICKET_AUTO_RENEWAL = seasonTicket.AUTORENEWAL.as("autoRenewal");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_ENTITY_NAME,
            JOIN_OPERATOR_ID,
            JOIN_START_TZ,
            JOIN_START_TZ_DESC,
            JOIN_START_TZ_OFFSET,
            JOIN_END_TZ,
            JOIN_END_TZ_DESC,
            JOIN_END_TZ_OFFSET,
            JOIN_CATEGORY_DESC,
            JOIN_CATEGORY_CODE,
            JOIN_CUSTOM_CATEGORY_DESC,
            JOIN_CUSTOM_CATEGORY_REF,
            JOIN_TOUR_NAME,
            JOIN_PROMOTER_NAME,
            JOIN_SEASON_TICKET_MEMBER_MANDATORY,
            JOIN_SEASON_TICKET_ALLOW_RENEWAL,
            JOIN_SEASON_TICKET_RENEWAL_INIT_DATE,
            JOIN_SEASON_TICKET_RENEWAL_END_DATE,
            JOIN_SEASON_TICKET_RENEWAL_ENABLED,
            JOIN_SEASON_TICKET_ALLOW_CHANGE_SEAT,
            JOIN_SEASON_TICKET_CHANGE_SEAT_ENABLED,
            JOIN_SEASON_TICKET_CHANGE_SEAT_INIT_DATE,
            JOIN_SEASON_TICKET_CHANGE_SEAT_END_DATE,
            JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE_ENABLED,
            JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE,
            JOIN_SEASON_TICKET_ENABLE_CHANGED_SEAT_QUOTA,
            JOIN_SEASON_TICKET_CHANGED_SEAT_QUOTA_ID,
            JOIN_SEASON_TICKET_CHANGED_SEAT_STATUS,
            JOIN_SEASON_TICKET_CHANGED_SEAT_BLOCK_REASON_ID,
            JOIN_SEASON_TICKET_ALLOW_TRANSFER_TICKET,
            JOIN_SEASON_TICKET_TRANSFER_TICKET_MAX_DELAY_TIME,
            JOIN_SEASON_TICKET_RECOVERY_TICKET_MAX_DELAY_TIME,
            JOIN_SEASON_TICKET_ENABLE_MAX_TICKET_TRANSFERS,
            JOIN_SEASON_TICKET_MAX_TICKET_TRANSFERS,
            JOIN_SEASON_TICKET_ALLOW_RELEASE_SEAT,
            JOIN_SEASON_TICKET_TRANSFER_TICKET_MIN_DELAY_TIME,
            JOIN_SEASON_TICKET_TRANSFER_POLICY,
            JOIN_SEASON_TICKET_CUSTOMER_MAX_SEATS,
            JOIN_SEASON_TICKET_REGISTER_MANDATORY,
            JOIN_SEASON_TICKET_AUTO_RENEWAL
    };

    public SeasonTicketEventDao() {
        super(CPANEL_EVENTO);
    }

    public Map<EventRecord, List<VenueRecord>> findSeasonTickets(SeasonTicketSearchFilter filter) {
        SelectFieldOrAsterisk[] fields = buildFields(filter);

        SelectJoinStep<Record> query =
                dsl.select(fields)
                        .from(evento)
                        .join(entity).on(entity.IDENTIDAD.eq(evento.IDENTIDAD))
                        .leftJoin(startDateTZ).on(startDateTZ.ZONEID.eq(evento.FECHAINICIOTZ))
                        .leftJoin(endDateTZ).on(endDateTZ.ZONEID.eq(evento.FECHAFINTZ))
                        .leftJoin(taxonomiaBase).on(taxonomiaBase.IDTAXONOMIA.eq(evento.IDTAXONOMIA))
                        .leftJoin(taxonomiaPropia).on(taxonomiaPropia.IDTAXONOMIA.eq(evento.IDTAXONOMIAPROPIA))
                        .leftJoin(gira).on(gira.IDGIRA.eq(evento.IDGIRA))
                        .leftJoin(promotor).on(promotor.IDPROMOTOR.eq(evento.IDPROMOTOR))
                        .leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO))
                        .leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                        .leftJoin(sesion).on(sesion.IDEVENTO.eq(evento.IDEVENTO))
                        .leftJoin(seasonTicket).on(seasonTicket.IDEVENTO.eq(evento.IDEVENTO))
                        .leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(evento.IDENTIDAD));

        query.where(builderWhereClause(filter));

        query.orderBy(SortUtils.buildSort(filter.getSort(), EventField::byName));
        query.groupBy(evento.IDEVENTO);
        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        //Get paginated season tickets
        List<EventRecord> seasonTickets = query.fetch(r -> buildSeasonTicketRecord(r, fields.length));

        //Get venue configs for obtained season tickets
        Map<Integer, List<VenueRecord>> eventVenues = dsl.select(evento.IDEVENTO,
                        JOIN_VENUE_CONFIG_ID,
                        JOIN_VENUE_CONFIG_NAME,
                        JOIN_VENUE_ID,
                        JOIN_VENUE_NAME,
                        JOIN_VENUE_COUNTRY_ID,
                        JOIN_VENUE_CITY)
                .from(evento)
                .leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO))
                .leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .where(evento.IDEVENTO.in(seasonTickets.stream().map(EventRecord::getIdevento).collect(Collectors.toList())))
                .fetchGroups(r -> r.get(evento.IDEVENTO),
                        r -> buildVenueRecord(r, fields.length));

        //Build response mixing season tickets with its venue configs
        Map<EventRecord, List<VenueRecord>> seasonTicketsWithVenueConfigs = new LinkedHashMap<>();
        for (EventRecord seasonTicket : seasonTickets) {
            seasonTicketsWithVenueConfigs.put(seasonTicket, eventVenues.get(seasonTicket.getIdevento()));
        }

        return seasonTicketsWithVenueConfigs;
    }


    public Map.Entry<EventRecord, List<VenueRecord>> findSeasonTicket(Long seasonTicketId) {
        SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();
        filter.setId(seasonTicketId);
        filter.setIncludeArchived(true);
        Map<EventRecord, List<VenueRecord>> seasonTickets = findSeasonTickets(filter);
        if (seasonTickets == null || seasonTickets.size() != 1) {
            return null;
        }
        return seasonTickets.entrySet().iterator().next();
    }

    public Long countByFilter(SeasonTicketSearchFilter filter) {
        return dsl.select(DSL.countDistinct(evento.IDEVENTO))
                .from(evento)
                .join(entity).on(entity.IDENTIDAD.eq(evento.IDENTIDAD))
                .leftJoin(configRecinto).on(configRecinto.IDEVENTO.eq(evento.IDEVENTO))
                .leftJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .leftJoin(sesion).on(sesion.IDEVENTO.eq(evento.IDEVENTO))
                .leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(evento.IDENTIDAD))
                .where(builderWhereClause(filter))
                .fetchOne(0, long.class);
    }

    private EventRecord buildSeasonTicketRecord(Record record, int fields) {
        EventRecord seasonTicket = record.into(EventRecord.class);
        //Add join fields only if has been added to base event fields
        if (fields > evento.fields().length) {
            seasonTicket.setEntityName(record.getValue(JOIN_ENTITY_NAME));
            seasonTicket.setOperatorId(record.getValue(JOIN_OPERATOR_ID));
            seasonTicket.setPromoterName(record.getValue(JOIN_PROMOTER_NAME));
            seasonTicket.setTourName(record.getValue(JOIN_TOUR_NAME));
            seasonTicket.setStartDateTZ(record.getValue(JOIN_START_TZ));
            seasonTicket.setStartDateTZDesc(record.getValue(JOIN_START_TZ_DESC));
            seasonTicket.setStartDateTZOffset(record.getValue(JOIN_START_TZ_OFFSET));
            seasonTicket.setEndDateTZ(record.getValue(JOIN_END_TZ));
            seasonTicket.setEndDateTZDesc(record.getValue(JOIN_END_TZ_DESC));
            seasonTicket.setEndDateTZOffset(record.getValue(JOIN_END_TZ_OFFSET));
            seasonTicket.setCategoryDescription(record.getValue(JOIN_CATEGORY_DESC));
            seasonTicket.setCategoryCode(record.getValue(JOIN_CATEGORY_CODE));
            seasonTicket.setCustomCategoryDescription(record.getValue(JOIN_CUSTOM_CATEGORY_DESC));
            seasonTicket.setCustomCategoryRef(record.getValue(JOIN_CUSTOM_CATEGORY_REF));
            seasonTicket.setMemberMandatory(record.getValue(JOIN_SEASON_TICKET_MEMBER_MANDATORY));
            seasonTicket.setAllowRenewal(record.getValue(JOIN_SEASON_TICKET_ALLOW_RENEWAL));
            seasonTicket.setRenewalStartingDate(record.getValue(JOIN_SEASON_TICKET_RENEWAL_INIT_DATE));
            seasonTicket.setRenewalEndDate(record.getValue(JOIN_SEASON_TICKET_RENEWAL_END_DATE));
            seasonTicket.setRenewalEnabled(record.getValue(JOIN_SEASON_TICKET_RENEWAL_ENABLED));
            seasonTicket.setAllowChangeSeat(record.getValue(JOIN_SEASON_TICKET_ALLOW_CHANGE_SEAT));
            seasonTicket.setChangeSeatEnabled(record.getValue(JOIN_SEASON_TICKET_CHANGE_SEAT_ENABLED));
            seasonTicket.setChangeSeatStartingDate(record.getValue(JOIN_SEASON_TICKET_CHANGE_SEAT_INIT_DATE));
            seasonTicket.setChangeSeatEndDate(record.getValue(JOIN_SEASON_TICKET_CHANGE_SEAT_END_DATE));
            seasonTicket.setMaxChangeSeatValueEnabled(record.getValue(JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE_ENABLED));
            seasonTicket.setMaxChangeSeatValue(record.getValue(JOIN_SEASON_TICKET_MAX_CHANGE_SEAT_VALUE));
            seasonTicket.setEnableChangedSeatQuota(record.getValue(JOIN_SEASON_TICKET_ENABLE_CHANGED_SEAT_QUOTA));
            seasonTicket.setChangedSeatQuotaId(record.getValue(JOIN_SEASON_TICKET_CHANGED_SEAT_QUOTA_ID));
            seasonTicket.setChangedSeatStatus(record.getValue(JOIN_SEASON_TICKET_CHANGED_SEAT_STATUS));
            seasonTicket.setChangedSeatBlockReasonId(record.getValue(JOIN_SEASON_TICKET_CHANGED_SEAT_BLOCK_REASON_ID));
            seasonTicket.setAllowTransferTicket(record.getValue(JOIN_SEASON_TICKET_ALLOW_TRANSFER_TICKET));
            seasonTicket.setTransferTicketMaxDelayTime(record.getValue(JOIN_SEASON_TICKET_TRANSFER_TICKET_MAX_DELAY_TIME));
            seasonTicket.setRecoveryTicketMaxDelayTime(record.getValue(JOIN_SEASON_TICKET_RECOVERY_TICKET_MAX_DELAY_TIME));
            seasonTicket.setEnableMaxTicketTransfers(record.getValue(JOIN_SEASON_TICKET_ENABLE_MAX_TICKET_TRANSFERS));
            seasonTicket.setMaxTicketTransfers(record.getValue(JOIN_SEASON_TICKET_MAX_TICKET_TRANSFERS));
            seasonTicket.setAllowReleaseSeat(record.getValue(JOIN_SEASON_TICKET_ALLOW_RELEASE_SEAT));
            seasonTicket.setCurrencyId(record.getValue(evento.IDCURRENCY));
            seasonTicket.setTransferTicketMinDelayTime(record.getValue(JOIN_SEASON_TICKET_TRANSFER_TICKET_MIN_DELAY_TIME));
            seasonTicket.setSessionTransferPolicy(record.getValue(JOIN_SEASON_TICKET_TRANSFER_POLICY));
            seasonTicket.setCustomerMaxSeats(record.getValue(JOIN_SEASON_TICKET_CUSTOMER_MAX_SEATS));
            seasonTicket.setRegisterMandatory(record.getValue(JOIN_SEASON_TICKET_REGISTER_MANDATORY));
            seasonTicket.setAutoRenewal(record.getValue(JOIN_SEASON_TICKET_AUTO_RENEWAL));
        }
        return seasonTicket;
    }

    private VenueRecord buildVenueRecord(Record record, int fields) {
        VenueRecord venue = new VenueRecord();
        if (fields > evento.fields().length && record.getValue(JOIN_VENUE_CONFIG_ID) != null) {
            venue.setVenueConfigId(record.getValue(JOIN_VENUE_CONFIG_ID).longValue());
            venue.setVenueConfigName(record.getValue(JOIN_VENUE_CONFIG_NAME));
            venue.setIdrecinto(record.getValue(JOIN_VENUE_ID));
            venue.setNombre(record.getValue(JOIN_VENUE_NAME));
            venue.setMunicipio(record.getValue(JOIN_VENUE_CITY));
            venue.setPais(record.getValue(JOIN_VENUE_COUNTRY_ID));
        }
        return venue;
    }

    private Condition builderWhereClause(SeasonTicketSearchFilter filter) {
        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(evento.ESTADO.ne(EventStatus.DELETED.getId()));
        conditions = conditions.and(evento.TIPOEVENTO.eq(EventType.SEASON_TICKET.getId()));
        if (filter.getId() != null) {
            conditions = conditions.and(evento.IDEVENTO.eq(filter.getId().intValue()));
        }
        if (filter.getOperatorId() != null) {
            conditions = conditions.and(entity.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        }
        if (filter.getEntityId() != null) {
            conditions = conditions.and(evento.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }
        if (filter.getEntityAdminId() != null) {
            conditions = conditions.and(entityAdmin.IDENTIDADADMIN.eq(filter.getEntityAdminId().intValue()));
        }
        if (filter.getProducerId() != null) {
            conditions = conditions.and(evento.IDPROMOTOR.eq(filter.getProducerId().intValue()));
        }
        if (filter.getName() != null) {
            conditions = conditions.and(evento.NOMBRE.eq(filter.getName()));
        }
        if (filter.getStatus() != null) {
            conditions = conditions.and(getConditionsFromStatusList(filter.getStatus(), sesion));
        }
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
        if (filter.getCurrencyId() != null) {
            conditions = conditions.and(evento.IDCURRENCY.eq(filter.getCurrencyId()));
        }
        if (!CommonUtils.isTrue(filter.getIncludeArchived())) {
            conditions = conditions.and(evento.ARCHIVADO.notEqual((byte) 1));
        }
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            String freeSearch = filter.getFreeSearch();
            conditions = conditions.and(evento.NOMBRE.like("%" + freeSearch + "%"));
        }

        conditions = buildVenueJoinFilters(filter, conditions);

        return conditions;
    }

    private Condition buildVenueJoinFilters(SeasonTicketSearchFilter filter, Condition conditions) {
        if (filter.getVenueId() != null) {
            conditions = conditions.and(configRecinto.IDRECINTO.eq(filter.getVenueId().intValue()));
        }
        if (filter.getVenueConfigId() != null) {
            conditions = conditions.and(configRecinto.IDCONFIGURACION.eq(filter.getVenueConfigId().intValue()));
        }
        if (filter.getVenueEntityId() != null) {
            conditions = conditions.and(recinto.IDENTIDAD.eq(filter.getVenueEntityId().intValue()));
        }
        if (filter.getCountryId() != null) {
            conditions = conditions.and(recinto.PAIS.eq(filter.getCountryId().intValue()));
        }
        if (filter.getCity() != null) {
            conditions = conditions.and(recinto.MUNICIPIO.eq(filter.getCity()));
        }
        return conditions;
    }

    private SelectFieldOrAsterisk[] buildFields(SeasonTicketSearchFilter filter) {
        if (filter == null || CommonUtils.isEmpty(filter.getFields())) {
            return ArrayUtils.addAll(evento.fields(), JOIN_FIELDS);
        } else {
            Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = buildFilteredFields(filter.getFields());
            return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
        }
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

    public Condition getConditionsFromStatusList(List<SeasonTicketStatusDTO> statusList, CpanelSesion sesion) {
        List<Condition> allStatusConditionList = new ArrayList<>();

        for (SeasonTicketStatusDTO status : statusList) {

            // We call existing converter to avoid code repetition
            SessionRecord sessionRecord = new SessionRecord();
            SeasonTicketStatusConverter.fromSeasonStatus(status, sessionRecord);

            List<Condition> thisStatusConditionList = new ArrayList<>();
            thisStatusConditionList.add(sesion.ESTADO.eq(sessionRecord.getEstado()));
            thisStatusConditionList.add(sesion.ISPREVIEW.eq(sessionRecord.getIspreview()));

            allStatusConditionList.add(DSL.and(thisStatusConditionList));
        }
        return DSL.or(allStatusConditionList);
    }

    public int updateField(final Integer eventId, final Field field, final Object value) {
        return dsl.update(Tables.CPANEL_EVENTO).set(field, value).where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .execute();
    }
}
