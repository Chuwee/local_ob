package es.onebox.event.catalog.elasticsearch.indexer;

import com.google.common.collect.Lists;
import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelConfigCB;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.attendants.MandatoryAttendantsCalculator;
import es.onebox.event.catalog.elasticsearch.builder.ChannelSessionAgencyDataBuilder;
import es.onebox.event.catalog.elasticsearch.builder.ChannelSessionOccupationBuilder;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionAgencyForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionAgencyForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.catalog.elasticsearch.exception.CatalogIndexerException;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrixCalculator;
import es.onebox.event.catalog.elasticsearch.secondarymarket.SecondaryMarketCalculator;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.catalog.elasticsearch.utils.IndexerUtils;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ChannelSessionAgencyDataIndexer extends ChannelSessionIndexer {

    private static final int MAX_PARTITION_SIZE = 500;

    private final ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao;
    private final CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao;

    public ChannelSessionAgencyDataIndexer(EventPromotionsService eventPromotionsService,
                                           SessionElasticDao sessionElasticDao,
                                           SessionRepository sessionRepository,
                                           PriceEngineSimulationService priceEngineSimulationService,
                                           SBSessionsCouchDao sbSessionsCouchDao,
                                           CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao,
                                           ChannelSessionAgencyElasticDao channelSessionAgencyElasticDao,
                                           ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                           CacheRepository localCacheRepository,
                                           StaticDataContainer staticDataContainer) {
        super(eventPromotionsService, sessionElasticDao, sessionRepository, priceEngineSimulationService, sbSessionsCouchDao,
                channelEventCommunicationElementDao, localCacheRepository, staticDataContainer);
        this.catalogChannelSessionAgencyCouchDao = catalogChannelSessionAgencyCouchDao;
        this.channelSessionAgencyElasticDao = channelSessionAgencyElasticDao;
    }

    public void indexChannelAgencySessions(EventIndexationContext ctx) {
        if (EventIndexationType.PARTIAL_COM_ELEMENTS.equals(ctx.getType()) ||
                EventIndexationType.SEASON_TICKET.equals(ctx.getType())) {
            return;
        }

        List<ChannelSessionAgencyForEventIndexation> channelAgencySessionsToIndex = ctx.getChannelAgencySessionsToIndex();

        Map<Long, List<ChannelSessionAgencyForEventIndexation>> channelSessionsByAgency = channelAgencySessionsToIndex
                .stream()
                .collect(groupingBy(ChannelSessionAgencyForEventIndexation::getAgencyId));

        final List<ChannelSessionAgencyData> oldChannelSessions;
        Long eventId = ctx.getEventId();
        List<Long> agencyIds = new ArrayList<>();
        if (ctx.getChannelsWithAgencies() != null && !ctx.getChannelsWithAgencies().isEmpty()) {
            ctx.getChannelsWithAgencies().values().forEach(ca -> agencyIds.addAll(ca.values().stream().map(ChannelAgency::getId).collect(Collectors.toList())));
        }
        if(ctx.getSessionFilter() == null) {
            oldChannelSessions = channelSessionAgencyElasticDao.getByEventId(eventId, agencyIds);
        } else {
            oldChannelSessions = channelSessionAgencyElasticDao.getBySessionId(ctx.getSessionFilter(), eventId, agencyIds);
        }

        Set<String> newChannelSessionIds = new HashSet<>();
        List<ChannelSessionAgencyData> allChannelSessionsAgencies = new ArrayList<>();

        for (Map.Entry<Long, List<ChannelSessionAgencyForEventIndexation>> entry : channelSessionsByAgency.entrySet()) {
            List<ChannelSessionAgencyData> allChannelSessions = buildChannelSessions(ctx, entry.getKey(), entry.getValue(), oldChannelSessions);
            allChannelSessionsAgencies.addAll(allChannelSessions);

            if (CollectionUtils.isNotEmpty(allChannelSessions)) {
                if (allChannelSessions.size() == 1) {
                    ChannelSessionAgency doc = IndexerUtils.toCouchAdapterForAgency(allChannelSessions).get(0);
                    String key = doc.getChannelId() + "_" + doc.getSessionId() + "_" + doc.getAgencyId();
                    this.catalogChannelSessionAgencyCouchDao.upsert(key, doc);
                } else {
                    List<ChannelSessionAgency> docs = IndexerUtils.toCouchAdapterForAgency(allChannelSessions);
                    this.catalogChannelSessionAgencyCouchDao.bulkUpsert(docs);
                }
            }

            //EYE DANGER: VERY IMPORTANT CHANGE TO KEEP INDEX.
            var channelSessions = allChannelSessions.stream()
                    .filter(ChannelSessionAgencyData::getMustBeIndexed)
                    .map(IndexerUtils.cleanContainerOccupationForAgency())
                    .collect(Collectors.toList());

            String routing = EventDataUtils.getEventKey(eventId);

            if (CollectionUtils.isNotEmpty(channelSessions) && EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
                if (channelSessions.size() == 1) {
                    channelSessionAgencyElasticDao.upsert(channelSessions.get(0), routing);
                    ctx.addDocumentsIndexed(channelSessions);
                    newChannelSessionIds.add(channelSessions.get(0).getId());
                } else {
                    List<List<ChannelSessionAgencyData>> partition = Lists.partition(channelSessions, MAX_PARTITION_SIZE);
                    partition.forEach(chs -> channelSessionAgencyElasticDao.bulkUpsert(false, routing, chs.toArray(new ChannelSessionAgencyData[0])));
                    ctx.addDocumentsIndexed(channelSessions);
                    for (ChannelSessionAgencyData channelSession : channelSessions) {
                        newChannelSessionIds.add(channelSession.getId());
                    }
                }
            }
        }

        String routing = EventDataUtils.getEventKey(eventId);
        removeOldChannelSessions(routing, oldChannelSessions, newChannelSessionIds, ctx, allChannelSessionsAgencies);
    }

    public void indexChannelAgencyOccupation(OccupationIndexationContext ctx) {
        if (CollectionUtils.isEmpty(ctx.getChannelAgencySessionsToIndex())) {
            return;
        }
        List<ChannelSessionAgencyData> occupations = ctx.getChannelAgencySessionsToIndex().stream()
                .map(channelSessionData -> buildChannelSessionForOccupation(ctx, channelSessionData))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(occupations)) {
            List<ChannelSessionAgency> cbUpdate = new ArrayList<>();
            occupations.stream().map(ChannelSessionAgencyData::getChannelSessionAgency).forEach(es -> {
                ChannelSessionAgency cb = catalogChannelSessionAgencyCouchDao.get(es.getChannelId().toString(), es.getSessionId().toString(), es.getAgencyId().toString());
                updateCBOccupation(es, cb, cbUpdate);
            });
            if (CollectionUtils.isNotEmpty(cbUpdate)) {
                catalogChannelSessionAgencyCouchDao.bulkUpsert(cbUpdate);
            }

            var channelSessions = occupations.stream()
                    .map(IndexerUtils.cleanContainerOccupationForAgency())
                    .collect(Collectors.toList());
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            List<List<ChannelSessionAgencyData>> partition = Lists.partition(channelSessions, MAX_PARTITION_SIZE);
            partition.forEach(chs -> channelSessionAgencyElasticDao.bulkUpsert(true,
                    routing, chs.toArray(new ChannelSessionAgencyData[0])));
            ctx.addDocumentsIndexed(channelSessions);
        }
    }

    private List<ChannelSessionAgencyData> buildChannelSessions(EventIndexationContext ctx, Long agencyId,
                                                                List<ChannelSessionAgencyForEventIndexation> channelSessionsToIndex,
                                                                List<ChannelSessionAgencyData> oldChannelSessions) {
        if (EventIndexationType.PARTIAL_BASIC.equals(ctx.getType())) {
            List<ChannelSessionAgencyData> agencyOldChannelSessions = oldChannelSessions.stream()
                    .filter(cs -> cs.getChannelSessionAgency().getAgencyId().equals(agencyId))
                    .collect(Collectors.toList());
            return updateBasicChannelSessionAgencyInfo(ctx, agencyOldChannelSessions);
        }

        return channelSessionsToIndex.stream()
                .map(channelSession -> buildChannelSessionForEvent(channelSession, ctx))
                .collect(Collectors.toList());
    }

    private void removeOldChannelSessions(String routing, List<ChannelSessionAgencyData> oldChannelSessions,
                                          Set<String> idsIndexed, EventIndexationContext ctx,
                                          List<ChannelSessionAgencyData> allChannelSessions) {
        Set<ChannelSessionAgencyData> csToRemove = new HashSet<>();
        for (ChannelSessionAgencyData cs : oldChannelSessions) {
            if (!idsIndexed.contains(cs.getId())) {
                csToRemove.add(cs);
            }
        }

        if (!EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
            csToRemove.addAll(allChannelSessions.stream().toList());
        }

        if (CollectionUtils.isNotEmpty(csToRemove)) {
            if (csToRemove.size() == 1) {
                List<String> esIds = csToRemove.stream().map(ChannelSessionAgencyData::getId).toList();
                channelSessionAgencyElasticDao.deleteById(esIds.get(0));

                List<Key> cbIds = csToRemove.stream().map(cs -> new Key(new String[]{
                                cs.getChannelSessionAgency().getChannelId().toString(),
                                cs.getChannelSessionAgency().getSessionId().toString(),
                                cs.getChannelSessionAgency().getAgencyId().toString()}))
                        .collect(Collectors.toList());
                catalogChannelSessionAgencyCouchDao.remove(cbIds.get(0).toString());
            } else {
                List<String> esIds = csToRemove.stream().map(ChannelSessionAgencyData::getId).toList();
                channelSessionAgencyElasticDao.bulkDelete(routing, esIds.toArray(new String[0]));

                List<Key> cbIds = csToRemove.stream().map(cs -> new Key(new String[]{
                                cs.getChannelSessionAgency().getChannelId().toString(),
                                cs.getChannelSessionAgency().getSessionId().toString(),
                                cs.getChannelSessionAgency().getAgencyId().toString()}))
                        .collect(Collectors.toList());
                catalogChannelSessionAgencyCouchDao.bulkRemove(cbIds);
            }
        }
    }

    private ChannelSessionAgencyData buildChannelSessionForEvent(ChannelSessionAgencyForEventIndexation in,
                                                                 EventIndexationContext ctx) {
        Long sessionId = in.getSessionId();
        Long channelId = in.getChannelId();
        Long agencyId = in.getAgencyId();
        try {
            //Basic data
            CpanelSesionRecord session = in.getSession();
            CpanelCanalEventoRecord channelEvent = in.getChannelEvent();
            String timeZone = ctx.getVenueBySessionId(sessionId)
                    .map(VenueRecord::getTimeZone)
                    .orElse(ZoneOffset.UTC.getId());
            ChannelCatalogDates dates = getChannelSessionDates(session, channelEvent, timeZone, ctx);

            boolean secMktEventChannelForSale = SecondaryMarketCalculator.calculateSaleChannelEvent(ctx, channelId);
            boolean forSale = getForSale(session, channelEvent, secMktEventChannelForSale);

            //Full data
            Optional<SessionData> sessionData = ctx.getDocumentsIndexed(SessionData.class)
                    .stream()
                    .filter(s -> s.getSession().getSessionId().equals(sessionId))
                    .findAny();
            List<SessionRate> rates = sessionData.map(SessionData::getSession)
                    .map(Session::getRates)
                    .orElse(Collections.emptyList());
            Long venueConfigId = sessionData.map(SessionData::getSession).map(Session::getVenueConfigId).orElse(null);

            List<Long> priceZonesWithAvailability = new ArrayList<>(in.getPriceZonesWithAvailability());

            List<SecondaryMarketSearch> secondaryMarketData = SecondaryMarketCalculator
                    .decoratePriceZoneAvailability(in, ctx, priceZonesWithAvailability);

            ChannelInfo channel = in.getChannel();
            Boolean mandatoryAttendants = MandatoryAttendantsCalculator.init()
                    .channelId(channelId.intValue())
                    .channelSubtypeId(channel.getSubtypeId())
                    .eventAttendantsConfig(in.getEventAttendantsConfig())
                    .sessionAttendantsConfig(in.getSessionAttendantsConfig())
                    .calculate();

            SecondaryMarketCalculator.buildSecondaryOccupation(in, secondaryMarketData);

            List<ChannelTaxInfo> channelSurchargesTaxes = in.getChannelSurchargesTaxes() == null ? new ArrayList<>() : in.getChannelSurchargesTaxes();

            ChannelSessionAgencyData data = ChannelSessionAgencyDataBuilder.builder()
                    .channelId(channelId)
                    .sessionId(sessionId)
                    .agencyId(agencyId)
                    //Basic
                    .dates(dates)
                    .forSale(forSale)
                    //Full
                    .eventId(channelEvent.getIdevento().longValue())
                    .venueConfigId(venueConfigId)
                    .timeZone(timeZone)
                    .presale(in.getPresales())
                    .preview(session.getIspreview())
                    .seasonPackSession(CommonUtils.isTrue(session.getEsabono()))
                    .quotas(in.getQuotas())
                    .mandatoryAttendants(mandatoryAttendants)
                    .productIds(in.getProducts())
                    .relatedPacksByPackId(in.getRelatedPacksByPackId())
                    .secondaryMarketConfig(in.getSecondaryMarketConfig())
                    .mustBeIndexed(in.getMustBeIndexed())
                    .priceTypeTags(getPriceTypeTags(ctx, venueConfigId, in.getPriceZones(), channelId))
                    .channelTaxes(buildChannelTaxes(channelSurchargesTaxes))
                    .build();

            //Occupation data
            Boolean primaryMktForSaleChannel = channelEvent.getEnventa() == 1;
            boolean soldOut = calculateSoldOut(ctx.getSecondaryMarketForSale(), primaryMktForSaleChannel,
                    secMktEventChannelForSale, sessionId, in.getSecondaryMarketConfig(), in.getQuotas(),
                    priceZonesWithAvailability, in.getContainerOccupations());
            List<Long> promotions = preparePromotions(ctx, sessionId, channelId, in.getPriceZones(), rates);
            List<SessionTaxInfo> sessionTaxes = sessionData.map(SessionData::getSession).map(Session::getTaxes).orElse(Collections.emptyList());
            List<SessionTaxInfo> surchargesTaxes = sessionData.map(SessionData::getSession).map(Session::getSurchargesTaxes).orElse(Collections.emptyList());
            PriceMatrix priceMatrix = PriceMatrixCalculator.builder()
                    .prices(ctx.getVenueTemplatePrices())
                    .channelEventSurcharges(ctx.getChannelSurcharges().get(channelId))
                    .eventPromotions(ctx.getEventPromotions())
                    .sessionPromotions(promotions)
                    .secondaryMarket(secondaryMarketData)
                    .priceZonesBySession(priceZonesWithAvailability)
                    .rates(rates)
                    .sessionTaxes(sessionTaxes)
                    .surchargesTaxes(surchargesTaxes)
                    .channelSurchargesTaxes(channelSurchargesTaxes)
                    .build();

            ChannelSessionOccupationBuilder.builder(data.getChannelSessionAgency())
                    .soldOut(soldOut)
                    .promotions(promotions)
                    .priceMatrix(priceMatrix)
                    .priceZoneOccupations(in.getPriceZoneOccupations())
                    .containerOccupation(in.getContainerOccupations())
                    .buildOccupation();

            return data;


        } catch (Exception e) {
            throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event:%d - session:%d - channel:%d . Error indexing channel session", ctx.getEventId(), sessionId, channelId), e);
        }
    }

    private Map<Long, Set<String>> getPriceTypeTags(EventIndexationContext ctx, Long venueConfigId, List<Long> priceTypeIds, Long channelId) {
        if (MapUtils.isNotEmpty(ctx.getTemplateElementInfoTags())
                && MapUtils.isNotEmpty(ctx.getTemplateElementInfoTags().get(venueConfigId))
                && isAllowPriceTypesFilterInChannelConfig(ctx.getChannelConfigsCB(), channelId.intValue())) {
            Map<Long, Set<String>> priceTypeTags = ctx.getTemplateElementInfoTags().get(venueConfigId);
            if (MapUtils.isNotEmpty(priceTypeTags) && CollectionUtils.isNotEmpty(priceTypeIds)) {
                return priceTypeTags.entrySet().stream()
                        .filter(entry -> priceTypeIds.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        }
        return null;
    }

    private boolean isAllowPriceTypesFilterInChannelConfig(Map<Integer, ChannelConfigCB> channelConfigsCB, Integer channelId) {
        return MapUtils.isNotEmpty(channelConfigsCB) && channelConfigsCB.containsKey(channelId)
                && BooleanUtils.isTrue(channelConfigsCB.get(channelId).getAllowPriceTypeTagFilter());
    }


    private ChannelSessionAgencyData buildChannelSessionForOccupation(OccupationIndexationContext ctx,
                                                                      ChannelSessionAgencyForOccupationIndexation channelSessionToIndex) {
        EventData event = ctx.getEventData();
        ChannelSessionAgency in = channelSessionToIndex.getChannelSessionIndexed();
        Long eventId = in.getEventId();
        Long sessionId = in.getSessionId();
        Long channelId = in.getChannelId();
        Long agencyId = in.getAgencyId();
        Optional<SessionData> session = ctx.getSessionOr(sessionId, id -> sessionElasticDao.get(id, eventId));

        List<SecondaryMarketSearch> secondaryMarketItems = SecondaryMarketCalculator.getSecondaryMarketItems(ctx.getSecondaryMarketForSale(),
                in.getSecondaryMarketConfig(), in.getQuotas(), sessionId);

        ChannelEventSurcharges surcharges = ctx.getChannelEvent(channelId)
                .map(ChannelEventData::getChannelEvent)
                .map(ChannelEvent::getSurcharges)
                .orElse(null);

        List<SessionRate> rates = session.map(SessionData::getSession).map(Session::getRates).orElse(Collections.emptyList());
        List<Long> promotions = preparePromotions(ctx, sessionId, channelId, channelSessionToIndex.getPriceZones(), rates);

        List<Long> priceZonesWithAvailability = channelSessionToIndex.getPriceZonesWithAvailability();
        List<SessionTaxInfo> sessionTaxes = session.map(SessionData::getSession).map(Session::getTaxes).orElse(Collections.emptyList());
        List<SessionTaxInfo> surchargesTaxes = session.map(SessionData::getSession).map(Session::getSurchargesTaxes).orElse(Collections.emptyList());
        List<ChannelTaxInfo> channelSurchargesTaxes = in.getChannelTaxes() == null ? new ArrayList<>() : in.getChannelTaxes().getSurcharges();
        PriceMatrix priceMatrix = PriceMatrixCalculator.builder()
                .prices(event.getEvent().getPrices())
                .channelEventSurcharges(surcharges)
                .eventPromotions(ctx.getEventPromotions())
                .sessionPromotions(promotions)
                .priceZonesBySession(priceZonesWithAvailability)
                .rates(rates)
                .secondaryMarket(secondaryMarketItems)
                .sessionTaxes(sessionTaxes)
                .surchargesTaxes(surchargesTaxes)
                .channelSurchargesTaxes(channelSurchargesTaxes == null ? new ArrayList<>() : channelSurchargesTaxes)
                .build();

        List<SessionOccupationVenueContainer> occupation = channelSessionToIndex.getContainerOccupations();
        SecondaryMarketCalculator.decorateSecondaryMarketAvailability(secondaryMarketItems, occupation);

        Boolean primaryMktForSaleChannel = ctx.getChannelEvent(channelId)
                .map(ChannelEventData::getChannelEvent)
                .map(ChannelEvent::getPurchaseChannelEvent)
                .orElse(null);
        Boolean secMktForSaleChannel = ctx.getChannelEvent(channelId)
                .map(ChannelEventData::getChannelEvent)
                .map(ChannelEvent::getPurchaseSecondaryMarketChannelEvent)
                .orElse(null);
        boolean soldOut = calculateSoldOut(ctx.getSecondaryMarketForSale(), primaryMktForSaleChannel, secMktForSaleChannel,
                sessionId, in.getSecondaryMarketConfig(), in.getQuotas(), priceZonesWithAvailability, in.getContainerOccupations());

        ChannelSessionAgencyData data = ChannelSessionAgencyDataBuilder.builder()
                .channelId(channelId)
                .sessionId(sessionId)
                .agencyId(agencyId)
                .build();

        ChannelSessionOccupationBuilder.builder(data.getChannelSessionAgency())
                .soldOut(soldOut)
                .promotions(promotions)
                .priceMatrix(priceMatrix)
                .priceZoneOccupations(in.getPriceZoneOccupations())
                .containerOccupation(in.getContainerOccupations())
                .buildOccupation();

        return data;
    }

    private List<ChannelSessionAgencyData> updateBasicChannelSessionAgencyInfo(EventIndexationContext ctx, List<ChannelSessionAgencyData> esChannelSessions) {
        Map<Long, List<ChannelSessionAgencyData>> csByChannel = esChannelSessions.stream()
                .collect(groupingBy(cs -> cs.getChannelSessionAgency().getChannelId()));

        checkReadySessionsAlreadyOnES(ctx, esChannelSessions.stream()
                .map(cs -> cs.getChannelSessionAgency().getSessionId().intValue()).toList());

        List<ChannelSessionAgencyData> channelSessionsToIndex = new ArrayList<>();
        for (Map.Entry<Long, List<ChannelSessionAgencyData>> channelCs : csByChannel.entrySet()) {
            Long channelId = channelCs.getKey();
            List<Key> channelSessionAgencyKeys = channelCs.getValue().stream()
                    .map(s -> new Key(new String[]{
                            channelId.toString(), s.getChannelSessionAgency().getSessionId().toString(), s.getChannelSessionAgency().getAgencyId().toString()}))
                    .collect(Collectors.toList());
            List<ChannelSessionAgency> channelSessions = catalogChannelSessionAgencyCouchDao.bulkGet(channelSessionAgencyKeys);
            boolean secMktEventChannelForSale = SecondaryMarketCalculator.calculateSaleChannelEvent(ctx, channelId);
            for (ChannelSessionAgency channelSession : channelSessions) {
                Long sessionId = channelSession.getSessionId();
                SessionForCatalogRecord session = ctx.getSession(sessionId.intValue());
                CpanelCanalEventoRecord channelEvent = ctx.getChannelEvent(channelSession.getChannelId().intValue());
                if (CatalogPublishableUtils.isSessionToIndex(session)) {
                    channelSession.setDate(getChannelSessionDates(session, channelEvent, channelSession.getTimeZone(), ctx));
                    channelSession.setForSale(getForSale(session, channelEvent, secMktEventChannelForSale));
                    ChannelSessionAgencyData csData = ChannelSessionAgencyDataBuilder.buildChannelSessionAgencyData(channelId, sessionId, channelSession.getAgencyId());
                    csData.setChannelSessionAgency(channelSession);
                    csData.setMustBeIndexed(CatalogPublishableUtils.isSessionPublishable(session, ctx.getEvent(), channelEvent));
                    channelSessionsToIndex.add(csData);
                }
            }
        }
        return channelSessionsToIndex;
    }

}
