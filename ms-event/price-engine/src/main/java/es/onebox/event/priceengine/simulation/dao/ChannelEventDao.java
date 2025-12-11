package es.onebox.event.priceengine.simulation.dao;

import es.onebox.event.priceengine.request.ChannelStatus;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.request.StatusRequestType;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.priceengine.sorting.EventChannelField;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelAsignacionCanal;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelCanalEvento;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelTimeZoneGroup;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static es.onebox.jooq.cpanel.tables.CpanelCanal.CPANEL_CANAL;
import static es.onebox.jooq.cpanel.tables.CpanelCanalEvento.CPANEL_CANAL_EVENTO;

@Repository
public class ChannelEventDao extends DaoImpl<CpanelCanalEventoRecord, Integer> {

    private static final CpanelCanalEvento channelEvent = CPANEL_CANAL_EVENTO.as(EventChannelField.Alias.EVENT_CHANNEL);
    private static final CpanelCanal channel = CPANEL_CANAL.as(EventChannelField.Alias.CHANNEL);
    private static final CpanelEntidad entity = CpanelEntidad.CPANEL_ENTIDAD.as("entity");
    private static final CpanelEvento event = CpanelEvento.CPANEL_EVENTO.as("event");
    private static final CpanelAsignacionCanal favoriteChannels = CpanelAsignacionCanal.CPANEL_ASIGNACION_CANAL.as("favoriteChannels");

    private static final CpanelTimeZoneGroup releaseDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("releaseDateTZ");
    private static final CpanelTimeZoneGroup saleDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("saleDateTZ");
    private static final CpanelTimeZoneGroup saleEndDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("saleEndDateTZ");
    private static final CpanelTimeZoneGroup bookingStartDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("bookingStartDateTZ");
    private static final CpanelTimeZoneGroup bookingEndDateTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("bookingEndDateTZ");
    private static final Field<Byte> favorite = DSL.iif(favoriteChannels.IDENTIDAD.isNull(), (byte) 0, (byte) 1).as("favorite");

    private static final Byte ONE = 1;

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            channel.IDCANAL,
            channel.NOMBRECANAL,
            channel.IDENTIDAD,
            channel.IDSUBTIPOCANAL,
            entity.NOMBRE,
            event.ESTADO,
            event.FECHAPUBLICACION,
            releaseDateTZ.OLSONID,
            event.FECHAVENTA,
            saleDateTZ.OLSONID,
            event.FECHAFIN,
            saleEndDateTZ.OLSONID,
            event.FECHAINICIORESERVA,
            bookingStartDateTZ.OLSONID,
            event.FECHAFINRESERVA,
            bookingEndDateTZ.OLSONID,
            entity.PATHIMAGEN,
            entity.IDOPERADORA,
            favorite,
            event.IDCURRENCY
    };

    protected ChannelEventDao() {
        super(CPANEL_CANAL_EVENTO);
    }

    public List<CpanelCanalEventoRecord> getChannelEvents(Long eventId) {
        return dsl.select(CPANEL_CANAL_EVENTO.fields())
                .from(CPANEL_CANAL_EVENTO)
                .where(CPANEL_CANAL_EVENTO.IDEVENTO.eq(eventId.intValue()))
                .fetch().into(CpanelCanalEventoRecord.class);
    }

    public Optional<CpanelCanalEventoRecord> getChannelEvent(int channelId, int eventId) {
        return dsl.select(CPANEL_CANAL_EVENTO.fields())
                .from(CPANEL_CANAL_EVENTO)
                .where(CPANEL_CANAL_EVENTO.IDCANAL.eq(channelId))
                .and(CPANEL_CANAL_EVENTO.IDEVENTO.eq(eventId))
                .fetchOptional().map(record -> record.into(CpanelCanalEventoRecord.class));
    }

    public EventChannelRecord getChannelEventDetailed(int channelId, int eventId) {
        SelectJoinStep<Record> query = buildSelect();
        query.where(channel.ESTADO.ne(ChannelStatus.DELETED.getId())
                .and(channelEvent.IDEVENTO.eq(eventId))
                .and(channelEvent.IDCANAL.eq(channelId)));
        return query.fetchOne(this::buildEventChannelRecord);
    }

    public List<EventChannelRecord> findChannelEvents(Long eventId, EventChannelSearchFilter filter) {
        SelectJoinStep<Record> query = buildSelect();

        query.where(builderWhereClause(eventId, filter));

        query.orderBy(SortUtils.buildSort(filter.getSort(), EventChannelField::byName));

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        return query.fetch(this::buildEventChannelRecord);
    }

    private SelectJoinStep<Record> buildSelect() {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(channelEvent.fields(), JOIN_FIELDS);
        return dsl.select(fields)
                .from(channelEvent)
                .join(channel).on(channelEvent.IDCANAL.eq(channel.IDCANAL))
                .join(entity).on(entity.IDENTIDAD.eq(channel.IDENTIDAD))
                .join(event).on(event.IDEVENTO.eq(channelEvent.IDEVENTO))
                .leftJoin(releaseDateTZ).on(event.FECHAPUBLICACIONTZ.eq(releaseDateTZ.ZONEID))
                .leftJoin(saleDateTZ).on(event.FECHAVENTATZ.eq(saleDateTZ.ZONEID))
                .leftJoin(saleEndDateTZ).on(event.FECHAFINTZ.eq(saleEndDateTZ.ZONEID))
                .leftJoin(bookingStartDateTZ).on(event.FECHAINICIORESERVATZ.eq(bookingStartDateTZ.ZONEID))
                .leftJoin(bookingEndDateTZ).on(event.FECHAFINRESERVATZ.eq(bookingEndDateTZ.ZONEID))
                .leftJoin(favoriteChannels).on(favoriteChannels.IDCANAL.eq(channel.IDCANAL).and(favoriteChannels.IDENTIDAD.eq(event.IDENTIDAD)));
    }

    public Long countByFilter(Long eventId, EventChannelSearchFilter filter) {
        return dsl.selectCount()
                .from(channelEvent)
                .join(channel).on(channelEvent.IDCANAL.eq(channel.IDCANAL))
                .join(entity).on(entity.IDENTIDAD.eq(channel.IDENTIDAD))
                .where(builderWhereClause(eventId, filter))
                .fetchOne(0, Long.class);
    }

    private Condition builderWhereClause(Long eventId, EventChannelSearchFilter filter) {
        Condition conditions = DSL.trueCondition();

        conditions = channel.ESTADO.ne(ChannelStatus.DELETED.getId())
                .and(channelEvent.IDEVENTO.eq(eventId.intValue()));

        if (CollectionUtils.isNotEmpty(filter.getId())) {
            conditions = conditions.and(channelEvent.IDCANAL.in(filter.getId()));
        }

        if (CollectionUtils.isNotEmpty(filter.getSubtype())) {
            conditions = conditions.and(channel.IDSUBTIPOCANAL.in(filter.getSubtype().stream().map(ChannelSubtype::getIdSubtipo).toList()));
        }

        if (CollectionUtils.isNotEmpty(filter.getRequestStatus())) {
            conditions = conditions.and(channelEvent.ESTADORELACION.in(filter.getRequestStatus().stream().map(StatusRequestType::getId).toList()));
        }

        if (filter.getEntityId() != null) {
            conditions = conditions.and(channel.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }

        if (StringUtils.isNotEmpty(filter.getQ())) {
            conditions = conditions.and(channel.NOMBRECANAL.like("%" + filter.getQ() + "%"));
        }

        return conditions;
    }


    private EventChannelRecord buildEventChannelRecord(Record record) {
        EventChannelRecord eventChannelRecord = new EventChannelRecord();

        eventChannelRecord.setId(record.get(channelEvent.IDCANALEEVENTO).longValue());
        eventChannelRecord.setAllSaleGroups(ONE.equals(record.get(channelEvent.TODOSGRUPOSVENTA)));
        eventChannelRecord.setOperatorId(record.get(entity.IDOPERADORA).longValue());
        eventChannelRecord.setChannelId(record.get(channel.IDCANAL).longValue());
        eventChannelRecord.setChannelName(record.get(channel.NOMBRECANAL));
        eventChannelRecord.setChannelType(record.get(channel.IDSUBTIPOCANAL));
        eventChannelRecord.setEntityId(record.get(channel.IDENTIDAD).longValue());
        eventChannelRecord.setEntityName(record.get(entity.NOMBRE));
        eventChannelRecord.setEntityLogoPath(record.get(entity.PATHIMAGEN));
        eventChannelRecord.setRequestStatus(record.get(channelEvent.ESTADORELACION));
        eventChannelRecord.setUseEventDates(ONE.equals(record.get(channelEvent.USAFECHASEVENTO)));

        eventChannelRecord.setEventId(record.get(channelEvent.IDEVENTO).longValue());
        eventChannelRecord.setEventStatus(record.get(event.ESTADO));
        eventChannelRecord.setEventCurrencyId(record.get(event.IDCURRENCY));

        eventChannelRecord.setReleaseEnable(ONE.equals(record.get(channelEvent.PUBLICADO)));
        eventChannelRecord.setSaleEnable(ONE.equals(record.get(channelEvent.ENVENTA)));
        eventChannelRecord.setBookingEnable(ONE.equals(record.get(channelEvent.RESERVASACTIVAS)));

        eventChannelRecord.setReleaseDateTZ(record.get(releaseDateTZ.OLSONID));
        eventChannelRecord.setSaleStartDateTZ(record.get(saleDateTZ.OLSONID));
        eventChannelRecord.setSaleEndDateTZ(record.get(saleEndDateTZ.OLSONID));
        eventChannelRecord.setBookingStartDateTZ(record.get(bookingStartDateTZ.OLSONID));
        eventChannelRecord.setBookingEndDateTZ(record.get(bookingEndDateTZ.OLSONID));

        eventChannelRecord.setReleaseDate(record.get(channelEvent.FECHAPUBLICACION));
        eventChannelRecord.setSaleStartDate(record.get(channelEvent.FECHAVENTA));
        eventChannelRecord.setSaleEndDate(record.get(channelEvent.FECHAFIN));
        eventChannelRecord.setBookingStartDate(record.get(channelEvent.FECHAINICIORESERVA));
        eventChannelRecord.setBookingEndDate(record.get(channelEvent.FECHAFINRESERVA));

        eventChannelRecord.setUseEventSurcharges(ONE.equals(record.get(channelEvent.USARECARGOEVENTO)));
        eventChannelRecord.setRecommendedChannelSurcharges(ONE.equals(record.get(channelEvent.RECOMENDARRECARGOSCANAL)));
        eventChannelRecord.setMinSurcharge(record.get(channelEvent.RECARGOMINIMO));
        eventChannelRecord.setMaxSurcharge(record.get(channelEvent.RECARGOMAXIMO));

        eventChannelRecord.setUsePromotionEventSurcharges(ONE.equals(record.get(channelEvent.USARECARGOEVENTOPROMOCION)));
        eventChannelRecord.setRecommendedPromotionChannelSurcharges(ONE.equals(record.get(channelEvent.RECOMENDARRECARGOSPROMOCIONCANAL)));
        eventChannelRecord.setMinPromotionSurcharge(record.get(channelEvent.RECARGOPROMOCIONMINIMO));
        eventChannelRecord.setMaxPromotionSurcharge(record.get(channelEvent.RECARGOPROMOCIONMAXIMO));

        eventChannelRecord.setRecommendedInvChannelSurcharges(ONE.equals(record.get(channelEvent.RECOMENDARRECARGOSINVCANAL)));
        eventChannelRecord.setMinInvSurcharge(record.get(channelEvent.RECARGOINVMINIMO));
        eventChannelRecord.setMaxInvSurcharge(record.get(channelEvent.RECARGOINVMAXIMO));
        eventChannelRecord.setFavorite(ONE.equals(record.get(favorite)));
        eventChannelRecord.setAllowChannelUseAlternativeCharges(record.get(channelEvent.ALLOWCHANNELUSEALTERNATIVECHARGES));
        eventChannelRecord.setIndividualTicketTemplate(record.get(channelEvent.IDPLANTILLATICKET));
        return eventChannelRecord;
    }
}
