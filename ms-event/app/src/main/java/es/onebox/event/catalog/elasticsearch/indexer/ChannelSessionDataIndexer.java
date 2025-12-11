package es.onebox.event.catalog.elasticsearch.indexer;

import com.google.common.collect.Lists;
import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.converter.CatalogPriceSimulationConverter;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.ChannelSessionPriceCouchDao;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.couch.CatalogSessionTaxInfo;
import es.onebox.event.catalog.dao.couch.CatalogVenueConfigPricesSimulation;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionTaxesForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.attendants.MandatoryAttendantsCalculator;
import es.onebox.event.catalog.elasticsearch.builder.ChannelSessionDataBuilder;
import es.onebox.event.catalog.elasticsearch.builder.ChannelSessionOccupationBuilder;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionCustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionPriceTypeLimit;
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
import es.onebox.event.catalog.utils.EventContextUtils;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.taxes.domain.CapacityRangeType;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.promotions.utils.PromotionUtils;
import es.onebox.event.sessions.converter.DynamicPriceConverter;
import es.onebox.event.sessions.domain.sessionconfig.CustomersLimits;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceZone;
import es.onebox.event.sessions.domain.sessionconfig.DynamicRatesPrice;
import es.onebox.event.sessions.domain.sessionconfig.PriceTypeLimit;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import es.onebox.event.sessions.enums.TaxType;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class ChannelSessionDataIndexer extends ChannelSessionIndexer {

    private final ChannelSessionElasticDao channelSessionElasticDao;
    private final ChannelSessionPriceCouchDao channelSessionPriceCouchDao;
    private final CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;

    private static final int MAX_PARTITION_SIZE = 500;

    @Autowired
    public ChannelSessionDataIndexer(SessionElasticDao sessionElasticDao,
                                     ChannelSessionElasticDao channelSessionElasticDao,
                                     ChannelSessionPriceCouchDao channelSessionPriceCouchDao,
                                     CatalogChannelSessionCouchDao catalogChannelSessionCouchDao,
                                     SessionRepository sessionRepository,
                                     PriceEngineSimulationService priceEngineSimulationService,
                                     EventPromotionsService eventPromotionsService,
                                     SBSessionsCouchDao sbSessionsCouchDao,
                                     ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                     CacheRepository localCacheRepository,
                                     StaticDataContainer staticDataContainer) {
        super(eventPromotionsService, sessionElasticDao, sessionRepository, priceEngineSimulationService, sbSessionsCouchDao,
                channelEventCommunicationElementDao, localCacheRepository, staticDataContainer);
        this.channelSessionElasticDao = channelSessionElasticDao;
        this.channelSessionPriceCouchDao = channelSessionPriceCouchDao;
        this.catalogChannelSessionCouchDao = catalogChannelSessionCouchDao;
    }

    public void indexChannelSessions(EventIndexationContext ctx) {
        if (EventIndexationType.PARTIAL_COM_ELEMENTS.equals(ctx.getType()) ||
                EventIndexationType.SEASON_TICKET.equals(ctx.getType())) {
            return;
        }

        Long eventId = ctx.getEventId();
        List<ChannelSessionData> oldChannelSessions;
        Long sessionFilter = ctx.getSessionFilter();
        if (sessionFilter != null) {
            oldChannelSessions = channelSessionElasticDao.getBySessionId(sessionFilter, eventId);
        } else {
            oldChannelSessions = channelSessionElasticDao.getByEventId(eventId);
        }

        List<ChannelSessionData> allChannelSessions = buildChannelSessions(ctx, oldChannelSessions);

        if (CollectionUtils.isNotEmpty(allChannelSessions)) {
            this.catalogChannelSessionCouchDao.bulkUpsert(IndexerUtils.toCouchAdapter(allChannelSessions));
        }

        storeChannelSessionPricesIntoCB(ctx);

        //EYE DANGER: VERY IMPORTANT CHANGE TO KEEP INDEX.
        var channelSessionsToIndex = allChannelSessions.stream()
                .filter(ChannelSessionData::getMustBeIndexed)
                .map(IndexerUtils.cleanContainerOccupation())
                .collect(Collectors.toList());

        String routing = EventDataUtils.getEventKey(eventId);

        if (CollectionUtils.isNotEmpty(channelSessionsToIndex) && EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
            List<List<ChannelSessionData>> partition = Lists.partition(channelSessionsToIndex, MAX_PARTITION_SIZE);
            partition.forEach(chs -> channelSessionElasticDao.bulkUpsert(false, routing, chs.toArray(new ChannelSessionData[0])));
            ctx.addDocumentsIndexed(channelSessionsToIndex);
        }
        removeOldChannelSessions(routing, oldChannelSessions, channelSessionsToIndex, ctx, allChannelSessions);
    }

    public List<ChannelSession> findSessionChannelProducts(List<Long> sessionIds, Long channelId) {
        List<ChannelSessionData> channelSessionData = channelSessionElasticDao.getBySessionAndChannelId(sessionIds, channelId);
        return channelSessionData.stream().map(ChannelSessionData::getChannelSession).collect(Collectors.toList());
    }

    public void indexOccupation(OccupationIndexationContext ctx) {
        List<ChannelSessionData> occupations = ctx.getChannelSessionsToIndex().stream()
                .map(channelSessionData -> buildChannelSessionForOccupation(ctx, channelSessionData))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(occupations)) {
            List<ChannelSession> cbUpdate = new ArrayList<>();

            Map<String, ChannelSession> channelSessionByKey = occupations.stream().map(ChannelSessionData::getChannelSession)
                    .collect(Collectors.toMap(ChannelSessionDataIndexer::buildKey, Function.identity()));
            List<Key> cbKeys = channelSessionByKey.values().stream().map(cs ->
                    new Key(new String[]{cs.getChannelId().toString(), cs.getSessionId().toString()})).toList();
            List<ChannelSession> cbChannelSessions = catalogChannelSessionCouchDao.bulkGet(cbKeys);
            for (ChannelSession cb : cbChannelSessions) {
                ChannelSession es = channelSessionByKey.get(buildKey(cb));
                updateCBOccupation(es, cb, cbUpdate);
            }
            if (CollectionUtils.isNotEmpty(cbUpdate)) {
                catalogChannelSessionCouchDao.bulkUpsert(cbUpdate);
            }

            var channelSessions = occupations.stream()
                    .map(IndexerUtils.cleanContainerOccupation())
                    .collect(Collectors.toList());
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            List<List<ChannelSessionData>> partition = Lists.partition(channelSessions, MAX_PARTITION_SIZE);
            partition.forEach(chs -> channelSessionElasticDao.bulkUpsert(true, routing, chs.toArray(new ChannelSessionData[0])));
            ctx.addDocumentsIndexed(channelSessions);
        }
    }

    private static String buildKey(ChannelSession cb) {
        return cb.getChannelId() + "_" + cb.getSessionId();
    }

    private List<ChannelSessionData> buildChannelSessions(EventIndexationContext ctx, List<ChannelSessionData> channelSessions) {

        if (EventIndexationType.PARTIAL_BASIC.equals(ctx.getType())) {
            return updateBasicChannelSessionInfo(ctx, channelSessions);
        }

        return ctx.getChannelSessionsToIndex().stream()
                .map(channelSession -> buildChannelSessionForEvent(channelSession, ctx))
                .collect(Collectors.toList());
    }

    private void removeOldChannelSessions(String routing, List<ChannelSessionData> oldChannelSessions,
                                          List<ChannelSessionData> channelSessionsToIndex,
                                          EventIndexationContext ctx,
                                          List<ChannelSessionData> allChannelSessions) {
        Set<String> idsIndexed = channelSessionsToIndex.stream().map(ChannelSessionData::getId).collect(Collectors.toSet());
        Set<ChannelSessionData> csToRemove = oldChannelSessions.stream()
                .filter(id -> !idsIndexed.contains(id.getId())).collect(Collectors.toSet());
        if (!EventStatus.READY.getId().equals(ctx.getEvent().getEstado())) {
            csToRemove.addAll(allChannelSessions.stream().toList());
        }
        if (CollectionUtils.isNotEmpty(csToRemove)) {
            List<String> esIds = csToRemove.stream().map(ChannelSessionData::getId).toList();
            channelSessionElasticDao.bulkDelete(routing, esIds.toArray(new String[0]));

            List<Key> cbIds = csToRemove.stream().map(cs -> new Key(new String[]{
                            cs.getChannelSession().getChannelId().toString(), cs.getChannelSession().getSessionId().toString()}))
                    .collect(Collectors.toList());
            catalogChannelSessionCouchDao.bulkRemove(cbIds);
        }
    }

    private void storeChannelSessionPricesIntoCB(EventIndexationContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getChannelSessionsToIndex())) {
            return;
        }

        Map<String, VenueConfigMap> venueConfigMapCache = new HashMap<>();
        Map<String, List<EventPriceRecord>> sessionWithQuotasPriceZonesCache = new HashMap<>();

        List<ChannelSessionPricesDocument> docs = new ArrayList<>();

        for (ChannelSessionForEventIndexation channelSession : ctx.getChannelSessionsToIndex()) {
            Long sessionId = channelSession.getSessionId();
            Long channelId = channelSession.getChannelId();
            Integer venueTemplateId = ctx.getVenueTemplatesBySession().get(sessionId).intValue();
            VenueDescriptor venueDescriptor = EventContextUtils.getVenueDescriptorBySessionId(ctx, sessionId);
            boolean isActivity = EventUtils.isActivityTemplate(venueDescriptor.getType());

            List<EventPriceRecord> sessionPrices = ctx.getPrices().stream()
                    .filter(ep -> {
                        Set<es.onebox.event.sessions.domain.SessionRate> sessionRates = ctx.getRatesBySession().get(sessionId);
                        return CollectionUtils.isNotEmpty(sessionRates) &&
                                sessionRates.stream().anyMatch(r -> r.getRateId().equals(ep.getRateId())) &&
                                ep.getVenueConfigId().equals(venueTemplateId);
                    })
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(sessionPrices)) {
                continue;
            }

            if (ctx.getSessionConfigs() != null
                    && ctx.getSessionConfigs().get(sessionId) != null
                    && ctx.getSessionConfigs().get(sessionId).getSessionDynamicPriceConfig() != null
                    && BooleanUtils.isTrue(ctx.getSessionConfigs().get(sessionId).getSessionDynamicPriceConfig().getActive())) {

                List<DynamicPriceZone> dynamicPriceZones = ctx.getSessionConfigs().get(sessionId).getSessionDynamicPriceConfig().getDynamicPriceZone();
                sessionPrices.forEach(eventPriceRecord -> {
                    Optional<DynamicPriceZone> optionalDynamicPriceZone = dynamicPriceZones.stream().filter(dpz -> dpz.getIdPriceZone().intValue() == eventPriceRecord.getPriceZoneId()).findFirst();
                    if (optionalDynamicPriceZone.isPresent() && optionalDynamicPriceZone.get().getActiveZone() != null && CollectionUtils.isNotEmpty(optionalDynamicPriceZone.get().getDynamicPrices())) {
                        DynamicPriceZone dynamicPriceZone = optionalDynamicPriceZone.get();
                        if (CollectionUtils.isNotEmpty(dynamicPriceZone.getDynamicPrices()
                                .get(dynamicPriceZone.getActiveZone().intValue()).getTranslations())) {
                            eventPriceRecord.setDynamicPriceTranslations(DynamicPriceConverter.toDynamicPriceTranslationDTOList(dynamicPriceZone.getDynamicPrices()
                                    .get(dynamicPriceZone.getActiveZone().intValue()).getTranslations()));
                            if (dynamicPriceZone.getActiveZone() < dynamicPriceZone.getDynamicPrices().size()) {
                                eventPriceRecord.setPrice(dynamicPriceZone.getDynamicPrices()
                                        .get(dynamicPriceZone.getActiveZone().intValue()).getDynamicRatesPrice().stream()
                                        .filter(dynamicRatesPrice -> dynamicRatesPrice.getId().intValue() == eventPriceRecord.getRateId())
                                        .findFirst().map(DynamicRatesPrice::getPrice).orElse(eventPriceRecord.getPrice()));
                            }
                        }
                    }
                });
            }

            Map<Integer, VenueConfigMap> venueConfigMap = getVenueConfigMap(channelSession, sessionPrices, isActivity,
                    venueConfigMapCache, sessionWithQuotasPriceZonesCache);

            Optional<EventChannelForCatalogRecord> eventChannel = ctx.getEventChannel(channelId.intValue());
            boolean specificSurcharges = eventChannel.isPresent() && eventChannel.get().getAplicarrecargoscanalespecificos() != null &&
                    BooleanUtils.toBoolean(eventChannel.get().getAplicarrecargoscanalespecificos());

            List<EventPromotionRecord> filteredPromotions = PromotionUtils.filterBySessionChannel(ctx, sessionId, channelId);

            List<SessionTaxesForCatalogRecord> sessionCatalogTaxes = ctx.getAllSessionTaxes().stream().filter(se -> sessionId.equals(se.getSessionId().longValue())).collect(Collectors.toList());
            SessionTaxes sessionTaxes = getSessionTaxes(sessionCatalogTaxes, channelSession.getChannelSurchargesTaxes(), ctx, sessionId);
            VenueConfigPricesSimulation priceSimulation = priceEngineSimulationService.getPriceSimulationForCatalog(
                    channelId, venueConfigMap, ctx.getChannelSurcharges().get(channelId),
                    specificSurcharges, filteredPromotions, sessionTaxes);

            ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
            doc.setSessionId(sessionId);
            doc.setChannelId(channelId);
            doc.setTaxes(getSessionTaxInfo(sessionTaxes));
            doc.setInvitationTaxes(getInvitationTaxInfo(sessionTaxes));
            doc.setSurchargesTaxes(getSurchargesTaxInfo(sessionTaxes));
            doc.setChannelSurchargesTaxes(getChannelSurchargesTaxInfo(channelSession.getChannelSurchargesTaxes()));

            CatalogVenueConfigPricesSimulation simulation
                    = CatalogPriceSimulationConverter.toCouchDoc(priceSimulation, ctx.getDefaultRateBySession().get(sessionId));

            if (EventUtils.isSmartBookingSession(ctx, sessionId.intValue())) {
                updateSBOriginalPrice(ctx.getSbBySession().get(sessionId), simulation);
            }
            doc.setSimulation(simulation);
            docs.add(doc);
        }

        if (CollectionUtils.isNotEmpty(docs)) {
            channelSessionPriceCouchDao.bulkUpsert(docs);
        }
    }

    private static List<CatalogSessionTaxInfo> getSurchargesTaxInfo(SessionTaxes in) {
        if (in == null) {
            return null;
        }
        List<CatalogSessionTaxInfo> result = new ArrayList<>();
        if (in.getSurchargesTaxes() != null) {
            for (SessionTaxInfo sessionTaxInfo : in.getSurchargesTaxes()) {
                result.add(
                        TaxSimulationUtils.createTaxInfo(
                                sessionTaxInfo.getId(),
                                sessionTaxInfo.getValue(),
                                sessionTaxInfo.getName(),
                                sessionTaxInfo.getMinRange(),
                                sessionTaxInfo.getMaxRange(),
                                sessionTaxInfo.getProgressive(),
                                sessionTaxInfo.getProgressiveMin(),
                                sessionTaxInfo.getProgressiveMax(),
                                sessionTaxInfo.getCapacityTypeId(),
                                sessionTaxInfo.getCapacityMin(),
                                sessionTaxInfo.getCapacityMax(),
                                sessionTaxInfo.getStartDate(),
                                sessionTaxInfo.getEndDate(),
                                CatalogSessionTaxInfo::new
                        ));
            }
        }
        return result;
    }

    private static List<CatalogSessionTaxInfo> getSessionTaxInfo(SessionTaxes in) {
        if (in == null) {
            return null;
        }
        List<CatalogSessionTaxInfo> result = new ArrayList<>();
        if (in.getPricesTaxes() != null) {
            for (SessionTaxInfo sessionTaxInfo : in.getPricesTaxes()) {
                result.add(
                        TaxSimulationUtils.createTaxInfo(
                                sessionTaxInfo.getId(),
                                sessionTaxInfo.getValue(),
                                sessionTaxInfo.getName(),
                                sessionTaxInfo.getMinRange(),
                                sessionTaxInfo.getMaxRange(),
                                sessionTaxInfo.getProgressive(),
                                sessionTaxInfo.getProgressiveMin(),
                                sessionTaxInfo.getProgressiveMax(),
                                sessionTaxInfo.getCapacityTypeId(),
                                sessionTaxInfo.getCapacityMin(),
                                sessionTaxInfo.getCapacityMax(),
                                sessionTaxInfo.getStartDate(),
                                sessionTaxInfo.getEndDate(),
                                CatalogSessionTaxInfo::new
                        ));
            }
        }
        return result;
    }

    private static List<CatalogSessionTaxInfo> getInvitationTaxInfo(SessionTaxes in) {
        if (in == null) {
            return null;
        }
        List<CatalogSessionTaxInfo> result = new ArrayList<>();
        if (in.getInvitationTaxes() != null) {
            for (SessionTaxInfo sessionTaxInfo : in.getInvitationTaxes()) {
                result.add(
                        TaxSimulationUtils.createTaxInfo(
                                sessionTaxInfo.getId(),
                                sessionTaxInfo.getValue(),
                                sessionTaxInfo.getName(),
                                sessionTaxInfo.getMinRange(),
                                sessionTaxInfo.getMaxRange(),
                                sessionTaxInfo.getProgressive(),
                                sessionTaxInfo.getProgressiveMin(),
                                sessionTaxInfo.getProgressiveMax(),
                                sessionTaxInfo.getCapacityTypeId(),
                                sessionTaxInfo.getCapacityMin(),
                                sessionTaxInfo.getCapacityMax(),
                                sessionTaxInfo.getStartDate(),
                                sessionTaxInfo.getEndDate(),
                                CatalogSessionTaxInfo::new
                        ));
            }
        }
        return result;
    }

    private List<CatalogSessionTaxInfo> getChannelSurchargesTaxInfo(List<ChannelTaxInfo> in) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }

        return in.stream()
                .map(ct -> TaxSimulationUtils.createTaxInfo(
                        ct.getId(),
                        ct.getValue(),
                        ct.getName(),
                        CatalogSessionTaxInfo::new)
                ).toList();
    }

    private SessionTaxes getSessionTaxes(List<SessionTaxesForCatalogRecord> sessionCatalogTaxes, List<ChannelTaxInfo> channelSurchargesTaxes, EventIndexationContext ctx, Long sessionId) {
        List<SessionTaxInfo> pricesTaxes = null;
        List<SessionTaxInfo> invitationTaxes = null;
        List<SessionTaxInfo> surchargesTaxes = null;
        for (SessionTaxesForCatalogRecord sessionTaxesForCatalogRecord : sessionCatalogTaxes) {
            if (sessionTaxesForCatalogRecord != null) {
                if (TaxType.TICKET.getType().equals(sessionTaxesForCatalogRecord.getTipo())) {
                    if (pricesTaxes == null) {
                        pricesTaxes = new ArrayList<>();
                    }
                    pricesTaxes.add(
                            TaxSimulationUtils.createTaxInfo(
                                    sessionTaxesForCatalogRecord.getId().longValue(),
                                    sessionTaxesForCatalogRecord.getTaxValue(),
                                    sessionTaxesForCatalogRecord.getTaxName(),
                                    sessionTaxesForCatalogRecord.getMinRange(),
                                    sessionTaxesForCatalogRecord.getMaxRange(),
                                    sessionTaxesForCatalogRecord.getProgressive(),
                                    sessionTaxesForCatalogRecord.getMinProgressive(),
                                    sessionTaxesForCatalogRecord.getMaxProgressive(),
                                    sessionTaxesForCatalogRecord.getCapacityType() != null ? CapacityRangeType.getById(sessionTaxesForCatalogRecord.getCapacityType()) : null,
                                    sessionTaxesForCatalogRecord.getCapacityMin(),
                                    sessionTaxesForCatalogRecord.getCapacityMax(),
                                    sessionTaxesForCatalogRecord.getStartDate() != null ? sessionTaxesForCatalogRecord.getStartDate().toLocalDateTime() : null,
                                    sessionTaxesForCatalogRecord.getEndDate() != null ? sessionTaxesForCatalogRecord.getEndDate().toLocalDateTime() : null,
                                    SessionTaxInfo::new
                            )
                    );
                }
                if (TaxType.TICKET_INVITATION.getType().equals(sessionTaxesForCatalogRecord.getTipo())) {
                    if (invitationTaxes == null) {
                        invitationTaxes = new ArrayList<>();
                    }
                    invitationTaxes.add(
                            TaxSimulationUtils.createTaxInfo(
                                    sessionTaxesForCatalogRecord.getId().longValue(),
                                    sessionTaxesForCatalogRecord.getTaxValue(),
                                    sessionTaxesForCatalogRecord.getTaxName(),
                                    sessionTaxesForCatalogRecord.getMinRange(),
                                    sessionTaxesForCatalogRecord.getMaxRange(),
                                    sessionTaxesForCatalogRecord.getProgressive(),
                                    sessionTaxesForCatalogRecord.getMinProgressive(),
                                    sessionTaxesForCatalogRecord.getMaxProgressive(),
                                    sessionTaxesForCatalogRecord.getCapacityType() != null ? CapacityRangeType.getById(sessionTaxesForCatalogRecord.getCapacityType()) : null,
                                    sessionTaxesForCatalogRecord.getCapacityMin(),
                                    sessionTaxesForCatalogRecord.getCapacityMax(),
                                    sessionTaxesForCatalogRecord.getStartDate() != null ? sessionTaxesForCatalogRecord.getStartDate().toLocalDateTime() : null,
                                    sessionTaxesForCatalogRecord.getEndDate() != null ? sessionTaxesForCatalogRecord.getEndDate().toLocalDateTime() : null,
                                    SessionTaxInfo::new
                            )
                    );
                }
                if (TaxType.CHARGES.getType().equals(sessionTaxesForCatalogRecord.getTipo())) {
                    if (surchargesTaxes == null) {
                        surchargesTaxes = new ArrayList<>();
                    }
                    surchargesTaxes.add(TaxSimulationUtils.createTaxInfo(
                                    sessionTaxesForCatalogRecord.getId().longValue(),
                                    sessionTaxesForCatalogRecord.getTaxValue(),
                                    sessionTaxesForCatalogRecord.getTaxName(),
                                    sessionTaxesForCatalogRecord.getMinRange(),
                                    sessionTaxesForCatalogRecord.getMaxRange(),
                                    sessionTaxesForCatalogRecord.getProgressive(),
                                    sessionTaxesForCatalogRecord.getMinProgressive(),
                                    sessionTaxesForCatalogRecord.getMaxProgressive(),
                                    sessionTaxesForCatalogRecord.getCapacityType() != null ? CapacityRangeType.getById(sessionTaxesForCatalogRecord.getCapacityType()) : null,
                                    sessionTaxesForCatalogRecord.getCapacityMin(),
                                    sessionTaxesForCatalogRecord.getCapacityMax(),
                                    sessionTaxesForCatalogRecord.getStartDate() != null ? sessionTaxesForCatalogRecord.getStartDate().toLocalDateTime() : null,
                                    sessionTaxesForCatalogRecord.getEndDate() != null ? sessionTaxesForCatalogRecord.getEndDate().toLocalDateTime() : null,
                                    SessionTaxInfo::new
                            )
                    );
                }
            }
        }

        if (surchargesTaxes == null) {
            SessionForCatalogRecord sessionForCatalogRecord = ctx.getSessions().stream()
                    .filter(s -> s.getIdsesion().equals(sessionId.intValue())).findFirst().orElse(null);
            if (sessionForCatalogRecord != null) {
                surchargesTaxes = List.of(
                        TaxSimulationUtils.createTaxInfo(
                                sessionForCatalogRecord.getSurchargesTaxId(),
                                sessionForCatalogRecord.getSurchargesTaxValue(),
                                sessionForCatalogRecord.getSurchargesTaxName(),
                                SessionTaxInfo::new
                        )
                );
            }
        }
        SessionTaxes sessionTaxes = new SessionTaxes();
        sessionTaxes.setPricesTaxes(pricesTaxes);
        sessionTaxes.setInvitationTaxes(invitationTaxes);
        sessionTaxes.setSurchargesTaxes(surchargesTaxes);
        sessionTaxes.setChannelSurchargesTaxes(channelSurchargesTaxes);
        return sessionTaxes;
    }

    private ChannelSessionData buildChannelSessionForEvent(ChannelSessionForEventIndexation in,
                                                           EventIndexationContext ctx) {
        Long sessionId = in.getSessionId();
        Long channelId = in.getChannelId();
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

            SessionDynamicPriceConfig sessionDynamicPriceConfig = getSessionDynamicPriceConfig(ctx, sessionId);

            ChannelSessionCustomersLimits channelSessionCustomersLimits = null;
            if (ctx.getSessionConfigs() != null && ctx.getSessionConfigs().containsKey(sessionId)) {
                SessionConfig sessionConfig = ctx.getSessionConfigs().get(sessionId);
                if (sessionConfig.getCustomersLimits() != null) {
                    CustomersLimits customerLimits = sessionConfig.getCustomersLimits();
                    channelSessionCustomersLimits = new ChannelSessionCustomersLimits();
                    channelSessionCustomersLimits.setMin(customerLimits.getMin());
                    channelSessionCustomersLimits.setMax(customerLimits.getMax());
                    if (customerLimits.getPriceTypeLimits() != null) {
                        List<ChannelSessionPriceTypeLimit> channelSessionPriceTypeLimitList = new ArrayList<>();
                        for (PriceTypeLimit priceTypeLimit : customerLimits.getPriceTypeLimits()) {
                            ChannelSessionPriceTypeLimit channelSessionPriceTypeLimit = new ChannelSessionPriceTypeLimit();
                            channelSessionPriceTypeLimit.setId(priceTypeLimit.getId());
                            channelSessionPriceTypeLimit.setMin(priceTypeLimit.getMin());
                            channelSessionPriceTypeLimit.setMax(priceTypeLimit.getMax());
                            channelSessionPriceTypeLimitList.add(channelSessionPriceTypeLimit);
                        }
                        channelSessionCustomersLimits.setPriceTypeLimits(channelSessionPriceTypeLimitList);
                    }
                }
            }

            List<ChannelTaxInfo> channelSurchargesTaxes = in.getChannelSurchargesTaxes() == null ? new ArrayList<>() : in.getChannelSurchargesTaxes();

            ChannelSessionData data = ChannelSessionDataBuilder.builder()
                    .channelId(channelId)
                    .sessionId(sessionId)
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
                    .sessionDynamicPriceConfig(sessionDynamicPriceConfig)
                    .mustBeIndexed(in.getMustBeIndexed())
                    .channelSessionCustomersLimits(channelSessionCustomersLimits)
                    .channelTaxes(buildChannelTaxes(channelSurchargesTaxes))
                    .build();

            //Occupation data
            Boolean primaryMktForSaleChannel = channelEvent.getEnventa() == 1;
            boolean soldOut = calculateSoldOut(ctx.getSecondaryMarketForSale(), primaryMktForSaleChannel, secMktEventChannelForSale,
                    sessionId, in.getSecondaryMarketConfig(), in.getQuotas(), priceZonesWithAvailability, in.getContainerOccupations());
            List<Long> promotions = preparePromotions(ctx, sessionId, channelId, in.getPriceZones(), rates);
            List<SessionTaxInfo> sessionTaxes = sessionData.map(SessionData::getSession).map(Session::getTaxes).orElse(Collections.emptyList());
            List<SessionTaxInfo> surchargesTaxes = sessionData.map(SessionData::getSession).map(Session::getSurchargesTaxes).orElse(Collections.emptyList());
            PriceMatrix priceMatrix = PriceMatrixCalculator.builder()
                    .prices(ctx.getVenueTemplatePrices())
                    .channelEventSurcharges(ctx.getChannelSurcharges().get(channelId))
                    .eventPromotions(ctx.getEventPromotions())
                    .sessionPromotions(promotions)
                    .priceZonesBySession(priceZonesWithAvailability)
                    .rates(rates)
                    .secondaryMarket(secondaryMarketData)
                    .sessionDynamicPriceConfig(sessionDynamicPriceConfig)
                    .sessionTaxes(sessionTaxes)
                    .surchargesTaxes(surchargesTaxes)
                    .channelSurchargesTaxes(channelSurchargesTaxes)
                    .build();

            ChannelSessionOccupationBuilder.builder(data.getChannelSession())
                    .soldOut(soldOut)
                    .promotions(promotions)
                    .priceMatrix(priceMatrix)
                    .priceZoneOccupations(in.getPriceZoneOccupations())
                    .containerOccupation(in.getContainerOccupations())
                    .buildOccupation();

            updateComElementsChannelEventInfo(in.getChannelEvent(), data.getChannelSession(), ctx);
            return data;

        } catch (Exception e) {
            throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event:%d - session:%d - channel:%d . Error indexing channel session", ctx.getEventId(), sessionId, channelId), e);
        }
    }

    private static SessionDynamicPriceConfig getSessionDynamicPriceConfig(EventIndexationContext ctx, Long sessionId) {
        return (ctx.getSessionConfigs() != null &&
                ctx.getSessionConfigs().get(sessionId) != null &&
                ctx.getSessionConfigs().get(sessionId).getSessionDynamicPriceConfig() != null)
                ? ctx.getSessionConfigs().get(sessionId).getSessionDynamicPriceConfig()
                : null;
    }

    private ChannelSessionData buildChannelSessionForOccupation(OccupationIndexationContext ctx,
                                                                ChannelSessionForOccupationIndexation channelSessionToIndex) {
        EventData event = ctx.getEventData();
        ChannelSession channelSession = channelSessionToIndex.getChannelSessionIndexed();
        Long eventId = channelSession.getEventId();
        Long sessionId = channelSession.getSessionId();
        Long channelId = channelSession.getChannelId();

        Optional<SessionData> session = ctx.getSessionOr(sessionId, id -> sessionElasticDao.get(id, eventId));

        List<SecondaryMarketSearch> secondaryMarketItems = SecondaryMarketCalculator.getSecondaryMarketItems(
                ctx.getSecondaryMarketForSale(), channelSession.getSecondaryMarketConfig(),
                channelSession.getQuotas(), sessionId);

        ChannelEventSurcharges surcharges = ctx.getChannelEvent(channelId)
                .map(ChannelEventData::getChannelEvent)
                .map(ChannelEvent::getSurcharges)
                .orElse(null);

        List<SessionRate> rates = session.map(SessionData::getSession).map(Session::getRates).orElse(Collections.emptyList());
        List<Long> promotions = preparePromotions(ctx, sessionId, channelId, channelSessionToIndex.getPriceZones(), rates);
        List<SessionTaxInfo> sessionTaxes = session.map(SessionData::getSession).map(Session::getTaxes).orElse(Collections.emptyList());
        List<SessionTaxInfo> surchargesTaxes = session.map(SessionData::getSession).map(Session::getSurchargesTaxes).orElse(Collections.emptyList());
        List<ChannelTaxInfo> channelSurchargesTaxes = channelSession.getChannelTaxes() == null ? new ArrayList<>() : channelSession.getChannelTaxes().getSurcharges();
        List<Long> priceZonesWithAvailability = channelSessionToIndex.getPriceZonesWithAvailability();

        PriceMatrix priceMatrix = PriceMatrixCalculator.builder()
                .prices(event.getEvent().getPrices())
                .channelEventSurcharges(surcharges)
                .eventPromotions(ctx.getEventPromotions())
                .sessionPromotions(promotions)
                .priceZonesBySession(priceZonesWithAvailability)
                .rates(rates)
                .secondaryMarket(secondaryMarketItems)
                .sessionDynamicPriceConfig(channelSession.getSessionDynamicPriceConfig())
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
        boolean soldOut = calculateSoldOut(ctx.getSecondaryMarketForSale(), primaryMktForSaleChannel,
                secMktForSaleChannel, sessionId, channelSession.getSecondaryMarketConfig(),
                channelSession.getQuotas(), priceZonesWithAvailability, channelSession.getContainerOccupations());

        ChannelSessionData data = ChannelSessionDataBuilder.builder()
                .channelId(channelId)
                .sessionId(sessionId)
                .build();

        ChannelSessionOccupationBuilder.builder(data.getChannelSession())
                .soldOut(soldOut)
                .promotions(promotions)
                .priceMatrix(priceMatrix)
                .priceZoneOccupations(channelSessionToIndex.getPriceZoneOccupations())
                .containerOccupation(occupation)
                .buildOccupation();

        return data;
    }

    private List<ChannelSessionData> updateBasicChannelSessionInfo(EventIndexationContext ctx, List<ChannelSessionData> esChannelSessions) {
        Map<Long, List<ChannelSessionData>> csByChannel = esChannelSessions.stream()
                .collect(groupingBy(cs -> cs.getChannelSession().getChannelId()));

        checkReadySessionsAlreadyOnES(ctx, esChannelSessions.stream()
                .map(cs -> cs.getChannelSession().getSessionId().intValue()).toList());

        List<ChannelSessionData> channelSessionsToIndex = new ArrayList<>();
        for (Map.Entry<Long, List<ChannelSessionData>> channelCs : csByChannel.entrySet()) {
            Long channelId = channelCs.getKey();
            List<Key> keys = IndexerUtils.buildSessionChannelKeys(channelCs.getValue());
            List<ChannelSession> channelSessions = catalogChannelSessionCouchDao.bulkGet(keys);
            boolean secMktEventChannelForSale = SecondaryMarketCalculator.calculateSaleChannelEvent(ctx, channelId);
            for (ChannelSession channelSession : channelSessions) {
                SessionForCatalogRecord session = ctx.getSession(channelSession.getSessionId().intValue());
                CpanelCanalEventoRecord channelEvent = ctx.getChannelEvent(channelSession.getChannelId().intValue());
                if (CatalogPublishableUtils.isSessionToIndex(session)) {
                    channelSession.setDate(getChannelSessionDates(session, channelEvent, channelSession.getTimeZone(), ctx));
                    channelSession.setForSale(getForSale(session, channelEvent, secMktEventChannelForSale));
                    ChannelSessionData csData = ChannelSessionDataBuilder.buildChannelSessionData(channelId, session.getIdsesion().longValue());
                    csData.setChannelSession(channelSession);
                    csData.setMustBeIndexed(CatalogPublishableUtils.isSessionPublishable(session, ctx.getEvent(), channelEvent));
                    channelSessionsToIndex.add(csData);
                }
            }
        }
        return channelSessionsToIndex;
    }
}
