package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.builder.ChannelEventDataBuilder;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.exception.CatalogIndexerException;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.catalog.elasticsearch.utils.ChannelCatalogInfoMerger;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.catalog.elasticsearch.utils.VenueUtils;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.EventChannelCommElemDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.postbookingquestions.dao.EventChannelPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.EventPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.exception.EventIndexationFullReload;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ChannelEventDataIndexer extends ChannelEventIndexer {

    private final ChannelEventElasticDao channelEventElasticDao;
    private final ChannelSessionElasticDao channelSessionElasticDao;
    private final CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    private final CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;

    @Autowired
    public ChannelEventDataIndexer(ChannelEventElasticDao channelEventElasticDao,
                                   ChannelSessionElasticDao channelSessionElasticDao,
                                   CatalogChannelEventCouchDao catalogChannelEventCouchDao,
                                   CustomTaxonomyDao customTaxonomyDao,
                                   EmailCommunicationElementDao emailCommunicationElementDao,
                                   EventChannelCommElemDao eventChannelCommElemDao,
                                   CatalogChannelSessionCouchDao catalogChannelSessionCouchDao,
                                   ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                   EventPostBookingQuestionDao eventPostBookingQuestionDao,
                                   EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao,
                                   ChannelsRepository channelsRepository,
                                   CacheRepository localCacheRepository,
                                   PostBookingQuestionCouchDao postBookingQuestionCouchDao) {
        super(customTaxonomyDao, emailCommunicationElementDao, eventChannelCommElemDao, channelEventCommunicationElementDao,
                eventPostBookingQuestionDao, eventChannelPostBookingQuestionDao, channelsRepository, localCacheRepository,
                postBookingQuestionCouchDao);
        this.channelEventElasticDao = channelEventElasticDao;
        this.channelSessionElasticDao = channelSessionElasticDao;
        this.catalogChannelEventCouchDao = catalogChannelEventCouchDao;
        this.catalogChannelSessionCouchDao = catalogChannelSessionCouchDao;
    }

    public void indexChannelEvents(EventIndexationContext ctx) {
        if (EventIndexationType.SEASON_TICKET.equals(ctx.getType())) {
            return;
        }

        List<ChannelEventData> channelEvents = buildChannelEvents(ctx);

        if (CollectionUtils.isNotEmpty(channelEvents)) {
            List<ChannelEventData> oldChannelEvents = channelEventElasticDao.getByEventId(ctx.getEventId());
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            catalogChannelEventCouchDao.bulkUpsert(channelEvents.stream().map(ChannelEventData::getChannelEvent).collect(Collectors.toList()));

            List<ChannelEventData> channelEventsToIndex = new ArrayList<>();
            if (EventStatus.READY.getId().equals(ctx.getEvent().getEstado()) &&
                    ctx.getAllSessions().stream().anyMatch(CatalogPublishableUtils::isPublishable)) {
                channelEventsToIndex = channelEvents.stream()
                        .filter(ChannelEventData::getMustBeIndexed)
                        .collect(Collectors.toList());
                channelEventElasticDao.bulkUpsert(false, routing, channelEventsToIndex.toArray(new ChannelEventData[0]));
                ctx.addDocumentsIndexed(channelEventsToIndex);
            }
            removeOldChannelEvents(routing, oldChannelEvents, channelEventsToIndex, ctx);
        }
    }

    private void removeOldChannelEvents(String routing, List<ChannelEventData> oldChannelEvents,
                                        List<ChannelEventData> newChannelEvents,
                                        EventIndexationContext ctx) {
        List<String> idsToRemove;
        if (EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
            if (ctx.getAllSessions().stream().noneMatch(CatalogPublishableUtils::isPublishable)) {
                idsToRemove = oldChannelEvents.stream().map(ChannelEventData::getId).toList();
                print(idsToRemove, "All sessions are preview");
            } else {
                List<String> idsIndexed = newChannelEvents.stream().map(ChannelEventData::getId).toList();
                idsToRemove = oldChannelEvents.stream().map(ChannelEventData::getId).filter(id -> !idsIndexed.contains(id)).collect(Collectors.toList());
                print(idsToRemove, "Old sessions filtering idsIndexed not empty");
            }
        } else {
            idsToRemove = oldChannelEvents.stream().map(ChannelEventData::getId).toList();
            print(idsToRemove, "EventStatus not ready");
        }
        if (CollectionUtils.isNotEmpty(idsToRemove)) {
            channelEventElasticDao.bulkDelete(routing, idsToRemove.toArray(new String[0]));
        }
    }

    public void indexOccupation(OccupationIndexationContext ctx) {
        List<ChannelEventData> occupations = buildChangedOccupations(ctx);
        if (CollectionUtils.isNotEmpty(occupations)) {
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            channelEventElasticDao.bulkUpsert(true, routing, occupations.toArray(new ChannelEventData[0]));
            ctx.addDocumentsIndexed(occupations);
        }
    }

    public void indexChannelEvent(Long channelId, Long eventId) {
        List<ChannelEventData> byChannelId = null;
        ChannelAttributes channelsAttrs = channelsRepository.getChannelAttributes(channelId);
        if (channelsAttrs != null) {
            byChannelId = channelEventElasticDao.getByChannelId(channelId);
        }

        if (CollectionUtils.isNotEmpty(byChannelId) && eventId != null) {
            byChannelId = byChannelId.stream()
                    .filter(channelEventData -> channelEventData.getChannelEvent().getEventId().equals(eventId))
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(byChannelId)) {
            byChannelId.forEach(channelEvent -> {
                Long id = channelEvent.getChannelEvent().getEventId();

                ChannelCatalogEventInfo catalogEventInfo = updateChannelEventBillboard(id.intValue(), channelEvent.getChannelEvent().getCatalogInfo(), channelsAttrs);
                channelEvent.getChannelEvent().setCatalogInfo(catalogEventInfo);

                String routing = EventDataUtils.getEventKey(id);
                channelEventElasticDao.upsert(channelEvent, routing, false);
            });
        }
    }

    private List<ChannelEventData> buildChannelEvents(EventIndexationContext ctx) {
        List<ChannelEventData> channelEventDatas = new ArrayList<>();
        List<CpanelCanalEventoRecord> channelEvents = ctx.getChannelEvents();
        for (CpanelCanalEventoRecord channelEvent : channelEvents) {
            Integer channelId = channelEvent.getIdcanal();
            Optional<EventChannelForCatalogRecord> eventChannel = ctx.getEventChannel(channelId);
            if (eventChannel.isPresent()) {
                try {
                    ChannelEventData channelEventData = build(channelEvent, eventChannel.get(), ctx);
                    channelEventDatas.add(channelEventData);
                } catch (Exception e) {
                    throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event:%d - channel:%d . Error indexing channel session", ctx.getEventId(), channelId), e);
                }
            }
        }
        return channelEventDatas;
    }

    private ChannelEventData build(CpanelCanalEventoRecord channelEventRecord,
                                   CpanelEventoCanalRecord eventChannelRecord,
                                   EventIndexationContext ctx) {
        Long channelId = channelEventRecord.getIdcanal().longValue();
        Integer eventId = channelEventRecord.getIdevento();

        switch (ctx.getType()) {
            case PARTIAL_BASIC -> {
                ChannelEventData channelEventData = getChannelEventData(channelId, eventId);
                updateBasicChannelEventInfo(channelEventRecord, channelEventData, ctx);
                return channelEventData;
            }
            case PARTIAL_COM_ELEMENTS -> {
                ChannelEventData channelEventData = getChannelEventData(channelId, eventId);
                updateComElementsChannelEventInfo(channelEventRecord, channelEventData.getChannelEvent());
                channelEventData.setMustBeIndexed(true);
                return channelEventData;
            }
        }

        //Basic data
        List<ChannelSession> channelSessions = new ArrayList<>();
        List<SessionForCatalogRecord> sessions = new ArrayList<>();
        fillChannelSessionsContext(ctx, channelId, channelSessions, sessions);

        List<SessionForCatalogRecord> publishableSessions = getPublishableSessions(sessions);
        Optional<Timestamp> firstPublishedDateSession = publishableSessions.stream().map(CpanelSesionRecord::getFechapublicacion).min(Timestamp::compareTo);

        List<SessionForCatalogRecord> publishableSessionPacks = getPublishableSessionPacks(sessions);
        Optional<Timestamp> firstPublishedDateSeasonPack = publishableSessionPacks.stream().map(CpanelSesionRecord::getFechapublicacion).min(Timestamp::compareTo);

        boolean hasPublishableSessions = !publishableSessions.isEmpty();
        boolean hasPublishableSessionPacks = !publishableSessionPacks.isEmpty();

        Optional<Timestamp> lastSessionTimeStamp = getLastPublishedSession(ctx, sessions);
        Date endEventDate = lastSessionTimeStamp.map(timestamp -> new Date(timestamp.getTime())).orElseGet(channelEventRecord::getFechafin);

        //Full data
        ChannelCatalogEventInfo catalogInfo = ChannelCatalogInfoMerger.merge(channelSessions);

        boolean someSessionNoShowDate = sessions.stream().anyMatch(s -> BooleanUtils.isFalse(s.getShowdate()));
        boolean someSessionNoShowDateTime = sessions.stream().anyMatch(s -> BooleanUtils.isFalse(s.getShowdatetime()));
        boolean someSessionNoFinalTime = sessions.stream().anyMatch(s -> CommonUtils.isTrue(s.getFechanodefinitiva()));
        boolean someSessionNoShowSchedule = sessions.stream().anyMatch(s -> !CommonUtils.isTrue(s.getMostrarhorario()));

        ChannelAttributes channelAttributes = ctx.getChannelAttributesByChannelId() == null ?
                null : ctx.getChannelAttributesByChannelId().getOrDefault(channelId.intValue(), null);
        addBillboardInfo(catalogInfo, channelEventRecord, channelAttributes, ctx.getEvent());

        List<VenueRecord> venues = getVenues(ctx, channelSessions);
        List<Long> venueIds = venues.stream()
                .map(VenueRecord::getId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> infoBannerSaleRequest = null;
        if (Objects.nonNull(eventChannelRecord.getElementocomcanal())) {
            infoBannerSaleRequest = eventChannelCommElemDao.getEventChannelCommElem(eventChannelRecord.getElementocomcanal());
        }

        boolean purchaseSecMktEventChannel = false;
        if (ctx.getEventSecondaryMarketConfig() != null && ctx.getEventSecondaryMarketConfig().getEnabledChannels() != null) {
            purchaseSecMktEventChannel = ctx.getEventSecondaryMarketConfig().getEnabledChannels()
                    .stream()
                    .anyMatch(enabledChannel ->
                    {
                        if (enabledChannel != null && enabledChannel.getId() != null) {
                            return channelId.equals(enabledChannel.getId());
                        } else {
                            return false;
                        }
                    });
        }

        BaseTaxonomyDao.TaxonomyInfo customTaxonomy = null;
        BaseTaxonomyDao.TaxonomyInfo customParentTaxonomy = null;
        if (eventChannelRecord.getTaxonomiapropia() != null) {
            Integer taxonomyId = eventChannelRecord.getTaxonomiapropia();
            customTaxonomy = localCacheRepository.cached(LocalCache.TAXONOMY_KEY, LocalCache.TAXONOMY_TTL, TimeUnit.SECONDS,
                    () -> customTaxonomyDao.getTaxonomyInfo(taxonomyId), new Object[]{taxonomyId});
            if (customTaxonomy.parentId() != null) {
                Integer parentTaxonomyId = customTaxonomy.parentId();
                customParentTaxonomy = localCacheRepository.cached(LocalCache.TAXONOMY_KEY, LocalCache.TAXONOMY_TTL, TimeUnit.SECONDS,
                        () -> customTaxonomyDao.getTaxonomyInfo(parentTaxonomyId), new Object[]{parentTaxonomyId});
            }
        }

        Boolean phoneValidationRequired = null;
        Boolean attendantVerificationRequired = null;
        if (ctx.getEventConfig() != null) {
            phoneValidationRequired = ctx.getEventConfig().getPhoneVerificationRequired();
            attendantVerificationRequired = ctx.getEventConfig().getAttendantVerificationRequired();
        }

        ChannelEventPostBookingQuestions postBookingQuestions = getChannelEventPostBookingQuestions(ctx, channelId);

        EventChangeSeatConfig eventChangeSeatConfig = getEventChangeSeatConfig(ctx);

        ChannelInfo channel = ctx.getChannelInfo(channelId);

        ChannelEventData channelEventData = ChannelEventDataBuilder.builder()
                .channelId(channelId)
                .eventId(channelEventRecord.getIdevento().longValue())
                //Basic
                .firstPublishedSession(firstPublishedDateSession)
                .firstPublishedSessionPack(firstPublishedDateSeasonPack)
                .hasSessions(hasPublishableSessions)
                .hasSessionPacks(hasPublishableSessionPacks)
                .endChannelEventDate(endEventDate)
                .catalogInfo(catalogInfo)
                //Full
                .channelName(channel.getName())
                .channelEntityId(channel.getEntityId())
                .channelEventId(channelEventRecord.getIdcanaleevento().longValue())
                .channelEventStatus(channelEventRecord.getEstadorelacion())
                .publishChannelEventDate(channelEventRecord.getFechapublicacion())
                .purchaseChannelEventDate(channelEventRecord.getFechaventa())
                .publishChannelEvent(CommonUtils.isTrue(channelEventRecord.getPublicado()))
                .purchaseChannelEvent(CommonUtils.isTrue(channelEventRecord.getEnventa()))
                .purchaseSecondaryMarketChannelEvent(CommonUtils.isTrue(purchaseSecMktEventChannel))
                .eventDates(CommonUtils.isTrue(channelEventRecord.getUsafechasevento()))
                .enabledBookingChannelEvent(CommonUtils.isTrue(channelEventRecord.getReservasactivas()))
                .beginBookingChannelEventDate(channelEventRecord.getFechainicioreserva())
                .endBookingChannelEventDate(channelEventRecord.getFechafinreserva())
                .customCategoryId(customTaxonomy == null ? null : customTaxonomy.id())
                .customCategoryCode(customTaxonomy == null ? null : customTaxonomy.code())
                .customCategoryName(customTaxonomy == null ? null : customTaxonomy.desc())
                .customParentCategoryId(customParentTaxonomy == null ? null : customParentTaxonomy.id())
                .customParentCategoryCode(customParentTaxonomy == null ? null : customParentTaxonomy.code())
                .customParentCategoryName(customParentTaxonomy == null ? null : customParentTaxonomy.desc())
                .venueIds(venueIds)
                .multiVenue(venueIds.size() > 1)
                .multiLocation(VenueUtils.areMultiLocation(venues))
                .surcharges(ctx.getChannelSurcharges().get(channelId))
                .allowChannelPromotions(CommonUtils.isTrue(eventChannelRecord.getPermitirpromocioncanales()))
                .sessionsShowDate(!someSessionNoShowDate)
                .sessionsShowDateTime(!someSessionNoShowDateTime)
                .sessionsShowSchedule(!someSessionNoShowSchedule)
                .sessionsNoFinalDate(someSessionNoFinalTime)
                .ticketHandling(CommonUtils.isTrue(eventChannelRecord.getTickethandling()))
                .useAlternativePromoterSurcharges(allowAlternativePromoterSurcharges(eventChannelRecord, ctx.getEvent(), channelEventRecord))
                .infoBannerSaleRequest(infoBannerSaleRequest)
                .mustBeIndexed(CollectionUtils.isNotEmpty(sessions))
                .phoneValidationRequired(phoneValidationRequired)
                .attendantVerificationRequired(attendantVerificationRequired)
                .postBookingQuestions(postBookingQuestions)
                .eventChangeSeatConfig(eventChangeSeatConfig)
                .channelSubtype(channel.getSubtype())
                .build();

        updateComElementsChannelEventInfo(channelEventRecord, channelEventData.getChannelEvent());

        return channelEventData;
    }

    private ChannelEventData getChannelEventData(Long channelIdLong, Integer eventId) {
        ChannelEvent channelEvent = catalogChannelEventCouchDao.get(channelIdLong.toString(), eventId.toString());
        if (channelEvent == null) {
            throw new EventIndexationFullReload("channel: " + channelIdLong);
        }
        ChannelEventData channelEventData = ChannelEventDataBuilder.buildChannelEventData(channelIdLong, eventId.longValue());
        channelEventData.setChannelEvent(channelEvent);
        return channelEventData;
    }

    private EventChangeSeatConfig getEventChangeSeatConfig(EventIndexationContext ctx) {
        if (ctx.getEventConfig() == null || ctx.getEventConfig().getEventChangeSeatConfig() == null) {
            return null;
        }
        EventChangeSeatConfig eventChangeSeatConfig = ctx.getEventConfig().getEventChangeSeatConfig();
        EventChangeSeatConfig eventChangeSeatConfigOut = new EventChangeSeatConfig();
        eventChangeSeatConfigOut.setAllowChangeSeat(eventChangeSeatConfig.getAllowChangeSeat());
        eventChangeSeatConfigOut.setEventChangeSeatExpiry(eventChangeSeatConfig.getEventChangeSeatExpiry());
        eventChangeSeatConfigOut.setChangeType(eventChangeSeatConfig.getChangeType());
        eventChangeSeatConfigOut.setNewTicketSelection(eventChangeSeatConfig.getNewTicketSelection());
        eventChangeSeatConfigOut.setReallocationChannel(eventChangeSeatConfig.getReallocationChannel());
        return eventChangeSeatConfigOut;
    }

    private void fillChannelSessionsContext(EventIndexationContext ctx, Long channelId,
                                            List<ChannelSession> channelSessions, List<SessionForCatalogRecord> sessions) {
        //When has no session filter all channel-sessions are available in context as indexed
        if (ctx.getSessionFilter() == null) {
            List<ChannelSessionData> allChannelSessions = ctx.getDocumentsIndexed(ChannelSessionData.class);
            for (ChannelSessionData indexedChannelSession : allChannelSessions) {
                if (indexedChannelSession.getChannelSession().getChannelId().equals(channelId)) {
                    channelSessions.add(indexedChannelSession.getChannelSession());
                }
            }
        } else {
            List<Key> channelSessionKeys = ctx.getAllSessions().stream()
                    .filter(CatalogPublishableUtils::isSessionToIndex)
                    .map(s -> new Key(new String[]{
                            channelId.toString(), s.getIdsesion().toString()})).collect(Collectors.toList());
            channelSessions.addAll(catalogChannelSessionCouchDao.bulkGet(channelSessionKeys));
        }
        Set<Long> channelSessionIds = channelSessions.stream().map(ChannelSession::getSessionId).collect(Collectors.toSet());
        sessions.addAll(ctx.getAllSessions().stream()
                .filter(s -> channelSessionIds.contains(s.getIdsesion().longValue())).toList());
    }

    private static List<ChannelSession> getChannelSessions(BaseIndexationContext<?, ?> ctx, Long channelId) {
        return ctx.getDocumentsIndexed(ChannelSessionData.class).stream()
                .map(ChannelSessionData::getChannelSession)
                .filter(channelSession -> channelSession.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    private List<ChannelEventData> buildChangedOccupations(OccupationIndexationContext ctx) {
        return ctx.getChannelEvents().stream()
                .map(channelEventData -> buildOccupation(ctx, channelEventData))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ChannelEventData buildOccupation(OccupationIndexationContext ctx, ChannelEventData channelEventData) {
        ChannelEvent channelEvent = channelEventData.getChannelEvent();
        Long eventId = channelEvent.getEventId();
        Long channelId = channelEvent.getChannelId();
        List<ChannelSession> channelSessions = getFreshOccupationChannelSessions(ctx, eventId, channelId);
        ChannelCatalogEventInfo catalogInfo = ChannelCatalogInfoMerger.mergeOccupation(channelSessions);

        //Skip update of channelEvent if there are no changes
        if (catalogInfo != null && channelEvent.getCatalogInfo() != null &&
                (Objects.equals(channelEvent.getCatalogInfo().getSoldOut(), catalogInfo.getSoldOut()) &&
                        Objects.equals(channelEvent.getCatalogInfo().getPrices(), catalogInfo.getPrices()))) {
            return null;
        }

        return ChannelEventDataBuilder.builder()
                .channelId(channelId)
                .eventId(eventId)
                .catalogInfo(catalogInfo)
                .build();
    }

    private List<ChannelSession> getFreshOccupationChannelSessions(BaseIndexationContext<?, ?> ctx, Long eventId, Long channelId) {
        List<ChannelSessionData> channelSessions = channelSessionElasticDao.getOccupationFieldsByEventAndChannelId(eventId, channelId);
        List<ChannelSession> freshChannelSessions = getChannelSessions(ctx, channelId);
        Map<Long, ChannelSession> freshChannelSessionsMap = freshChannelSessions.stream()
                .collect(Collectors.toMap(ChannelSession::getSessionId, java.util.function.Function.identity(), (existing, replacement) -> existing));
        List<ChannelSession> result = new java.util.ArrayList<>(channelSessions.size());
        for (ChannelSessionData old : channelSessions) {
            Long sessionId = old.getChannelSession().getSessionId();
            result.add(freshChannelSessionsMap.getOrDefault(sessionId, old.getChannelSession()));
        }
        return result;
    }

    private void updateBasicChannelEventInfo(CpanelCanalEventoRecord channelEventRecord, ChannelEventData channelEventData, EventIndexationContext ctx) {
        List<ChannelSession> channelSessions = new ArrayList<>();
        List<SessionForCatalogRecord> sessions = new ArrayList<>();
        fillChannelSessionsContext(ctx, channelEventRecord.getIdcanal().longValue(), channelSessions, sessions);

        channelEventData.setMustBeIndexed(CollectionUtils.isNotEmpty(sessions));
        updateBasicChannelEventInfo(channelEventRecord, channelEventData.getChannelEvent(), sessions, channelSessions, ctx);
    }

}
