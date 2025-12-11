package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.couch.CatalogVenueConfigPricesSimulation;
import es.onebox.event.catalog.dao.couch.smartbooking.SBPriceList;
import es.onebox.event.catalog.dao.couch.smartbooking.SBPriceZone;
import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.builder.ChannelCatalogDatesBuilder;
import es.onebox.event.catalog.elasticsearch.builder.ChannelSessionComElementsBuilder;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelTaxes;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.catalog.elasticsearch.secondarymarket.SecondaryMarketCalculator;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.record.CommElementRecord;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.exception.EventIndexationFullReload;
import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfig;
import es.onebox.event.priceengine.simulation.domain.RateMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.promotions.utils.PromotionUtils;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.event.config.LocalCache.SESSION_CHANNEL_BANNER_KEY;
import static es.onebox.event.config.LocalCache.SESSION_CHANNEL_COM_TTL;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class ChannelSessionIndexer {

    protected final SessionElasticDao sessionElasticDao;
    protected final SessionRepository sessionRepository;
    protected final PriceEngineSimulationService priceEngineSimulationService;
    protected final EventPromotionsService eventPromotionsService;
    protected final SBSessionsCouchDao sbSessionsCouchDao;
    protected final ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    protected final CacheRepository localCacheRepository;
    protected final StaticDataContainer staticDataContainer;

    private static final int LIMIT_QUOTAS = 200;


    protected ChannelSessionIndexer(EventPromotionsService eventPromotionsService,
                                    SessionElasticDao sessionElasticDao,
                                    SessionRepository sessionRepository,
                                    PriceEngineSimulationService priceEngineSimulationService,
                                    SBSessionsCouchDao sbSessionsCouchDao,
                                    ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                    CacheRepository localCacheRepository, StaticDataContainer staticDataContainer) {
        this.eventPromotionsService = eventPromotionsService;
        this.sessionElasticDao = sessionElasticDao;
        this.sessionRepository = sessionRepository;
        this.priceEngineSimulationService = priceEngineSimulationService;
        this.sbSessionsCouchDao = sbSessionsCouchDao;
        this.channelEventCommunicationElementDao = channelEventCommunicationElementDao;
        this.localCacheRepository = localCacheRepository;
        this.staticDataContainer = staticDataContainer;
    }


    protected Map<Integer, VenueConfigMap> getVenueConfigMap(ChannelSessionForEventIndexation channelSession,
                                                             List<EventPriceRecord> sessionPrices, Boolean isActivity,
                                                             Map<String, VenueConfigMap> venueConfigMapCache,
                                                             Map<String, List<EventPriceRecord>> sessionQuotasPriceZonesCache) {
        EventPriceRecord eventPrice = sessionPrices.get(0);
        Long sessionTemplateId = eventPrice.getVenueConfigId().longValue();
        String currentSessionVenueConfigName = eventPrice.getVenueConfigName();
        VenueConfigMap vcm = new VenueConfigMap(sessionTemplateId, currentSessionVenueConfigName, null);
        Map<Integer, RateMap> rates = buildRates(channelSession, isActivity, sessionPrices, sessionQuotasPriceZonesCache, venueConfigMapCache);
        vcm.setRate(rates);
        return Map.of(vcm.getId().intValue(), vcm);
    }

    protected void updateSBOriginalPrice(SBSession sbSession, CatalogVenueConfigPricesSimulation simulation) {
        if (sbSession != null) {
            simulation.getRates().forEach(rate -> {
                if (rate.isDefaultRate()) {
                    rate.getPriceTypes().forEach(priceType -> {
                        if (MapUtils.isNotEmpty(sbSession.getPriceZonesMapping())) {
                            SBPriceZone sbPriceZone = sbSession.getPriceZonesMapping().get(priceType.getId());
                            if (sbPriceZone != null && sbSession.getPartnerPriceListName() != null) {
                                SBPriceList priceList = null;
                                for (Map.Entry<String, SBPriceList> entry : sbPriceZone.getPriceListMap().entrySet()) {
                                    if (!entry.getKey().equals(sbSession.getPartnerPriceListName())) {
                                        priceList = entry.getValue();
                                        break;
                                    }
                                }
                                if (priceList != null && priceList.getInitialPrice() != null) {
                                    Double initialPrice = priceList.getInitialPrice();
                                    priceType.getPrice().setOriginal(initialPrice);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    protected List<Long> preparePromotions(BaseIndexationContext<?, ?> ctx, Long sessionId, Long channelId, List<Long> priceZones, List<SessionRate> rates) {
        List<EventPromotion> promotions = PromotionUtils.filterBySession(ctx.getEventPromotions(), sessionId);
        promotions = PromotionUtils.filterByChannel(promotions, channelId);
        promotions = PromotionUtils.filterByPriceZones(promotions, priceZones);
        promotions = PromotionUtils.filterByRates(promotions, rates.stream().map(SessionRate::getId).collect(Collectors.toList()));
        promotions = filterByPromotionCounters(promotions, sessionId, ctx.getEventId());

        return promotions.stream().map(EventPromotion::getEventPromotionTemplateId).distinct().collect(Collectors.toList());
    }

    private List<EventPromotion> filterByPromotionCounters(List<EventPromotion> promotions, Long sessionId, Long eventId) {
        return promotions.stream().filter(promo -> {
            if (promo.getRestrictions() != null) {
                boolean eventLimitIsReached = Boolean.FALSE;
                boolean sessionLimitIsReached = Boolean.FALSE;
                if (promo.getRestrictions().getEventLimit() != null && promo.getRestrictions().getEventLimit().getEnabled()) {
                    Long currentUsages = eventPromotionsService.getCurrentPromotionUsageByEventId(promo.getEventPromotionTemplateId(), eventId);
                    currentUsages = ObjectUtils.defaultIfNull(currentUsages, 0L);
                    eventLimitIsReached = currentUsages >= promo.getRestrictions().getEventLimit().getValue().longValue();
                }
                if (promo.getRestrictions().getSessionLimit() != null && promo.getRestrictions().getSessionLimit().getEnabled()) {
                    Long currentUsages = eventPromotionsService.getCurrentPromotionUsageBySessionId(promo.getEventPromotionTemplateId(), sessionId);
                    currentUsages = ObjectUtils.defaultIfNull(currentUsages, 0L);
                    sessionLimitIsReached = currentUsages >= promo.getRestrictions().getSessionLimit().getValue().longValue();
                }
                return !eventLimitIsReached && !sessionLimitIsReached;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
    }


    private Map<Integer, RateMap> buildRates(ChannelSessionForEventIndexation channelSession, Boolean isActivity,
                                             List<EventPriceRecord> sessionPrices,
                                             Map<String, List<EventPriceRecord>> sessionQuotasPriceZonesCache,
                                             Map<String, VenueConfigMap> venueConfigMapCache) {

        String venueConfigCacheKey = getVenueConfigRatesCacheKey(sessionPrices, channelSession.getQuotas());
        if (venueConfigMapCache.containsKey(venueConfigCacheKey) && Boolean.TRUE.equals(isActivity)) {
            return venueConfigMapCache.get(venueConfigCacheKey).getRate();
        }

        String sessionQuotasCacheKey = channelSession.getSessionId() + "_" + StringUtils.join(channelSession.getQuotas(), ",");
        List<EventPriceRecord> sessionQuotasPriceZones = sessionQuotasPriceZonesCache.get(sessionQuotasCacheKey);
        if (sessionQuotasPriceZones == null) {
            CapacityType type = Boolean.TRUE.equals(isActivity) ? CapacityType.SIMPLE : CapacityType.NORMAL;
            List<Long> filteredPriceZoneIds;
            if (CollectionUtils.isEmpty(channelSession.getQuotas()) || channelSession.getQuotas().size() <= LIMIT_QUOTAS) {
                filteredPriceZoneIds = sessionRepository.getSessionPriceZones(channelSession.getSessionId(), channelSession.getQuotas(), type);
            } else {
                filteredPriceZoneIds = new ArrayList<>();
                Set<Set<Long>> setOfQuotasIds = divideSet(new HashSet<>(channelSession.getQuotas()));
                for (Set<Long> quotas : setOfQuotasIds) {
                    List<Long> priceZones = sessionRepository.getSessionPriceZones(channelSession.getSessionId(), quotas.stream().toList(), type);
                    if (CollectionUtils.isNotEmpty(priceZones)) {
                        filteredPriceZoneIds.addAll(priceZones);
                    }
                }
            }
            sessionQuotasPriceZones = sessionPrices.stream()
                    .filter(ep -> filteredPriceZoneIds.contains(ep.getPriceZoneId().longValue()))
                    .sorted(Comparator.comparing(EventPriceRecord::getRateId).thenComparing(EventPriceRecord::getPriceZoneId))
                    .collect(Collectors.toList());
            sessionQuotasPriceZonesCache.put(sessionQuotasCacheKey, sessionQuotasPriceZones);
        }

        Map<Integer, RateMap> rates = new HashMap<>();
        for (EventPriceRecord sp : sessionQuotasPriceZones) {
            rates.putIfAbsent(sp.getRateId(), new RateMap(sp.getRateId(), sp.getRateName(), new ArrayList<>()));
            rates.get(sp.getRateId()).getPriceZones().add(new PriceZone(sp.getPriceZoneId().longValue(),
                    sp.getPrice(), new PriceZoneConfig(sp.getPriceZoneCode(), sp.getPriceZoneDescription())));
        }

        if (Boolean.TRUE.equals(isActivity)) {
            EventPriceRecord eventPriceRecord = sessionPrices.get(0);
            Long sessionTemplateId = eventPriceRecord.getVenueConfigId().longValue();
            String currentSessionVenueConfigName = eventPriceRecord.getVenueConfigName();
            VenueConfigMap vcm = new VenueConfigMap(sessionTemplateId, currentSessionVenueConfigName, null);
            vcm.setRate(rates);
            venueConfigMapCache.putIfAbsent(venueConfigCacheKey, vcm);
        }

        return rates;
    }

    public static Set<Set<Long>> divideSet(Set<Long> ids) {
        Set<Set<Long>> result = new HashSet<>();
        List<Long> idList = new ArrayList<>(ids);

        for (int i = 0; i < idList.size(); i += LIMIT_QUOTAS) {
            Set<Long> subset = new HashSet<>(idList.subList(i, Math.min(i + LIMIT_QUOTAS, idList.size())));
            result.add(subset);
        }

        return result;
    }

    private String getVenueConfigRatesCacheKey(List<EventPriceRecord> sessionPrices, List<Long> quotas) {
        Long sessionTemplateId = sessionPrices.get(0).getVenueConfigId().longValue();
        List<Integer> sessionRates = sessionPrices.stream()
                .map(EventPriceRecord::getRateId)
                .distinct()
                .toList();
        return sessionTemplateId + "_" + StringUtils.join(sessionRates, ",") + "_" + StringUtils.join(quotas, ",");
    }

    protected ChannelCatalogDates getChannelSessionDates(CpanelSesionRecord session,
                                                         CpanelCanalEventoRecord channelEvent,
                                                         String timeZone, EventIndexationContext ctx) {
        if (CommonUtils.isTrue(session.getEsabono())) {
            buildSessionPackDates(session, ctx);
        }
        return new ChannelCatalogDatesBuilder()
                .session(session)
                .channelEvent(channelEvent)
                .timeZone(timeZone)
                .build();
    }

    private void buildSessionPackDates(CpanelSesionRecord session, EventIndexationContext ctx) {
        List<Integer> sessionPackSessions = ctx.getSessionsBySessionPack().get(session.getIdsesion());
        if (CollectionUtils.isNotEmpty(sessionPackSessions)) {
            var sessions = ctx.getSessions().stream().filter(s -> sessionPackSessions.contains(s.getIdsesion())).toList();
            sessions.stream().min(Comparator.comparing(CpanelSesionRecord::getFechainiciosesion))
                    .map(CpanelSesionRecord::getFechainiciosesion)
                    .ifPresent(session::setFechainiciosesion);
            sessions.stream().max(Comparator.comparing(CpanelSesionRecord::getFechainiciosesion))
                    .map(CpanelSesionRecord::getFechainiciosesion)
                    .ifPresent(session::setFecharealfinsesion);
        }
    }

    protected boolean getForSale(CpanelSesionRecord session, CpanelCanalEventoRecord channelEvent, boolean secMktEventChannelForSale) {
        return session.getEnventa() == 1 && (channelEvent.getEnventa() == 1 || secMktEventChannelForSale);
    }

    protected void checkReadySessionsAlreadyOnES(EventIndexationContext ctx, List<Integer> esSessions) {
        List<Integer> publishableSessions = ctx.getSessions().stream()
                .filter(CatalogPublishableUtils::isSessionToIndex)
                .map(SessionForCatalogRecord::getIdsesion).toList();
        if (!new HashSet<>(esSessions).containsAll(publishableSessions)) {
            throw new EventIndexationFullReload("not all ready sessions on ES");
        }
    }

    protected <T extends ChannelSession> void updateCBOccupation(ChannelSession es, T cb, List<T> cbUpdate) {
        if (cb != null && es != null) {
            cb.setSoldOut(es.getSoldOut());
            cb.setPromotions(es.getPromotions());
            cb.setPrices(es.getPrices());
            cb.setPriceZoneOccupations(es.getPriceZoneOccupations());
            cb.setContainerOccupations(es.getContainerOccupations());
            cbUpdate.add(cb);
        }
    }

    protected void updateComElementsChannelEventInfo(CpanelCanalEventoRecord channelEventRecord, ChannelSession channelSession, EventIndexationContext ctx) {
        Integer channelId = channelEventRecord.getIdcanal();
        Integer eventId = channelEventRecord.getIdevento();
        Integer sessionId = channelSession.getSessionId().intValue();
        Object[] ecCacheKey = {eventId, channelId};


        List<CommElementRecord> ecBannerSquare = null;
        if (channelEventCommunicationElementDao != null) {
            Integer channelEventId = channelEventRecord.getIdcanaleevento();
            ecBannerSquare = localCacheRepository.cached(SESSION_CHANNEL_BANNER_KEY + "Square", SESSION_CHANNEL_COM_TTL, SECONDS,
                    () -> channelEventCommunicationElementDao.getCommElementByEventIdAndChannelIdAndSessionId(channelEventId, sessionId), ecCacheKey);
        }

        List<SessionCommunicationElement> commElements = ChannelSessionComElementsBuilder.builder(
                        sessionId,
                        eventId,
                        ctx,
                        staticDataContainer.getS3Repository()
                )
                .squareBanners(ecBannerSquare)
                .build();

        channelSession.setCommunicationElements(commElements);
    }

    protected ChannelTaxes buildChannelTaxes(List<ChannelTaxInfo> channelSurchargesTaxes) {
        ChannelTaxes channelTaxes = new ChannelTaxes();
        channelTaxes.setSurcharges(channelSurchargesTaxes);
        return channelTaxes;
    }

    protected Boolean calculateSoldOut(List<SecondaryMarketSearch> secondaryMarketSearchList, Boolean primaryMktForSaleChannel, Boolean secMktEventChannelForSale, Long sessionId,
                                       SecondaryMarketConfigDTO secondaryMarketConfig, List<Long> quotas, List<Long> priceZonesWithAvailability,
                                       List<SessionOccupationVenueContainer> containerOccupation) {

        boolean onlySecMktChannel = BooleanUtils.isFalse(primaryMktForSaleChannel) && BooleanUtils.isTrue(secMktEventChannelForSale);
        boolean onlyPrimaryMktChannel = BooleanUtils.isTrue(primaryMktForSaleChannel) && BooleanUtils.isFalse(secMktEventChannelForSale);

        List<SecondaryMarketSearch> secondaryMarketItems = SecondaryMarketCalculator.getSecondaryMarketItems(
                secondaryMarketSearchList, secondaryMarketConfig, quotas, sessionId);

        boolean soldOut = false;
        if (onlySecMktChannel) {
            soldOut = secondaryMarketItems == null || secondaryMarketItems.isEmpty();
        } else {
            soldOut = priceZonesWithAvailability == null || priceZonesWithAvailability.isEmpty();

            if (onlyPrimaryMktChannel) {
                // Change soldOut to true if event-channel has secondary market DISABLED but event has secondary items ON SALE
                if (!soldOut && CollectionUtils.isNotEmpty(secondaryMarketItems)
                        && containerOccupation != null) {

                    Map<Long, Long> secondaryCountsByViewId = secondaryMarketItems.stream()
                            .filter(item -> item.getTicket() != null && item.getTicket().getViewId() != null)
                            .collect(Collectors.groupingBy(item -> item.getTicket().getViewId(), Collectors.counting()));

                    long totalSecondaryItemsCount = secondaryMarketItems.size();

                    if (containerOccupation.stream().anyMatch(
                            cs -> cs.getOccupation().getStatus().containsKey(TicketStatus.AVAILABLE) &&
                                    cs.getOccupation().getStatus().get(TicketStatus.AVAILABLE) <= getSecondaryMktItemCountByViewId(
                                            cs.getId(), secondaryCountsByViewId, totalSecondaryItemsCount))) {
                        soldOut = true;
                    }
                }
            } else {
                // Change soldOut to false if secondary market is active and there's tickets AVAILABLE
                if (soldOut && CollectionUtils.isNotEmpty(secondaryMarketItems)
                        && containerOccupation != null
                        && containerOccupation.stream().anyMatch(
                        cs -> cs.getOccupation().getStatus().containsKey(TicketStatus.AVAILABLE) &&
                                cs.getOccupation().getStatus().get(TicketStatus.AVAILABLE) > 0)) {
                    soldOut = false;
                }
            }
        }

        return soldOut;
    }

    private long getSecondaryMktItemCountByViewId(Long viewId, Map<Long, Long> secondaryCountsByViewId, long totalSecondaryItemsCount) {
        long secondaryItemsAmount;
        if (viewId != null && !viewId.equals(0L)) {
            secondaryItemsAmount = secondaryCountsByViewId.getOrDefault(viewId, 0L);
        } else {
            secondaryItemsAmount = totalSecondaryItemsCount;
        }

        return secondaryItemsAmount;
    }
}
