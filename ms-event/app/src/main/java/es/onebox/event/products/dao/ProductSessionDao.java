package es.onebox.event.products.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.datasources.ms.channel.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductSessionsPublishingFilterDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelCanalEvento;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductChannel;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.CpanelProductEventDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelProductSession;
import es.onebox.jooq.cpanel.tables.CpanelProductSessionDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_SESSION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_SESSION_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;

@Repository
public class ProductSessionDao extends DaoImpl<CpanelProductSessionRecord, Integer> {
    private static final CpanelProductSession productSession = CpanelProductSession.CPANEL_PRODUCT_SESSION;
    private static final CpanelProductEvent productEvent = CPANEL_PRODUCT_EVENT.as("productoEvento");
    private static final CpanelProductSessionDeliveryPoint productSessionDeliveryPoint =
            CPANEL_PRODUCT_SESSION_DELIVERY_POINT.as("productSessionDeliveryPoint");
    private static final CpanelProductEventDeliveryPoint productEventDeliveryPoint =
            CPANEL_PRODUCT_EVENT_DELIVERY_POINT.as("productEventDeliveryPoint");
    private static final CpanelEvento event = CPANEL_EVENTO.as("evento");
    private static final CpanelSesion session = CPANEL_SESION.as("sesion");
    private static final CpanelProduct product = CPANEL_PRODUCT.as("producto");
    private static final CpanelCanal channel = CPANEL_CANAL;
    private static final CpanelProductChannel productChannel = CpanelProductChannel.CPANEL_PRODUCT_CHANNEL;
    private static final CpanelCanalEvento channelEvent = CPANEL_CANAL_EVENTO;
    private static final List<Integer> ALLOWED_SESSION_STATUS = Arrays.asList(SessionStatus.SCHEDULED.getId(),
            SessionStatus.READY.getId(), SessionStatus.PREVIEW.getId(), SessionStatus.IN_PROGRESS.getId(),
            SessionStatus.PLANNED.getId());

    private static final List<Integer> ALLOWED_EVENT_STATUS = Arrays.asList(EventStatus.PLANNED.getId(),
            EventStatus.IN_PROGRAMMING.getId(), EventStatus.READY.getId(), EventStatus.IN_PROGRESS.getId());
    private static final byte NOT_SUBSCRIPTION = 0;

    protected ProductSessionDao() {
        super(CPANEL_PRODUCT_SESSION);
    }

    public List<ProductSessionRecord> findProductSessionsByProductId(Integer productId, Integer eventId) {
        return findProductSessionsByProductId(productId, eventId, null);
    }

    public List<ProductSessionRecord> findProductSessionsByProductId(Integer productId, Integer eventId, ProductSessionsPublishingFilterDTO filter) {
        var query = dsl.select(productSession.fields())
                .select(session.FECHAINICIOSESION)
                .select(session.FECHAREALFINSESION)
                .select(session.NOMBRE)
                .from(productSession)
                .innerJoin(productEvent).on(productSession.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                .innerJoin(session).on(productSession.SESSIONID.eq(session.IDSESION))
                .innerJoin(event).on(productEvent.EVENTID.eq(event.IDEVENTO))
                .where(buildWhere(productId, eventId, filter));

        return query.fetch().map(this::buildProductSessionRecord);
    }

    private Condition buildWhere(Integer productId, Integer eventId, ProductSessionsPublishingFilterDTO filter) {
        Condition condition = productEvent.PRODUCTID.eq(productId)
                .and(productEvent.EVENTID.eq(eventId))
                .and(session.ESTADO.in(ALLOWED_SESSION_STATUS))
                .and(session.ESABONO.eq(NOT_SUBSCRIPTION))
                .and(event.ESTADO.in(ALLOWED_EVENT_STATUS));
        if (filter != null && CollectionUtils.isNotEmpty(filter.getSessionIds())) {
            condition = condition.and(session.IDSESION.in(filter.getSessionIds()));
        }
        return condition;
    }


    public List<ProductSessionRecord> findRestrictedSessionsByProductId(Integer productId) {
        SelectOnConditionStep<Record> query = dsl
                .select(productSession.fields())
                .from(productSession)
                .innerJoin(productEvent).on(productSession.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID)
                        .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId())
                                .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.RESTRICTED.getId()))))
                .and(productEvent.PRODUCTID.eq(productId))
                .innerJoin(productSessionDeliveryPoint).on(productSession.SESSIONID.eq(productSessionDeliveryPoint.SESSIONID))
                .innerJoin(productEventDeliveryPoint).on(productEvent.PRODUCTEVENTID.eq(productEventDeliveryPoint.PRODUCTEVENTID));

        return query.fetch().map(this::buildProductSessionRecord);
    }

    public List<CpanelProductSessionRecord> findByProductEventId(Integer productEventId) {
        return dsl.selectFrom(productSession).where(productSession.PRODUCTEVENTID.eq(productEventId)).fetchInto(CpanelProductSessionRecord.class);
    }

    public Set<Integer> findRelatedEvents(Long productId, List<Long> eventIds, List<Integer> channelIds) {
        try {
            SelectConditionStep query = dsl.selectDistinct(productEvent.EVENTID)
                    .from(productChannel)
                    .innerJoin(channel).on(channel.IDCANAL.eq(productChannel.CHANNELID)).and(channel.ESTADO.eq(1))
                    .innerJoin(productEvent).on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID)).and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.ALL.getId()))
                    .innerJoin(channelEvent).on(channelEvent.IDCANAL.eq(channel.IDCANAL)).and(channelEvent.IDEVENTO.eq(productEvent.EVENTID)).and(channelEvent.ESTADORELACION.eq(ChannelEventStatus.ACCEPTED.getStatus()))
                    .where(productChannel.PRODUCTID.eq(productId.intValue()));

            if (eventIds != null && !eventIds.isEmpty()) {
                query.and(productEvent.EVENTID.in(eventIds));
            }

            if (channelIds != null && !channelIds.isEmpty()) {
                query.and(productChannel.CHANNELID.in(channelIds));
            }

            return new HashSet<Integer>(query.fetchInto(Integer.class));
        } catch (Exception e) {
            return null;
        }
    }

    public Map<Long, List<Long>> findPublishedSessions(Long productId, Set<Long> sessionIds, List<Long> eventIds, List<Integer> channelIds) {
        try {
            SelectConditionStep query = dsl.select(productSession.SESSIONID, event.IDEVENTO)
                    .from(productChannel)
                    .innerJoin(channel).on(channel.IDCANAL.eq(productChannel.CHANNELID))
                    .and(channel.ESTADO.eq(1))
                    .innerJoin(productEvent).on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID))
                    .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                    .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.RESTRICTED.getId()))
                    .innerJoin(channelEvent).on(channelEvent.IDCANAL.eq(channel.IDCANAL)).and(channelEvent.IDEVENTO.eq(productEvent.EVENTID))
                    .and(channelEvent.ESTADORELACION.eq(ChannelEventStatus.ACCEPTED.getStatus()))
                    .innerJoin(productSession).on(productSession.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .innerJoin(event).on(event.IDEVENTO.eq(productEvent.EVENTID))
                    .where(productChannel.PRODUCTID.eq(productId.intValue()));

            if (sessionIds != null && !sessionIds.isEmpty()) {
                query.and(productSession.SESSIONID.in(sessionIds));
            }

            if (eventIds != null && !eventIds.isEmpty()) {
                query.and(productEvent.EVENTID.in(eventIds));
            }

            if (channelIds != null && !channelIds.isEmpty()) {
                query.and(productChannel.CHANNELID.in(channelIds));
            }
            return (Map<Long, List<Long>>) query.fetchGroups(event.IDEVENTO, productSession.SESSIONID)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> ((Map.Entry<Integer, List<Integer>>) e).getKey().longValue(),
                            e -> ((Map.Entry<Integer, List<Integer>>) e).getValue().stream()
                                    .map(Integer::longValue)
                                    .collect(Collectors.toList())
                    ));
        } catch (Exception e) {
            return null;
        }
    }

    public Set<Integer> findPublishedDeliveryProducts(Long deliveryPointId) {
        try {
            Set<Integer> result = new HashSet<>();

            List<Integer> eventProducts = dsl.selectDistinct(product.PRODUCTID)
                    .from(productChannel)
                    .innerJoin(channel).on(channel.IDCANAL.eq(productChannel.CHANNELID))
                    .and(channel.ESTADO.eq(1))
                    .innerJoin(productEvent).on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID))
                    .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                    .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.ALL.getId()))
                    .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                    .and(product.STATE.eq(ProductState.ACTIVE.getId()))
                    .innerJoin(channelEvent).on(channelEvent.IDCANAL.eq(channel.IDCANAL)).and(channelEvent.IDEVENTO.eq(productEvent.EVENTID))
                    .and(channelEvent.ESTADORELACION.eq(ChannelEventStatus.ACCEPTED.getStatus()))
                    .leftJoin(productEventDeliveryPoint).on(productEventDeliveryPoint.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .and(productEventDeliveryPoint.DELIVERYPOINTID.eq(deliveryPointId.intValue()))
                    .leftJoin(productSessionDeliveryPoint).on(productSessionDeliveryPoint.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .and(productSessionDeliveryPoint.DELIVERYPOINTID.eq(deliveryPointId.intValue()))
                    .fetchInto(Integer.class);


            List<Integer> sessionProducts = dsl.selectDistinct(product.PRODUCTID)
                    .from(productChannel)
                    .innerJoin(channel).on(channel.IDCANAL.eq(productChannel.CHANNELID))
                    .and(channel.ESTADO.eq(1))
                    .innerJoin(productEvent).on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID))
                    .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                    .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.RESTRICTED.getId()))
                    .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                    .and(product.STATE.eq(ProductState.ACTIVE.getId()))
                    .innerJoin(channelEvent).on(channelEvent.IDCANAL.eq(channel.IDCANAL)).and(channelEvent.IDEVENTO.eq(productEvent.EVENTID))
                    .and(channelEvent.ESTADORELACION.eq(ChannelEventStatus.ACCEPTED.getStatus()))
                    .leftJoin(productEventDeliveryPoint).on(productEventDeliveryPoint.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .and(productEventDeliveryPoint.DELIVERYPOINTID.eq(deliveryPointId.intValue()))
                    .leftJoin(productSessionDeliveryPoint).on(productSessionDeliveryPoint.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .and(productSessionDeliveryPoint.DELIVERYPOINTID.eq(deliveryPointId.intValue()))
                    .fetchInto(Integer.class);

            result.addAll(eventProducts);
            result.addAll(sessionProducts);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private ProductSessionRecord buildProductSessionRecord(Record sessionRecord) {
        ProductSessionRecord result = sessionRecord.into(ProductSessionRecord.class);
        result.setSession(sessionRecord.into(CpanelSesionRecord.class));

        return result;
    }

    public void deleteByProductEventId(Integer productEventId) {
        dsl.deleteFrom(CPANEL_PRODUCT_SESSION)
                .where(CPANEL_PRODUCT_SESSION.PRODUCTEVENTID.eq(productEventId)).execute();
    }

    public CpanelProductSessionRecord findProductEventSession(Integer productEventId, Integer sessionId) {
        return CommonUtils.ifNotNull(
                dsl.select(productSession.fields())
                        .from(productSession)
                        .where(productSession.PRODUCTEVENTID.eq(productEventId).and(productSession.SESSIONID.eq(sessionId)))
                        .fetchOne(), result -> result.into(CpanelProductSessionRecord.class)
        );
    }

}
