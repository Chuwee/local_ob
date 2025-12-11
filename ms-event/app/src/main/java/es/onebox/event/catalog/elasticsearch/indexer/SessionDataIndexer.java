package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dao.couch.SessionTemplateInfo;
import es.onebox.event.catalog.dao.couch.TemplateElementInfo;
import es.onebox.event.catalog.dao.couch.TemplateInfoStatus;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionTaxesForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.builder.SessionDataBuilder;
import es.onebox.event.catalog.elasticsearch.builder.SessionDataComElementsBuilder;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateChannelRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateCustomerTypesRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateDatesRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RatePeriodRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RatePriceZonesRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateRelationsRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateRestrictions;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionPackSettings;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRelated;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRelatedDate;
import es.onebox.event.catalog.elasticsearch.dto.session.VenueProviderConfig;
import es.onebox.event.catalog.elasticsearch.dto.session.external.ExternalData;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.catalog.elasticsearch.enums.VenueProviderVersion;
import es.onebox.event.catalog.elasticsearch.exception.CatalogIndexerException;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.catalog.elasticsearch.utils.PresaleConfigUtil;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.enums.RatePriceZoneCriteria;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.events.dao.CountrySubdivisionDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.EventIndexationFullReload;
import es.onebox.event.loyaltypoints.sessions.dao.SessionLoyaltyPointsConfigCouchDao;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.priceengine.taxes.domain.CapacityRangeType;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.utils.PromotionUtils;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.enums.SessionTaxesType;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.onebox.event.catalog.elasticsearch.builder.SessionDataComElementsBuilder.getSessionCommunicationElement;

@Component
public class SessionDataIndexer {

    private final SessionElasticDao sessionElasticDao;
    private final CatalogSessionCouchDao catalogSessionCouchDao;
    private final SeasonSessionDao seasonSessionDao;
    private final CountrySubdivisionDao countrySubdivisionDao;
    private final SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao;
    private final EntitiesRepository entitiesRepository;
    private final CacheRepository localCacheRepository;
    private final StaticDataContainer staticDataContainer;
    private final IntAvetConfigRepository intAvetConfigRepository;

    @Autowired
    public SessionDataIndexer(SessionElasticDao sessionElasticDao,
                              CatalogSessionCouchDao catalogSessionCouchDao,
                              SeasonSessionDao seasonSessionDao,
                              CountrySubdivisionDao countrySubdivisionDao,
                              SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao,
                              EntitiesRepository entitiesRepository,
                              CacheRepository localCacheRepository,
                              StaticDataContainer staticDataContainer,
                              IntAvetConfigRepository intAvetConfigRepository) {
        this.sessionElasticDao = sessionElasticDao;
        this.catalogSessionCouchDao = catalogSessionCouchDao;
        this.seasonSessionDao = seasonSessionDao;
        this.countrySubdivisionDao = countrySubdivisionDao;
        this.sessionLoyaltyPointsConfigCouchDao = sessionLoyaltyPointsConfigCouchDao;
        this.entitiesRepository = entitiesRepository;
        this.localCacheRepository = localCacheRepository;
        this.staticDataContainer = staticDataContainer;
        this.intAvetConfigRepository = intAvetConfigRepository;
    }

    public void indexSessions(EventIndexationContext ctx) {
        List<SessionData> sessions = buildSessions(ctx);

        if (CollectionUtils.isNotEmpty(sessions)) {
            String routing = EventDataUtils.getEventKey(ctx.getEventId());
            if (CollectionUtils.isNotEmpty(ctx.getDeletedSessions())) {
                sessionElasticDao.bulkDelete(routing, ctx.getDeletedSessions()
                        .stream().map(session -> EventDataUtils.getSessionKey(session.getIdsesion().longValue())).toArray(String[]::new));
                catalogSessionCouchDao.bulkRemove(ctx.getDeletedSessions().stream().map(CpanelSesionRecord::getIdsesion)
                        .map(id -> new Key(new String[]{String.valueOf(id)})).toList());
            }
            sessionElasticDao.bulkUpsert(false, routing, sessions.toArray(new SessionData[0]));
            ctx.addDocumentsIndexed(sessions);
            catalogSessionCouchDao.bulkUpsert(sessions.stream().map(SessionData::getSession).collect(Collectors.toList()));
        }
    }

    private List<SessionData> buildSessions(EventIndexationContext ctx) {
        List<SessionForCatalogRecord> sessions = ctx.getSessions();
        return sessions.stream()
                .map(session -> this.buildSession(session, ctx))
                .collect(Collectors.toList());
    }

    private SessionData buildSession(SessionForCatalogRecord sessionRecord, EventIndexationContext ctx) {
        Long sessionId = sessionRecord.getIdsesion().longValue();
        try {

            switch (ctx.getType()) {
                case PARTIAL_BASIC -> {
                    SessionData sessionData = getSessionData(ctx, sessionId);
                    updateBasicSessionInfo(sessionData, sessionRecord, ctx.getEvent());
                    return sessionData;
                }
                case PARTIAL_COM_ELEMENTS -> {
                    SessionData sessionData = getSessionData(ctx, sessionId);
                    updateComElementsSessionInfo(sessionData, ctx);
                    return sessionData;
                }
            }

            //Basic data
            CpanelEventoRecord eventRecord = ctx.getEvent();

            //ComElements data
            List<CpanelElementosComEventoRecord> commElements = extractComElements(ctx, sessionId);

            //Full data
            Long venueTemplateId = ctx.getVenueTemplatesBySession().get(sessionId);
            SessionDao.VenueTemplateInfo venueTemplateInfo = ctx.getVenueTemplateInfos().get(venueTemplateId);
            Long externalVenueTemplateId = venueTemplateInfo.externalVenueConfigId() != null ? venueTemplateInfo.externalVenueConfigId().longValue() : null;
            IdNameCodeDTO externalVenueTemplate = null;
            if (externalVenueTemplateId != null && ctx.getExternalVenueTemplates() != null) {
                externalVenueTemplate = ctx.getExternalVenueTemplates().stream().filter(evt -> evt.getId().equals(externalVenueTemplateId)).findFirst().orElse(null);
            }

            VenueRecord venueRecord = ctx.getVenueBySessionId(sessionId).orElse(null);
            Long externalVenueId = venueRecord != null && venueRecord.getExternalVenueId() != null ? venueRecord.getExternalVenueId().longValue() : null;
            IdNameCodeDTO externalVenue = null;
            if (externalVenueId != null && ctx.getExternalVenues() != null) {
                externalVenue = ctx.getExternalVenues().stream().filter(ev -> ev.getId().equals(externalVenueId)).findFirst().orElse(null);
            }
            VenueDescriptor venueDescriptor = ctx.getVenueDescriptor().get(venueTemplateId.intValue());
            VenueProviderConfig venueProviderConfig = buildVenueProviderConfig(venueTemplateInfo, ctx);

            List<CpanelTarifaRecord> rates = extractRates(ctx, sessionId);
            List<Long> relatedSessionPacks = extractSessionPacks(ctx, sessionId);
            List<Long> priceZones = extractPriceZones(ctx, sessionId);
            List<Long> promotions = preparePromotions(ctx, sessionId, priceZones);

            Map<Long, Set<EntityTemplateZonesDTO>> templatesZonesByPriceZone = extractTemplatesZones(sessionId, venueTemplateId,
                    ctx.getTemplateElementInfoByTemplateId(), ctx.getTemplateZonesById());

            SessionConfig sessionConfig = ctx.getSessionConfigs().get(sessionId);
            var priceZoneRestrictions = priceZonesRestrictions(ctx.getEventConfig(), sessionConfig, priceZones);
            var rateRestrictions = ratesRestrictions(ctx.getEventConfig(), sessionConfig, rates);

            Integer promoterId = sessionRecord.getIdpromotor();
            ProducerDTO producer = localCacheRepository.cached(LocalCache.PRODUCER_KEY, LocalCache.PRODUCER_TTL, TimeUnit.SECONDS,
                    () -> entitiesRepository.getProducerRaw(promoterId), new Object[]{promoterId});
            IdNameCodeDTO sessionPromoterSubdivision = null;
            if (producer.getCountrySubdivisionId() != null) {
                sessionPromoterSubdivision = localCacheRepository.cached(LocalCache.COUNTRY_SUB_KEY, LocalCache.COUNTRY_SUB_TTL, TimeUnit.SECONDS,
                        () -> countrySubdivisionDao.getCountrySubInfo(producer.getCountrySubdivisionId()), new Object[]{producer.getCountrySubdivisionId()});
            }

            List<PresaleConfig> presales = null;
            if (MapUtils.isNotEmpty(ctx.getSessionPresaleConfigMap())
                    && CollectionUtils.isNotEmpty(ctx.getSessionPresaleConfigMap().get(sessionId))) {
                List<PresaleRecord> presalesActives = PresaleConfigUtil.getPresalesConfigActives(ctx.getSessionPresaleConfigMap().get(sessionId));
                presales = PresaleConfigUtil.convertToPresaleConfigs(presalesActives, ctx.getPresaleCollectives());
            }

            var externalData = buildExternalData(sessionId, eventRecord.getTipoevento());

            SessionLoyaltyPointsConfig loyaltyPointsConfig = getSessionLoyaltyPointsConfig(ctx, sessionId);

            List<SessionTaxesForCatalogRecord> ticketSessionTaxes = ctx.getAllSessionTaxes().stream().filter(se -> se.getSessionId().equals(sessionId.intValue()) && se.getTipo().equals(SessionTaxesType.TICKETS.getType())).collect(Collectors.toList());
            List<SessionTaxesForCatalogRecord> invitationSessionTaxes = ctx.getAllSessionTaxes().stream().filter(se -> se.getSessionId().equals(sessionId.intValue()) && se.getTipo().equals(SessionTaxesType.TICKET_INVITATION.getType())).collect(Collectors.toList());
            List<SessionTaxesForCatalogRecord> chargesSessionTaxes = ctx.getAllSessionTaxes().stream().filter(se -> se.getSessionId().equals(sessionId.intValue()) && se.getTipo().equals(SessionTaxesType.CHARGES.getType())).collect(Collectors.toList());

            SessionData sessionData = SessionDataBuilder.builder()
                    //Basic
                    .sessionRecord(sessionRecord)
                    .eventRecord(eventRecord)
                    //Full
                    .taxes(buildTaxes(ticketSessionTaxes))
                    .invitationTaxes(buildTaxes(invitationSessionTaxes))
                    .surchargeTaxes(buildTaxes(chargesSessionTaxes))
                    .staticDataContainer(staticDataContainer)
                    .venueConfigId(venueTemplateId)
                    .externalVenueConfig(externalVenueTemplate)
                    .venueId(venueRecord != null ? venueRecord.getId() : null)
                    .externalVenue(externalVenue)
                    .timeZone(venueRecord != null ? venueRecord.getTimeZone() : null)
                    .venueQuotas(venueDescriptor != null ? venueDescriptor.getQuotas() : null)
                    .venueProviderConfig(venueProviderConfig)
                    .venueTemplateType(venueTemplateInfo.venueTemplateType())
                    .isGraphic(venueTemplateInfo.isGraphical())
                    .rates(rates)
                    .promotions(promotions)
                    .relatedSeasonSessionIds(relatedSessionPacks)
                    .promoter(producer)
                    .promoterCountrySubdivision(sessionPromoterSubdivision)
                    .priceZonesRestrictions(priceZoneRestrictions)
                    .ratesRestrictions(rateRestrictions)
                    .presales(presales)
                    .isSmartBooking(EventUtils.isSmartBookingSession(sessionRecord, venueTemplateInfo.venueTemplateType()))
                    .relatedSessionId(sessionRecord.getSbsesionrelacionada() != null ? sessionRecord.getSbsesionrelacionada().longValue() : null)
                    .externalData(externalData)
                    .loyaltyPointsConfig(loyaltyPointsConfig)
                    .ipRestrictedCountries(sessionConfig)
                    .priceZoneLimit(sessionConfig)
                    .customersLimits(sessionConfig)
                    .virtualQueue(sessionConfig)
                    .sessionPresalesConfig(sessionConfig)
                    .entityTemplatesZonesByPriceZoneId(MapUtils.isNotEmpty(templatesZonesByPriceZone) ? templatesZonesByPriceZone : null)
                    .build();

            SessionDataComElementsBuilder.builder(sessionData.getSession())
                    .staticDataContainer(staticDataContainer)
                    .operatorId(ctx.getEntity().getOperator().getId().longValue())
                    .communicationElements(commElements)
                    .buildComElements();

            if (CommonUtils.isTrue(sessionRecord.getEsabono())) {
                buildSessionPackSettings(sessionRecord, ctx, sessionId, eventRecord, sessionData);
            }

            return sessionData;
        } catch (EventIndexationFullReload e) {
            throw e;
        } catch (Exception e) {
            throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event: %d - session: %d . Error indexing session", ctx.getEventId(), sessionId), e);
        }
    }

    private Map<Long, Set<EntityTemplateZonesDTO>> extractTemplatesZones(Long sessionId, Long venueTemplateId,
                                                                         Map<Long, List<TemplateElementInfo>> templateElementInfoByVenueTemplateId,
                                                                         Map<Integer, EntityTemplateZonesDTO> templatesZonesById) {
        if (MapUtils.isEmpty(templateElementInfoByVenueTemplateId) || MapUtils.isEmpty(templatesZonesById)) {
            return new HashMap<>();
        }
        List<TemplateElementInfo> templatesElementInfo = templateElementInfoByVenueTemplateId.get(venueTemplateId);
        if (CollectionUtils.isEmpty(templatesElementInfo)) {
            return new HashMap<>();
        }

        Map<Long, Set<EntityTemplateZonesDTO>> result = new HashMap<>();
        Map<Long, Set<Integer>> sessionInfoByTemplateZonesIds = new HashMap<>(); // priceZoneId + templateZonesIds

        templatesElementInfo.forEach(templateElementInfo -> {
            if (CollectionUtils.isNotEmpty(templateElementInfo.getSessionTemplateInfoList()) && existsSessionById(sessionId, templateElementInfo)) {

                SessionTemplateInfo sessionTemplateInfo = templateElementInfo.getSessionTemplateInfoList().stream()
                        .filter(sessionTemplate -> sessionTemplate.getSessionId().equals(sessionId))
                        .findFirst().orElse(null);
                if (sessionTemplateInfo != null && TemplateInfoStatus.ENABLED.equals(sessionTemplateInfo.getStatus()) &&
                        sessionTemplateInfo.getAggregatedInfo() != null &&
                        CollectionUtils.isNotEmpty(sessionTemplateInfo.getAggregatedInfo().getTemplatesZonesIds())) {
                    sessionInfoByTemplateZonesIds.put(templateElementInfo.getId(), new HashSet<>(sessionTemplateInfo.getAggregatedInfo().getTemplatesZonesIds()));
                }
            } else if (templateElementInfo.getDefaultInfo() != null && CollectionUtils.isNotEmpty(templateElementInfo.getDefaultInfo().getTemplatesZonesIds())) {
                sessionInfoByTemplateZonesIds.put(templateElementInfo.getId(), new HashSet<>(templateElementInfo.getDefaultInfo().getTemplatesZonesIds()));
            }
        });

        if (MapUtils.isNotEmpty(sessionInfoByTemplateZonesIds)) {
            sessionInfoByTemplateZonesIds.forEach((priceZone, templatesZonesIds) -> {
                Set<EntityTemplateZonesDTO> templateZones = new HashSet<>();
                templatesZonesIds.forEach(id -> {
                    var tz = templatesZonesById.get(id);
                    if (tz != null) {
                        templateZones.add(tz);
                    }
                });
                if (CollectionUtils.isNotEmpty(templateZones)) {
                    result.put(priceZone, templateZones);
                }
            });

        }
        return result;
    }

    private static boolean existsSessionById(Long sessionId, TemplateElementInfo templateElementInfo) {
        return templateElementInfo.getSessionTemplateInfoList().stream()
                .anyMatch(sessionTemplateInfo -> sessionTemplateInfo.getSessionId().equals(sessionId));
    }

    private SessionData getSessionData(EventIndexationContext ctx, Long sessionId) {
        Session session = catalogSessionCouchDao.get(sessionId.toString());
        if (session == null) {
            throw new EventIndexationFullReload("session: " + sessionId);
        }
        SessionData sessionData = SessionDataBuilder.buildSessionData(sessionId, ctx.getEventId());
        sessionData.setSession(session);
        return sessionData;
    }

    private void updateBasicSessionInfo(SessionData sessionData, SessionForCatalogRecord record, CpanelEventoRecord eventRecord) {
        SessionDataBuilder.builder(sessionData.getSession())
                .sessionRecord(record)
                .eventRecord(eventRecord)
                .buildBasicSessionInfo();
    }

    private void updateComElementsSessionInfo(SessionData sessionData, EventIndexationContext ctx) {
        Long sessionId = sessionData.getSession().getSessionId();
        List<CpanelElementosComEventoRecord> commElements = extractComElements(ctx, sessionId);
        SessionDataComElementsBuilder.builder(sessionData.getSession())
                .staticDataContainer(staticDataContainer)
                .operatorId(ctx.getEntity().getOperator().getId().longValue())
                .communicationElements(commElements)
                .buildComElements();

        if (sessionData.getSession().getSessionPackSettings() != null &&
                CollectionUtils.isNotEmpty(sessionData.getSession().getSessionPackSettings().getSessions())) {
            var imageUrlBuilder = S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                    .withSessionId(sessionId.intValue()).withEventId(sessionData.getSession().getEventId())
                    .withEntityId(sessionData.getSession().getEntityId())
                    .withOperatorId(ctx.getEntity().getOperator().getId().longValue())
                    .build();
            for (SessionRelated s : sessionData.getSession().getSessionPackSettings().getSessions()) {
                List<CpanelElementosComEventoRecord> sessionComElement = extractComElements(ctx, s.getId());
                s.setCommunicationElements(sessionComElement.stream().map(elem ->
                        getSessionCommunicationElement(elem, imageUrlBuilder, staticDataContainer)).toList());
            }
        }
    }

    private List<SessionTaxInfo> buildTaxes(List<SessionTaxesForCatalogRecord> ticketTaxes) {
        List<SessionTaxInfo> result = new ArrayList<>();
        for (SessionTaxesForCatalogRecord sessionTaxesForCatalogRecord : ticketTaxes) {
            result.add(TaxSimulationUtils.createTaxInfo(
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
            ));
        }
        return result;
    }

    private SessionLoyaltyPointsConfig getSessionLoyaltyPointsConfig(EventIndexationContext ctx, Long sessionId) {
        if (BooleanUtils.isTrue(ctx.getEntity().getAllowLoyaltyPoints())) {
            SessionLoyaltyPointsConfig loyaltyPointsConfig = sessionLoyaltyPointsConfigCouchDao.get(sessionId.toString());
            if (loyaltyPointsConfig != null
                    && loyaltyPointsConfig.getPointGain() != null
                    && NumberUtils.zeroIfNull(loyaltyPointsConfig.getPointGain().getAmount()) > 0
            ) {
                return loyaltyPointsConfig;
            }
        }
        return null;
    }

    private ExternalData buildExternalData(Long sessionId, Integer isAvetEvent) {
        if (EventType.AVET.getId().equals(isAvetEvent)) {
            SessionMatch sessionMatch = intAvetConfigRepository.getSessionMatch(sessionId);
            return Optional.ofNullable(sessionMatch)
                    .flatMap(s -> Optional.ofNullable(s.getCapacityId())
                            .map(Integer::longValue)
                            .map(ex -> {
                                var externalData = new ExternalData();
                                externalData.setCapacityId(ex);
                                return externalData;
                            })).orElse(null);
        }
        return null;
    }

    private static SessionPackSettings buildSessionPackSettings(List<SessionForCatalogRecord> sessionsInSeason, List<SessionRelated> relatedSessions) {
        long numDays;
        long numSessions;
        List<SessionForCatalogRecord> notCancelledSessionsInPack = sessionsInSeason.stream()
                .filter(s -> !SessionStatus.CANCELLED.getId().equals(s.getEstado()))
                .toList();
        Map<LocalDate, Long> sessionsPerDay = notCancelledSessionsInPack.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getFechainiciosesion().toLocalDateTime().toLocalDate(),
                        Collectors.counting()
                ));
        numDays = sessionsPerDay.size();
        numSessions = notCancelledSessionsInPack.size();
        return new SessionPackSettings(numDays, numSessions, relatedSessions);
    }

    private static VenueProviderConfig buildVenueProviderConfig(SessionDao.VenueTemplateInfo configRecinto, EventIndexationContext ctx) {
        EventConfig eventConfig = ctx.getEventConfig();
        VenueProviderConfig out = new VenueProviderConfig();
        out.setVenueProviderCode(configRecinto.venueProviderCode());
        out.setVenueProviderMinimapCode(configRecinto.venueProviderMinimapCode());
        out.setPlugins(configRecinto.plugins());
        if (eventConfig != null) {
            out.setUseSeat3dView(eventConfig.isUseSeat3dView());
            out.setUseSector3dView(eventConfig.isUseSector3dView());
            out.setUseVenue3dView(eventConfig.isUseVenue3dView());
            out.setInteractiveVenueType(eventConfig.getInteractiveVenueType());
            out.setVenueProviderVersion(venueProviderVersionMapper(eventConfig));
        }
        return out;
    }

    private static VenueProviderVersion venueProviderVersionMapper(EventConfig eventConfig) {
        if (eventConfig.isUse3dVenueModule()) {
            return VenueProviderVersion.V1;
        } else if (eventConfig.isUse3dVenueModuleV2()) {
            return VenueProviderVersion.V2;
        } else {
            return VenueProviderVersion.NONE;
        }
    }

    private static List<Long> preparePromotions(EventIndexationContext ctx, Long sessionId, List<Long> priceZones) {
        List<EventPromotion> promotions = PromotionUtils.filterBySession(ctx.getEventPromotions(), sessionId);
        if (promotions.stream().anyMatch(promotion -> CollectionUtils.isNotEmpty(promotion.getRestrictions().getPriceZones()))) {
            promotions = PromotionUtils.filterByPriceZones(promotions, priceZones);
        }
        return promotions.stream().map(EventPromotion::getEventPromotionTemplateId).distinct().collect(Collectors.toList());
    }

    private static List<CpanelElementosComEventoRecord> extractComElements(EventIndexationContext ctx, Long sessionId) {
        List<CpanelElementosComEventoRecord> comElements = ctx.getComElementsBySession().get(sessionId.intValue());
        if (CollectionUtils.isNotEmpty(comElements)) {
            return comElements;
        }
        return List.of();
    }

    private static List<CpanelTarifaRecord> extractRates(EventIndexationContext ctx, Long sessionId) {
        List<CpanelTarifaRecord> rates = new ArrayList<>();
        Set<SessionRate> sessionRates = ctx.getRatesBySession().get(sessionId);
        if (CollectionUtils.isNotEmpty(sessionRates)) {
            for (SessionRate sessionRate : sessionRates) {
                CpanelTarifaRecord rate = new CpanelTarifaRecord();
                rate.setIdtarifa(sessionRate.getRateId());
                rate.setNombre(sessionRate.getRateName());
                rate.setDefecto(ConverterUtils.isTrueAsByte(sessionRate.getDefaultRate()));
                rate.setAccesorestrictivo(sessionRate.getRestrictiveAccess());
                rate.setPosition(sessionRate.getPosition());
                rates.add(rate);
            }
        }
        return rates;
    }

    private static List<Long> extractSessionPacks(EventIndexationContext ctx, Long sessionId) {
        List<Integer> sessionPackIds = ctx.getSessionPacksBySession().get(sessionId.intValue());
        if (CollectionUtils.isNotEmpty(sessionPackIds)) {
            return sessionPackIds.stream().map(Integer::longValue).toList();
        }
        return List.of();
    }

    private static List<Long> extractPriceZones(EventIndexationContext ctx, Long sessionId) {
        List<Long> priceZones = new ArrayList<>();
        Long sessionVenueTemplateId = ctx.getVenueTemplatesBySession().get(sessionId);
        if (sessionVenueTemplateId != null && CollectionUtils.isNotEmpty(ctx.getVenueTemplatePrices())) {
            VenueTemplatePrice sessionVenueTemplate = ctx.getVenueTemplatePrices().stream().
                    filter(vt -> vt.getId().equals(sessionVenueTemplateId.intValue())).findFirst().orElse(null);
            if (sessionVenueTemplate != null) {
                priceZones = sessionVenueTemplate.getPriceZones().stream().map(p -> p.getId().longValue()).toList();
            }
        }
        return priceZones;
    }

    private static Map<Long, PriceZoneRestriction> priceZonesRestrictions(EventConfig eventConfig, SessionConfig sessionConfig, List<Long> priceZones) {
        PriceZonesRestrictions restrictions = resolvePriceZoneRestrictions(eventConfig, sessionConfig);
        if (restrictions != null && CollectionUtils.isNotEmpty(priceZones)) {
            return buildPriceZoneRestriction(priceZones, restrictions);
        }
        return null;
    }

    private static Map<Long, RateRestrictions> ratesRestrictions(EventConfig eventConfig, SessionConfig sessionConfig, List<CpanelTarifaRecord> rates) {
        RatesRestrictions restrictions = resolveRateRestrictions(eventConfig, sessionConfig);
        if (restrictions != null && CollectionUtils.isNotEmpty(rates)) {
            return buildRateRestriction(rates, restrictions);
        }
        return null;
    }

    private static Map<Long, PriceZoneRestriction> buildPriceZoneRestriction(List<Long> priceZones, PriceZonesRestrictions restrictions) {
        return restrictions.entrySet().stream().filter(pz -> priceZones.contains(pz.getKey().longValue()))
                .collect(Collectors.toMap(e -> e.getKey().longValue(), e -> {
                    PriceZoneRestriction out = new PriceZoneRestriction();
                    es.onebox.event.common.domain.PriceZoneRestriction restriction = e.getValue();
                    out.setRequiredPriceZones(restriction.getRequiredPriceZones());
                    if (restriction.getMaxItemsMultiplier() != null) {
                        if (restriction.getMaxItemsMultiplier() >= 1) {
                            out.setRequired(null);
                            out.setLocked(restriction.getMaxItemsMultiplier().longValue());
                        } else {
                            double tickets = 1 / restriction.getMaxItemsMultiplier();
                            out.setRequired((long) tickets);
                            out.setLocked(null);
                        }
                    }
                    return out;
                }));
    }

    private static Map<Long, RateRestrictions> buildRateRestriction(List<CpanelTarifaRecord> rates, RatesRestrictions restrictions) {
        return restrictions.entrySet().stream()
                .filter(r -> ObjectUtils.isNotEmpty(r) &&
                        rates.stream().anyMatch(rate -> rate.getIdtarifa().equals(r.getKey())))
                .collect(Collectors.toMap(e -> e.getKey().longValue(), e -> {
                    RateRestrictions out = new RateRestrictions();
                    es.onebox.event.common.domain.RateRestrictions restriction = e.getValue();
                    if (restriction.getDateRestriction() != null) {
                        RateDatesRestriction dateRestriction = new RateDatesRestriction();
                        dateRestriction.setFrom(restriction.getDateRestriction().getFrom());
                        dateRestriction.setTo(restriction.getDateRestriction().getTo());
                        out.setRateDatesRestriction(dateRestriction);
                    }

                    if (restriction.getCustomerTypeRestriction() != null) {
                        RateCustomerTypesRestriction customerTypeRestriction = new RateCustomerTypesRestriction();
                        customerTypeRestriction.setAllowedCustomerTypes(restriction.getCustomerTypeRestriction());
                        out.setRateCustomerTypesRestriction(customerTypeRestriction);
                    }

                    if (restriction.getRateRelationsRestriction() != null) {
                        RateRelationsRestriction rateRelationsRestriction = new RateRelationsRestriction();
                        rateRelationsRestriction.setRequiredRates(restriction.getRateRelationsRestriction().getRequiredRates());
                        if (restriction.getRateRelationsRestriction().getMaxItemsMultiplier() != null) {
                            if (restriction.getRateRelationsRestriction().getMaxItemsMultiplier() >= 1) {
                                rateRelationsRestriction.setRequired(null);
                                rateRelationsRestriction.setLocked(restriction.getRateRelationsRestriction().getMaxItemsMultiplier().longValue());
                            } else {
                                double tickets = 1 / restriction.getRateRelationsRestriction().getMaxItemsMultiplier();
                                rateRelationsRestriction.setRequired((long) tickets);
                                rateRelationsRestriction.setLocked(null);
                            }
                        }
                        rateRelationsRestriction.setRestrictedPriceZones(restriction.getRateRelationsRestriction().getRestrictedPriceZones());
                        RatePriceZoneCriteria criteria = restriction.getRateRelationsRestriction().getPriceZoneCriteria();
                        rateRelationsRestriction.setPriceTypeCriteria(criteria != null ? criteria : RatePriceZoneCriteria.EQUAL);
                        rateRelationsRestriction.setApplyToB2b(BooleanUtils.isTrue(restriction.getRateRelationsRestriction().getApplyToB2b()));
                        out.setRelationRestriction(rateRelationsRestriction);
                    }

                    if (restriction.getPriceZoneRestriction() != null) {
                        RatePriceZonesRestriction ratePriceZonesRestriction = new RatePriceZonesRestriction();
                        if (CollectionUtils.isNotEmpty(restriction.getPriceZoneRestriction())) {
                            ratePriceZonesRestriction.setRestrictedPriceZones(restriction.getPriceZoneRestriction());
                        }
                        ratePriceZonesRestriction.setApplyToB2b(BooleanUtils.isTrue(restriction.getPriceZoneRestrictionApplyToB2b()));
                        out.setRatePriceZonesRestriction(ratePriceZonesRestriction);
                    }

                    if (restriction.getChannelRestriction() != null) {
                        RateChannelRestriction rateChannelRestriction = new RateChannelRestriction();
                        rateChannelRestriction.setRestrictedChannels(restriction.getChannelRestriction());
                        out.setRateChannelRestriction(rateChannelRestriction);
                    }

                    if (restriction.getPeriodRestrictions() != null) {
                        RatePeriodRestriction ratePeriodRestriction = new RatePeriodRestriction();
                        ratePeriodRestriction.setRestrictedPeriods(restriction.getPeriodRestrictions());
                        out.setRatePeriodRestriction(ratePeriodRestriction);
                    }

                    if (restriction.getMaxItemRestriction() != null) {
                        out.setMaxItemRestriction(restriction.getMaxItemRestriction());
                    }

                    return out;
                }));
    }

    private static PriceZonesRestrictions resolvePriceZoneRestrictions(EventConfig eventConfig, SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getRestrictions() != null && sessionConfig.getRestrictions().getPriceZones() != null) {
            return sessionConfig.getRestrictions().getPriceZones();
        } else if (eventConfig != null && eventConfig.getRestrictions() != null && eventConfig.getRestrictions().getPriceZones() != null) {
            return eventConfig.getRestrictions().getPriceZones();
        }
        return null;
    }

    private static RatesRestrictions resolveRateRestrictions(EventConfig eventConfig, SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getRestrictions() != null && MapUtils.isNotEmpty(sessionConfig.getRestrictions().getRates())) {
            return sessionConfig.getRestrictions().getRates();

        } else if (eventConfig != null && eventConfig.getRestrictions() != null && MapUtils.isNotEmpty(eventConfig.getRestrictions().getRates())) {
            return eventConfig.getRestrictions().getRates();
        }
        return null;
    }

    private void buildSessionPackSettings(SessionForCatalogRecord sessionRecord, EventIndexationContext ctx, Long sessionId, CpanelEventoRecord eventoRecord, SessionData sessionData) {
        List<Long> sessionsBySessionPackId = seasonSessionDao.findSessionsBySessionPackId(sessionRecord.getIdsesion().longValue());

        var imageUrlBuilder = S3URLResolver.builder()
                .withUrl(staticDataContainer.getS3Repository())
                .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                .withSessionId(sessionId.intValue()).withEventId(eventoRecord.getIdevento())
                .withEntityId(eventoRecord.getIdentidad().longValue())
                .withOperatorId(ctx.getEntity().getOperator().getId().longValue())
                .build();

        var sessions = ctx.getAllSessions().stream().filter(s -> sessionsBySessionPackId.contains(s.getIdsesion().longValue())).toList();
        var relatedSessions = sessions.stream().map(s -> {
            List<CpanelElementosComEventoRecord> sessionComElement = extractComElements(ctx, s.getIdsesion().longValue());
            SessionRelated sessionRelated = new SessionRelated();
            sessionRelated.setId(s.getIdsesion().longValue());
            sessionRelated.setName(s.getNombre());
            sessionRelated.setDate(new SessionRelatedDate(s.getFechainiciosesion(), s.getShowdate(), s.getShowdatetime()));
            sessionRelated.setCommunicationElements(sessionComElement.stream().map(elem ->
                    getSessionCommunicationElement(elem, imageUrlBuilder, staticDataContainer)).toList());
            return sessionRelated;
        }).collect(Collectors.toList());

        sessionData.getSession().setSessionPackSettings(buildSessionPackSettings(sessions, relatedSessions));
        relatedSessions.stream().min(Comparator.comparing(sessionRelated -> sessionRelated.getDate().getBeginSessionDate()))
                .map(sessionRelated -> sessionRelated.getDate().getBeginSessionDate())
                .ifPresent(date -> sessionData.getSession().setBeginSessionDate(date));
        relatedSessions.stream().max(Comparator.comparing(sessionRelated -> sessionRelated.getDate().getBeginSessionDate()))
                .map(sessionRelated -> sessionRelated.getDate().getBeginSessionDate())
                .ifPresent(date -> sessionData.getSession().setEndSessionDate(date));
    }
}
