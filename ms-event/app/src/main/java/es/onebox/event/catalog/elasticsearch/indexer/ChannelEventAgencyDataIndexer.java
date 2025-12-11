package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.logger.util.LogUtil;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dao.CatalogChannelEventAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.builder.ChannelEventAgencyDataBuilder;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.exception.CatalogIndexerException;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.catalog.elasticsearch.utils.ChannelCatalogInfoMerger;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.catalog.elasticsearch.utils.VenueUtils;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.EventChannelCommElemDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.EventStatus;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

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

@Service
public class ChannelEventAgencyDataIndexer extends ChannelEventIndexer {

    private final CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao;
    private final ChannelEventAgencyElasticDao channelEventAgencyElasticDao;
    private final ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao;
    private final CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao;

    public ChannelEventAgencyDataIndexer(CustomTaxonomyDao customTaxonomyDao,
                                         EmailCommunicationElementDao emailCommunicationElementDao,
                                         EventChannelCommElemDao eventChannelCommElemDao,
                                         CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao,
                                         ChannelEventAgencyElasticDao channelEventAgencyElasticDao,
                                         ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao,
                                         CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao,
                                         ChannelsRepository channelsRepository,
                                         CacheRepository localCacheRepository,
                                         PostBookingQuestionCouchDao postBookingQuestionCouchDao) {
        super(customTaxonomyDao, emailCommunicationElementDao, eventChannelCommElemDao, null,
                null, null, channelsRepository, localCacheRepository,
                postBookingQuestionCouchDao);
        this.catalogChannelEventAgencyCouchDao = catalogChannelEventAgencyCouchDao;
        this.channelEventAgencyElasticDao = channelEventAgencyElasticDao;
        this.channelSessionAgencyElasticDao = channelSessionAgencyElasticDao;
        this.catalogChannelSessionAgencyCouchDao = catalogChannelSessionAgencyCouchDao;
    }

    public void indexChannelAgencyEvents(EventIndexationContext ctx) {
        if (EventIndexationType.SEASON_TICKET.equals(ctx.getType())) {
            return;
        }

        List<ChannelEventAgencyData> channelEvents = buildChannelEvents(ctx);

        if (CollectionUtils.isNotEmpty(channelEvents)) {
            List<ChannelEventAgencyData> oldChannelEvents = channelEventAgencyElasticDao.getByEventId(ctx.getEventId());
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            List<ChannelEventAgency> docs = channelEvents.stream().map(ChannelEventAgencyData::getChannelEventAgency).collect(Collectors.toList());
            catalogChannelEventAgencyCouchDao.bulkUpsert(docs);
            List<ChannelEventAgencyData> channelEventsToIndex = new ArrayList<>();
            if (EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
                channelEventsToIndex = channelEvents.stream()
                        .filter(ChannelEventAgencyData::getMustBeIndexed)
                        .collect(Collectors.toList());
                channelEventAgencyElasticDao.bulkUpsert(false, routing, channelEventsToIndex.toArray(new ChannelEventAgencyData[0]));
                ctx.addDocumentsIndexed(channelEventsToIndex);
            }
            removeOldChannelEvents(routing, oldChannelEvents, channelEventsToIndex, ctx);
        }
    }

    public void indexOccupation(OccupationIndexationContext ctx) {
        List<ChannelEventAgencyData> occupations = buildChangedOccupations(ctx);
        if (CollectionUtils.isNotEmpty(occupations)) {
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            channelEventAgencyElasticDao.bulkUpsert(true, routing, occupations.toArray(new ChannelEventAgencyData[0]));
            ctx.addDocumentsIndexed(occupations);
        }
    }

    public void indexChannelEventAgency(Long channelId, Long eventId, Long agencyId) {
        List<ChannelEventAgencyData> byChannelId = null;
        ChannelAttributes channelsAttrs = channelsRepository.getChannelAttributes(channelId);
        if (channelsAttrs != null) {
            byChannelId = channelEventAgencyElasticDao.getByChannelId(channelId, agencyId);
        }

        if (CollectionUtils.isNotEmpty(byChannelId) && eventId != null) {
            byChannelId = byChannelId.stream()
                    .filter(channelEventData -> channelEventData.getChannelEventAgency().getEventId().equals(eventId))
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(byChannelId)) {
            byChannelId.forEach(channelEvent -> {
                Long id = channelEvent.getChannelEventAgency().getEventId();

                ChannelCatalogEventInfo catalogEventInfo = updateChannelEventBillboard(id.intValue(), channelEvent.getChannelEventAgency().getCatalogInfo(), channelsAttrs);
                channelEvent.getChannelEventAgency().setCatalogInfo(catalogEventInfo);

                String routing = EventDataUtils.getEventKey(id);
                channelEventAgencyElasticDao.upsert(channelEvent, routing, false);
            });
        }
    }

    private void removeOldChannelEvents(String routing, List<ChannelEventAgencyData> oldChannelEvents,
                                        List<ChannelEventAgencyData> newChannelEvents,
                                        EventIndexationContext ctx) {
        List<String> idsToRemove;
        if (EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
            if (ctx.getAllSessions().stream().noneMatch(CatalogPublishableUtils::isPublishable)) {
                idsToRemove = oldChannelEvents.stream().map(ChannelEventAgencyData::getId).toList();
                print(idsToRemove, "All sessions are preview");
            } else {
                List<String> idsIndexed = newChannelEvents.stream().map(ChannelEventAgencyData::getId).toList();
                idsToRemove = oldChannelEvents.stream().map(ChannelEventAgencyData::getId).filter(id -> !idsIndexed.contains(id)).collect(Collectors.toList());
                print(idsToRemove, "Old sessions filtering idsIndexed not empty");
            }
        } else {
            idsToRemove = oldChannelEvents.stream().map(ChannelEventAgencyData::getId).toList();
            print(idsToRemove, "EventStatus not ready");
        }
        if (CollectionUtils.isNotEmpty(idsToRemove)) {
            LOGGER.info("[EVENT2ES] Removing old channel events {} ids", LogUtil.collectionToString(idsToRemove));
            channelSessionAgencyElasticDao.bulkDelete(routing, idsToRemove.toArray(new String[0]));
        }
    }

    private List<ChannelEventAgencyData> buildChannelEvents(EventIndexationContext ctx) {
        List<ChannelEventAgencyData> channelEventDatas = new ArrayList<>();
        List<CpanelCanalEventoRecord> channelEvents = ctx.getChannelEvents();
        channelEvents.stream().filter(c -> includeB2B(c, ctx)).forEach(channelEvent -> {
            Integer channelId = channelEvent.getIdcanal();
            Map<Long, ChannelAgency> channelAgencies = ctx.getChannelAgencies(channelId.longValue());
            Optional<EventChannelForCatalogRecord> eventChannel = ctx.getEventChannel(channelId);
            if (eventChannel.isPresent() && MapUtils.isNotEmpty(channelAgencies)) {
                channelAgencies.forEach((agencyId, channelAgency) -> {
                    try {
                        ChannelEventAgencyData channelEventAgencyData = build(channelEvent, eventChannel.get(), ctx, channelAgency);
                        channelEventDatas.add(channelEventAgencyData);
                    } catch (Exception e) {
                        throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event:%d - channel:%d - agency:%d. Error indexing channel session", ctx.getEventId(), channelId, agencyId), e);
                    }
                });
            }
        });
        return channelEventDatas;
    }

    private ChannelEventAgencyData build(CpanelCanalEventoRecord channelEventRecord,
                                         CpanelEventoCanalRecord eventChannelRecord,
                                         EventIndexationContext ctx, ChannelAgency agency) {
        Long channelId = channelEventRecord.getIdcanal().longValue();
        Integer eventId = channelEventRecord.getIdevento();
        Long agencyId = agency.getId();

        switch (ctx.getType()) {
            case PARTIAL_BASIC -> {
                ChannelEventAgencyData channelEventAgencyData = getChannelEventAgencyData(channelId, eventId, agencyId);
                updateBasicChannelEventAgencyInfo(channelEventRecord, agencyId, ctx, channelEventAgencyData);
                return channelEventAgencyData;
            }
            case PARTIAL_COM_ELEMENTS -> {
                ChannelEventAgencyData channelEventAgencyData = getChannelEventAgencyData(channelId, eventId, agencyId);
                updateComElementsChannelEventInfo(channelEventRecord, channelEventAgencyData.getChannelEventAgency());
                channelEventAgencyData.setMustBeIndexed(true);
                return channelEventAgencyData;
            }
        }

        //Basic data
        List<ChannelSessionAgency> channelSessions = new ArrayList<>();
        List<SessionForCatalogRecord> sessions = new ArrayList<>();
        fillChannelSessionsContext(ctx, channelId, agencyId, channelSessions, sessions);

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

        ChannelInfo channel = ctx.getChannelInfo(channelId);

        ChannelEventAgencyData channelEventAgencyData = ChannelEventAgencyDataBuilder.builder()
                .channelId(channelId)
                .eventId(eventId.longValue())
                .agencyId(agencyId)
                //Basic
                .firstPublishedSession(firstPublishedDateSession)
                .firstPublishedSessionPack(firstPublishedDateSeasonPack)
                .hasSessions(hasPublishableSessions)
                .hasSessionPacks(hasPublishableSessionPacks)
                .endChannelEventDate(endEventDate)
                .catalogInfo(catalogInfo)
                //Full
                .agencyConditions(agency.getConditions())
                .channelName(channel.getName())
                .channelEntityId(channel.getEntityId())
                .channelEventId(channelEventRecord.getIdcanaleevento().longValue())
                .channelEventStatus(channelEventRecord.getEstadorelacion())
                .enabledBookingChannelEvent(CommonUtils.isTrue(channelEventRecord.getReservasactivas()))
                .publishChannelEventDate(channelEventRecord.getFechapublicacion())
                .purchaseChannelEventDate(channelEventRecord.getFechaventa())
                .publishChannelEvent(CommonUtils.isTrue(channelEventRecord.getPublicado()))
                .purchaseChannelEvent(CommonUtils.isTrue(channelEventRecord.getEnventa()))
                .purchaseSecondaryMarketChannelEvent(CommonUtils.isTrue(purchaseSecMktEventChannel))
                .eventDates(CommonUtils.isTrue(channelEventRecord.getUsafechasevento()))
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
                .useAlternativePromoterSurcharges(allowAlternativePromoterSurcharges
                        (eventChannelRecord, ctx.getEvent(), channelEventRecord))
                .infoBannerSaleRequest(infoBannerSaleRequest)
                .mustBeIndexed(CollectionUtils.isNotEmpty(sessions))
                .channelSubtype(channel.getSubtype())
                .build();

        updateComElementsChannelEventInfo(channelEventRecord, channelEventAgencyData.getChannelEventAgency());

        return channelEventAgencyData;
    }

    private ChannelEventAgencyData getChannelEventAgencyData(Long channelId, Integer eventId, Long agencyId) {
        ChannelEventAgency channelEvent = catalogChannelEventAgencyCouchDao.get(channelId.toString(), eventId.toString(), agencyId.toString());
        if (channelEvent == null) {
            throw new EventIndexationFullReload("channel: " + channelId + " - agency " + agencyId);
        }
        ChannelEventAgencyData channelEventData = ChannelEventAgencyDataBuilder.buildChannelEventAgencyData(channelId, eventId.longValue(), agencyId);
        channelEventData.setChannelEventAgency(channelEvent);
        return channelEventData;
    }

    private void fillChannelSessionsContext(EventIndexationContext ctx, Long channelId, Long agencyId,
                                            List<ChannelSessionAgency> channelSessions, List<SessionForCatalogRecord> sessions) {
        //When has no session filter all channels-agency-sessions are available in context as indexed
        if (ctx.getSessionFilter() == null) {
            List<ChannelSessionAgencyData> allChannelSessions = ctx.getDocumentsIndexed(ChannelSessionAgencyData.class);
            for (ChannelSessionAgencyData indexedChannelSession : allChannelSessions) {
                if (indexedChannelSession.getChannelSessionAgency().getChannelId().equals(channelId) &&
                        indexedChannelSession.getChannelSessionAgency().getAgencyId().equals(agencyId)) {
                    channelSessions.add(indexedChannelSession.getChannelSessionAgency());
                }
            }
        } else {
            List<Key> channelSessionKeys = ctx.getAllSessions().stream()
                    .filter(CatalogPublishableUtils::isSessionToIndex)
                    .map(s -> new Key(new String[]{
                            channelId.toString(), s.getIdsesion().toString(), agencyId.toString()})).collect(Collectors.toList());
            channelSessions.addAll(catalogChannelSessionAgencyCouchDao.bulkGet(channelSessionKeys));
        }
        Set<Long> channelSessionIds = channelSessions.stream().map(ChannelSessionAgency::getSessionId).collect(Collectors.toSet());
        sessions.addAll(ctx.getAllSessions().stream()
                .filter(s -> channelSessionIds.contains(s.getIdsesion().longValue())).toList());
    }

    private static List<ChannelSessionAgency> getChannelSessions(BaseIndexationContext<?, ?> ctx, Long channelId, final Long agencyId) {
        return ctx.getDocumentsIndexed(ChannelSessionAgencyData.class).stream()
                .filter(cs ->
                        cs.getChannelSessionAgency().getChannelId().equals(channelId) && cs.getChannelSessionAgency().getAgencyId().equals(agencyId)
                )
                .map(ChannelSessionAgencyData::getChannelSessionAgency)
                .collect(Collectors.toList());
    }

    private List<ChannelEventAgencyData> buildChangedOccupations(OccupationIndexationContext ctx) {
        var channelEventAgencies = ctx.getChannelEventAgencies();
        if (CollectionUtils.isEmpty(channelEventAgencies)) {
            return null;
        }
        return channelEventAgencies.stream()
                .map(channelEventData -> buildOccupation(ctx, channelEventData))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ChannelEventAgencyData buildOccupation(OccupationIndexationContext ctx, ChannelEventAgencyData channelEventData) {
        ChannelEventAgency channelEvent = channelEventData.getChannelEventAgency();
        Long eventId = channelEvent.getEventId();
        Long channelId = channelEvent.getChannelId();
        Long agencyId = channelEvent.getAgencyId();
        List<ChannelSessionAgency> channelSessions = getFreshOccupationChannelSessions(ctx, eventId, channelId, agencyId);
        ChannelCatalogEventInfo catalogInfo = ChannelCatalogInfoMerger.mergeOccupation(channelSessions);

        //Skip update of channelEvent if there are no changes
        if (catalogInfo != null && channelEvent.getCatalogInfo() != null &&
                (Objects.equals(channelEvent.getCatalogInfo().getSoldOut(), catalogInfo.getSoldOut()) &&
                        Objects.equals(channelEvent.getCatalogInfo().getPrices(), catalogInfo.getPrices()))) {
            return null;
        }

        return ChannelEventAgencyDataBuilder.builder()
                .agencyId(agencyId)
                .channelId(channelId)
                .eventId(eventId)
                .catalogInfo(catalogInfo)
                .build();
    }

    private List<ChannelSessionAgency> getFreshOccupationChannelSessions(BaseIndexationContext<?, ?> ctx, Long eventId, Long channelId, Long agencyId) {
        List<ChannelSessionAgencyData> channelSessions = channelSessionAgencyElasticDao.getOccupationFieldsByEventAndChannelId(eventId, channelId, agencyId);
        List<ChannelSessionAgency> freshChannelSessions = getChannelSessions(ctx, channelId, agencyId);
        Map<Long, ChannelSessionAgency> freshChannelSessionsMap = freshChannelSessions.stream()
                .collect(Collectors.toMap(ChannelSessionAgency::getSessionId, java.util.function.Function.identity(), (existing, replacement) -> existing));
        List<ChannelSessionAgency> result = new java.util.ArrayList<>(channelSessions.size());
        for (ChannelSessionAgencyData old : channelSessions) {
            Long sessionId = old.getChannelSessionAgency().getSessionId();
            result.add(freshChannelSessionsMap.getOrDefault(sessionId, old.getChannelSessionAgency()));
        }
        return result;
    }


    private static Boolean includeB2B(CpanelCanalEventoRecord ce, EventIndexationContext ctx) {
        Long channelId = ce.getIdcanal().longValue();
        ChannelInfo channelInfo = ctx.getChannelInfo(channelId);
        if (channelInfo != null) {
            return channelInfo.isB2BChannel();
        }
        return true;
    }

    private void updateBasicChannelEventAgencyInfo(CpanelCanalEventoRecord channelEventRecord, Long agencyId, EventIndexationContext ctx, ChannelEventAgencyData channelEventAgencyData) {
        List<ChannelSessionAgency> channelSessions = new ArrayList<>();
        List<SessionForCatalogRecord> sessions = new ArrayList<>();
        fillChannelSessionsContext(ctx, channelEventRecord.getIdcanal().longValue(), agencyId, channelSessions, sessions);

        channelEventAgencyData.setMustBeIndexed(CollectionUtils.isNotEmpty(sessions));
        updateBasicChannelEventInfo(channelEventRecord, channelEventAgencyData.getChannelEventAgency(), sessions, channelSessions, ctx);
    }

}
