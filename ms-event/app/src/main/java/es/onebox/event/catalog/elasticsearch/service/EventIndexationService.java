package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.catalog.converter.ChannelAttributesConverter;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.catalog.dao.TemplateElementInfoCouchDao;
import es.onebox.event.catalog.dao.couch.AggregatedInfo;
import es.onebox.event.catalog.dao.couch.SessionTemplateInfo;
import es.onebox.event.catalog.dao.couch.TemplateElementInfo;
import es.onebox.event.catalog.dao.couch.TemplateInfoStatus;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionTaxesForCatalogRecord;
import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.presales.PresaleValidatorType;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceType;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelAttributesCouchDao;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.attributes.ChannelAttributesDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItem;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantField;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantFieldValidator;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.elasticsearch.utils.IndexerUtils;
import es.onebox.event.catalog.elasticsearch.utils.PresaleConfigUtil;
import es.onebox.event.catalog.service.InvalidableCacheService;
import es.onebox.event.catalog.utils.EventContextUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.entity.templateszones.dao.EntityTemplatesZonesCommElementDao;
import es.onebox.event.entity.templateszones.dao.EntityTemplatesZonesDao;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.entity.templateszones.enums.TemplateZonesTagType;
import es.onebox.event.events.converter.AttendantFieldConverter;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.CollectiveDao;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.dao.record.AttendantFieldRecord;
import es.onebox.event.events.dao.record.AttendantFieldValidatorRecord;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.prices.enums.PriceTypeFilter;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.domain.PackChannelItemsRecord;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.enums.PromotionStatus;
import es.onebox.event.promotions.service.EventPromotionsService;
import es.onebox.event.promotions.utils.PromotionUtils;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.sessions.dao.PresaleDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionTaxesDao;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.venues.dao.ProviderVenueDao;
import es.onebox.event.venues.dao.ProviderVenueTemplateDao;
import es.onebox.event.venues.dao.VenueDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTemplatesZonesElementsComRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTemplatesZonesEntityRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public final class EventIndexationService extends IndexationService {

    private final EventChannelDao eventChannelDao;
    private final VenueDao venueDao;
    private final ProviderVenueDao providerVenueDao;
    private final ProviderVenueTemplateDao providerVenueTemplateDao;
    private final EventPricesDao eventPricesDao;
    private final SeasonTicketDao seasonTicketDao;
    private final AttendantFieldDao attendantFieldDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final CatalogSurchargeService catalogSurchargeService;
    private final AttendantsConfigService attendantsConfigService;
    private final VenueDescriptorService venueDescriptorService;
    private final ProductChannelDao productChannelDao;
    private final PresaleDao presaleDao;
    private final CollectiveDao collectiveDao;
    private final ChannelAttributesCouchDao channelAttributesCouchDao;
    private final SeasonSessionDao seasonSessionDao;
    private final TemplateElementInfoCouchDao templateElementInfoCouchDao;
    private final PackDao packDao;
    private final PackItemsDao packItemsDao;
    private final PackItemSubsetDao packItemSubsetDao;
    private final PackChannelDao packChannelDao;
    private final ProductDao productDao;
    private final EntityTemplatesZonesDao entityTemplatesZonesDao;
    private final EntityTemplatesZonesCommElementDao entityTemplatesZonesCommElementDao;
    private final StaticDataContainer staticDataContainer;
    private final CatalogChannelPackCouchDao catalogChannelPackCouchDao;
    private final InvalidableCacheService invalidableCacheService;

    @Autowired
    public EventIndexationService(ChannelSessionIndexationService channelSessionIndexationService,
                                  ChannelAgencyIndexationService channelAgencyIndexationService,
                                  SessionDao sessionDao,
                                  ProviderVenueDao providerVenueDao,
                                  ProviderVenueTemplateDao providerVenueTemplateDao,
                                  SessionTaxesDao sessionTaxesDao,
                                  EventChannelDao eventChannelDao, EventPricesDao eventPricesDao,
                                  ChannelEventDao channelEventDao,
                                  ChannelDao channelDao, VenueDao venueDao,
                                  SalesGroupAssignmentDao salesGroupAssignmentDao,
                                  SeasonTicketDao seasonTicketDao,
                                  AttendantFieldDao attendantFieldDao,
                                  EventElasticDao eventElasticDao,
                                  EventConfigCouchDao eventConfigCouchDao,
                                  CatalogSurchargeService catalogSurchargeService,
                                  AttendantsConfigService attendantsConfigService,
                                  VenueDescriptorService venueDescriptorService,
                                  EntitiesRepository entitiesRepository,
                                  CacheRepository localCacheRepository,
                                  ProductChannelDao productChannelDao,
                                  PresaleDao presaleDao,
                                  CollectiveDao collectiveDao,
                                  EventPromotionsService eventPromotionsService,
                                  ChannelAttributesCouchDao channelAttributesCouchDao,
                                  TicketsRepository ticketsRepository, SeasonSessionDao seasonSessionDao,
                                  TemplateElementInfoCouchDao templateElementInfoCouchDao, PackDao packDao,
                                  PackItemsDao packItemsDao, PackItemSubsetDao packItemSubsetDao,
                                  PackChannelDao packChannelDao, ProductDao productDao,
                                  EntityTemplatesZonesDao entityTemplatesZonesDao,
                                  EntityTemplatesZonesCommElementDao entityTemplatesZonesCommElementDao,
                                  StaticDataContainer staticDataContainer, CatalogChannelPackCouchDao catalogChannelPackCouchDao,
                                  InvalidableCacheService invalidableCacheService) {
        super(channelSessionIndexationService, channelAgencyIndexationService, eventElasticDao, eventPromotionsService,
                localCacheRepository, entitiesRepository, channelDao, channelEventDao, ticketsRepository, sessionDao, sessionTaxesDao, salesGroupAssignmentDao);
        this.eventChannelDao = eventChannelDao;
        this.venueDao = venueDao;
        this.providerVenueDao = providerVenueDao;
        this.providerVenueTemplateDao = providerVenueTemplateDao;
        this.eventPricesDao = eventPricesDao;
        this.catalogSurchargeService = catalogSurchargeService;
        this.seasonTicketDao = seasonTicketDao;
        this.attendantsConfigService = attendantsConfigService;
        this.venueDescriptorService = venueDescriptorService;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.attendantFieldDao = attendantFieldDao;
        this.productChannelDao = productChannelDao;
        this.presaleDao = presaleDao;
        this.collectiveDao = collectiveDao;
        this.channelAttributesCouchDao = channelAttributesCouchDao;
        this.seasonSessionDao = seasonSessionDao;
        this.templateElementInfoCouchDao = templateElementInfoCouchDao;
        this.packDao = packDao;
        this.packItemsDao = packItemsDao;
        this.packItemSubsetDao = packItemSubsetDao;
        this.packChannelDao = packChannelDao;
        this.productDao = productDao;
        this.entityTemplatesZonesDao = entityTemplatesZonesDao;
        this.entityTemplatesZonesCommElementDao = entityTemplatesZonesCommElementDao;
        this.staticDataContainer = staticDataContainer;
        this.catalogChannelPackCouchDao = catalogChannelPackCouchDao;
        this.invalidableCacheService = invalidableCacheService;
    }

    public EventIndexationContext prepareEventContext(CpanelEventoRecord eventRecord, Long sessionId, EventIndexationType indexationType) {
        EventIndexationContext ctx = new EventIndexationContext(eventRecord, indexationType);

        prepareBaseEventContext(eventRecord, sessionId, ctx);

        //For partial basic indexation type only load basic event & session data
        if (EventIndexationType.PARTIAL_BASIC.equals(ctx.getType())) {
            return ctx;
        } else if (EventIndexationType.PARTIAL_COM_ELEMENTS.equals(ctx.getType())) {
            prepareComElementsEventContext(ctx);
            return ctx;
        }

        prepareFullEventContext(eventRecord, sessionId, ctx);

        return ctx;
    }

    private void prepareBaseEventContext(CpanelEventoRecord eventRecord, Long sessionId, EventIndexationContext ctx) {
        Long eventId = ctx.getEventId();
        EntityDTO eventEntity = localCacheRepository.cached(LocalCache.ENTITY_KEY, LocalCache.ENTITY_TTL, TimeUnit.SECONDS,
                () -> entitiesRepository.getEntity(ctx.getEvent().getIdentidad()), new Object[]{ctx.getEvent().getIdentidad()});
        ctx.setEventConfig(eventConfigCouchDao.get(String.valueOf(eventId)));
        ctx.setEntity(eventEntity);

        if (ctx.getEventConfig() != null && ctx.getEventConfig().getInventoryProvider() != null) {
            List<IdNameCodeDTO> providerVenues = providerVenueDao.getProviderVenues(ctx.getEventConfig().getInventoryProvider().name());
            List<IdNameCodeDTO> providerVenueTemplates = providerVenueTemplateDao.getProviderVenueTemplates(ctx.getEventConfig().getInventoryProvider().name());
            ctx.setExternalVenues(providerVenues);
            ctx.setExternalVenueTemplates(providerVenueTemplates);
        }

        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setEventId(List.of(eventId));
        filter.setIncludeDeleted(true);
        List<SessionForCatalogRecord> sessions = sessionDao.findSessionsForCatalog(filter);

        List<SessionTaxesForCatalogRecord> sessionTaxes = sessionTaxesDao.findSessionsTicketTaxes(eventId.intValue());
        ctx.setAllSessionTaxes(sessionTaxes);

        Map<Boolean, List<SessionForCatalogRecord>> sessionsByStatus = sessions.stream()
                .collect(Collectors.partitioningBy(session -> SessionStatus.DELETED.getId().equals(session.getEstado())));
        ctx.setDeletedSessions(sessionsByStatus.get(true));
        List<SessionForCatalogRecord> allActiveEventSessions = sessionsByStatus.get(false);

        if (sessionId != null) {
            List<SessionForCatalogRecord> filteredSessions = allActiveEventSessions.stream().
                    filter(s -> s.getIdsesion().equals(sessionId.intValue())).collect(Collectors.toList());
            ctx.setSessions(filteredSessions);
            ctx.setSessionFilter(sessionId);
            ctx.setAllSessions(allActiveEventSessions);
        } else {
            ctx.setAllSessions(allActiveEventSessions);
            ctx.setSessions(allActiveEventSessions);
        }
        List<EventChannelForCatalogRecord> eventChannels = EventContextUtils.filterDuplicates(eventChannelDao.getEventChannels(eventId));
        ctx.setEventChannels(eventChannels);

        List<CpanelCanalEventoRecord> channelEvents = IndexerUtils.getChannelEvents(channelEventDao.getChannelEvents(eventId), eventRecord);
        ctx.setChannelEvents(channelEvents);
        List<Long> sessionPackIds = ctx.getSessions().stream().filter(s -> CommonUtils.isTrue(s.getEsabono()))
                .map(CpanelSesionRecord::getIdsesion).map(Integer::longValue).toList();
        ctx.setSessionsBySessionPack(seasonSessionDao.findSessionsBySessionPackIds(sessionPackIds));
    }

    private void prepareComElementsEventContext(EventIndexationContext ctx) {
        channelSessionIndexationService.prepareComElementsChannelSessionsToIndex(ctx);
    }

    public void prepareFullEventContext(CpanelEventoRecord eventRecord, Long sessionId, EventIndexationContext ctx) {
        Long eventId = ctx.getEventId();

        invalidableCacheService.invalidateSurchargesCaches(ctx);
        prepareAttendantConfig(ctx);

        ctx.setPrices(eventPricesDao.getBasePricesByEventId(eventId, PriceTypeFilter.INDIVIDUAL));
        ctx.setQuotasByChannel(getEventQuotas(ctx.getChannelEvents()));
        ctx.setChannels(prepareChannels(EventContextUtils.filterChannels(ctx.getEventChannels())));
        ctx.setChannelAttributesByChannelId(prepareChannelAttributes(ctx.getChannels().keySet()));
        channelAgencyIndexationService.prepareB2BContext(ctx);

        List<Long> allEventSessionsReady = filterSessionsReady(ctx.getAllSessions());

        Map<Long, Long> sessionVenues = sessionDao.getSessionVenueIds(allEventSessionsReady);
        List<VenueRecord> venues = venueDao.getVenues(sessionVenues.values());
        ctx.setVenues(venues);
        ctx.setVenuesBySession(sessionVenues);
        Map<Long, Long> sessionVenueTemplates = sessionDao.getSessionVenueTemplateIds(allEventSessionsReady);
        List<Long> venueTemplateIds = sessionVenueTemplates.values().stream().distinct().collect(Collectors.toList());
        ctx.setVenueTemplatesBySession(sessionVenueTemplates);

        Map<Long, SessionDao.VenueTemplateInfo> venueTemplateInfos = sessionDao.getVenueTemplateInfos(venueTemplateIds);
        ctx.setVenueTemplateInfos(venueTemplateInfos);

        ctx.setUseTiers(CommonUtils.isTrue(eventRecord.getUsetieredpricing()));
        prepareVenueDescriptors(ctx, eventId, venueTemplateIds);

        ctx.setSessionPresaleConfigMap(presaleDao.getPresalesBySessionIds(allEventSessionsReady));
        prepareCollectivesPresalesInCtx(ctx);
        prepareChannelEventSurcharges(ctx);

        prepareProductSessions(ctx);

        if (EventType.SEASON_TICKET.getId().equals(eventRecord.getTipoevento())) {
            CpanelSeasonTicketRecord cpanelSeasonTicketRecord = seasonTicketDao.getById(eventId.intValue());
            ctx.setSeasonTicket(cpanelSeasonTicketRecord);
        }

        List<EventPromotionRecord> promotionRecords = getEventPromotionRecords(eventId);
        List<EventPromotion> promotions = eventPromotionsService.storePromotionsInCouchbase(eventId, promotionRecords);
        ctx.setEventPromotions(PromotionUtils.filterByStatus(promotions, PromotionStatus.ACTIVE));
        ctx.setPromotions(promotionRecords);

        prepareSecondaryLocationsForSale(ctx, ctx.getEntity(), sessionId);
        prepareRelatedPacks(ctx);
        channelSessionIndexationService.prepareChannelSessionsToIndex(ctx);
    }

    private List<EventPromotionRecord> getEventPromotionRecords(Long eventId) {
        List<EventPromotionRecord> promotionRecords = eventPromotionsService.getEventPromotionRecords(eventId);
        if (CollectionUtils.isNotEmpty(promotionRecords)) {
            return promotionRecords.stream().filter(promotion -> BooleanUtils.isNotTrue(promotion.getUseEntityPacks())).collect(Collectors.toList());
        }
        return promotionRecords;
    }

    private void prepareVenueDescriptors(EventIndexationContext ctx, Long eventId, List<Long> venueTemplateIds) {
        List<VenueDescriptor> venueDescriptors = this.venueDescriptorService.create(eventId, venueTemplateIds, ctx.getUseTiers());
        prepareVenueTemplateElementsInfo(ctx, venueDescriptors);
        ctx.setVenueDescriptor(venueDescriptors.stream().collect(Collectors.toMap(VenueDescriptor::getVenueConfigId, Function.identity())));
    }

    private void prepareVenueTemplateElementsInfo(EventIndexationContext ctx, List<VenueDescriptor> venueDescriptors) {
        if (CollectionUtils.isNotEmpty(venueDescriptors) && existsChannelsAgencyWithPriceTypeTagFilter(ctx)) {
            Map<Long, List<TemplateElementInfo>> templateElementInfoByTemplateId = getTemplateElementInfoByVenueTemplateId(venueDescriptors);

            if (MapUtils.isNotEmpty(templateElementInfoByTemplateId)) {
                Map<Long, Map<Long, Set<String>>> templateElementInfoTags = new HashMap<>();

                Map<Integer, EntityTemplateZonesDTO> entityTemplatesZones = getTemplatesZones(ctx, templateElementInfoByTemplateId);//templateZoneId-templateZones

                for (Map.Entry<Long, List<TemplateElementInfo>> entry : templateElementInfoByTemplateId.entrySet()) {
                    List<TemplateElementInfo> templateElementInfos = entry.getValue();
                    Map<Long, Set<String>> priceTypeWithTags = extractTagsFromTemplateElementInfos(templateElementInfos);
                    if (MapUtils.isNotEmpty(priceTypeWithTags)) {
                        templateElementInfoTags.put(entry.getKey(), priceTypeWithTags);
                    }

                }
                if (MapUtils.isNotEmpty(templateElementInfoTags)) {
                    ctx.setTemplateElementInfoTags(templateElementInfoTags);
                }
                if (MapUtils.isNotEmpty(templateElementInfoByTemplateId)) {
                    ctx.setTemplateElementInfoByTemplateId(templateElementInfoByTemplateId);
                }
                if (MapUtils.isNotEmpty(entityTemplatesZones)) {
                    ctx.setTemplateZonesById(entityTemplatesZones);
                }
            }
        }
    }


    private Map<Integer, EntityTemplateZonesDTO> getTemplatesZones(EventIndexationContext ctx, Map<Long, List<TemplateElementInfo>> templateElementInfoByTemplateId) {
        Map<Integer, EntityTemplateZonesDTO> result = new HashMap<>();
        Set<Integer> templatesZonesIds = new HashSet<>();
        if (MapUtils.isNotEmpty(templateElementInfoByTemplateId)) {
            templatesZonesIds = templateElementInfoByTemplateId.values().stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(TemplateElementInfo::getDefaultInfo)
                    .filter(Objects::nonNull)
                    .map(AggregatedInfo::getTemplatesZonesIds)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            Set<Integer> sessionTemplateZonesIds = templateElementInfoByTemplateId.values().stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(TemplateElementInfo::getSessionTemplateInfoList)
                    .filter(CollectionUtils::isNotEmpty)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(session -> TemplateInfoStatus.ENABLED.equals(session.getStatus()))
                    .map(SessionTemplateInfo::getAggregatedInfo)
                    .filter(Objects::nonNull)
                    .map(AggregatedInfo::getTemplatesZonesIds)
                    .filter(CollectionUtils::isNotEmpty)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            if (CollectionUtils.isNotEmpty(sessionTemplateZonesIds)) {
                templatesZonesIds.addAll(sessionTemplateZonesIds);
            }
        }

        if (CollectionUtils.isNotEmpty(templatesZonesIds)) {
            List<CpanelTemplatesZonesEntityRecord> templatesZones = entityTemplatesZonesDao.getTemplatesZones(ctx.getEvent().getIdentidad(), templatesZonesIds.stream().toList());
            if (CollectionUtils.isNotEmpty(templatesZones)) {
                var ids = templatesZones.stream().map(CpanelTemplatesZonesEntityRecord::getTemplatezoneid).toList();
                List<CpanelTemplatesZonesElementsComRecord> commElements = entityTemplatesZonesCommElementDao.getCommElements(ids);
                if (CollectionUtils.isNotEmpty(commElements)) {
                    result = fillTemplatesZones(templatesZones, commElements, staticDataContainer);
                }
            }
        }
        return result;
    }

    private static Map<Integer, EntityTemplateZonesDTO> fillTemplatesZones(List<CpanelTemplatesZonesEntityRecord> templatesZones,
                                                                           List<CpanelTemplatesZonesElementsComRecord> communicationElements,
                                                                           StaticDataContainer staticDataContainer) {
        Map<Integer, EntityTemplateZonesDTO> out = new HashMap<>();

        templatesZones.forEach(tz -> {
            EntityTemplateZonesDTO dto = new EntityTemplateZonesDTO();
            dto.setId(tz.getTemplatezoneid());
            dto.setName(tz.getName());
            dto.setCode(tz.getCode());

            if (CollectionUtils.isNotEmpty(communicationElements)) {
                Map<String, List<CatalogCommunicationElementDTO>> contentsTextsByLang =
                    communicationElements.stream()
                        .filter(commElem -> commElem.getValue() != null)
                        .filter(commElem -> tz.getTemplatezoneid().equals(commElem.getTemplatezoneid()))
                        .filter(commElem -> TemplateZonesTagType.NAME.getId().equals(commElem.getTagid()))
                        .collect(Collectors.groupingBy(
                                commElem -> staticDataContainer.getLanguage(commElem.getLanguageid()), // group by lang
                                Collectors.mapping(
                                        commElem -> {
                                            CatalogCommunicationElementDTO commElement = new CatalogCommunicationElementDTO();
                                            commElement.setTag(TemplateZonesTagType.getById(commElem.getTagid()).toString());
                                            commElement.setValue(commElem.getValue());
                                            return commElement;
                                        },
                                        Collectors.toList()
                                )
                        ));
            dto.setContentsTexts(contentsTextsByLang);
            }
            out.put(dto.getId(), dto);
        });
        return out;
    }

    private @NotNull Map<Long, List<TemplateElementInfo>> getTemplateElementInfoByVenueTemplateId(List<VenueDescriptor> venueDescriptors) {
        Map<Long, List<TemplateElementInfo>> templateElementInfoByTemplateId = new HashMap<>();
        for (VenueDescriptor venueDescriptor : venueDescriptors) {
            List<Long> priceTypeIds = venueDescriptor.getPriceTypes().stream().map(VenuePriceType::getId).toList();
            List<TemplateElementInfo> templateInfos =
                    templateElementInfoCouchDao.bulkGet(venueDescriptor.getVenueConfigId(), priceTypeIds,"priceType");
            if (CollectionUtils.isNotEmpty(templateInfos)) {
                templateElementInfoByTemplateId.put(venueDescriptor.getVenueConfigId().longValue(), templateInfos);
            }
        }
        return templateElementInfoByTemplateId;
    }

    private Map<Long, Set<String>> extractTagsFromTemplateElementInfos(List<TemplateElementInfo> templateElementInfos) {
        Map<Long, Set<String>> priceTypeWithTags = new HashMap<>();
        for (TemplateElementInfo templateElementInfo : templateElementInfos) {
            Set<String> tags = new HashSet<>();
            if (CollectionUtils.isNotEmpty(templateElementInfo.getTags())) {
                tags.addAll(templateElementInfo.getTags());
            }
            if (CollectionUtils.isNotEmpty(templateElementInfo.getSessionTemplateInfoList())) {
                for (SessionTemplateInfo sessionTemplateInfo : templateElementInfo.getSessionTemplateInfoList()) {
                    if (sessionTemplateInfo.getTags() != null) {
                        tags.addAll(sessionTemplateInfo.getTags());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(tags)) {
                priceTypeWithTags.put(templateElementInfo.getId(), tags);
            }
        }
        return MapUtils.isNotEmpty(priceTypeWithTags) ? priceTypeWithTags : null;
    }

    private boolean existsChannelsAgencyWithPriceTypeTagFilter(EventIndexationContext ctx) {
        if (MapUtils.isNotEmpty(ctx.getChannelsWithAgencies()) && MapUtils.isNotEmpty(ctx.getChannelConfigsCB())) {
            return ctx.getChannelConfigsCB().values().stream()
                    .anyMatch( ch -> BooleanUtils.isTrue(ch.getAllowPriceTypeTagFilter()));
        }
        return false;
    }


    public Long deleteEvent(Long eventId) {
        return eventElasticDao.deleteAllRelatedToEvent(eventId);
    }

    private static List<Long> filterSessionsReady(List<SessionForCatalogRecord> eventSessions) {
        List<Integer> sessionStatusIdList = Arrays.asList(SessionStatus.READY.getId(),
                SessionStatus.PLANNED.getId(),
                SessionStatus.SCHEDULED.getId(),
                SessionStatus.CANCELLED.getId(),
                SessionStatus.CANCELLED_EXTERNAL.getId(),
                SessionStatus.NOT_ACCOMPLISHED.getId(),
                SessionStatus.FINALIZED.getId());

        return eventSessions.stream()
                .filter(session -> sessionStatusIdList.contains(session.getEstado()))
                .map(CpanelSesionRecord::getIdsesion)
                .map(Integer::longValue)
                .collect(Collectors.toList());
    }

    private <T extends Number> Map<Long, ChannelInfo> prepareChannels(List<T> channelIds) {
        List<ChannelInfo> channelInfo = channelDao.getByIds(channelIds);
        return channelInfo.stream().collect(Collectors.toMap(ChannelInfo::getId, Function.identity()));
    }


    private Map<Integer, ChannelAttributes> prepareChannelAttributes(Collection<Long> channelIds) {
        Set<Key> keys = channelIds.stream().map(id -> new Key(new String[]{id.toString()})).collect(Collectors.toSet());
        List<ChannelAttributesDTO> channelAttributes = channelAttributesCouchDao.bulkGet(keys);
        return ChannelAttributesConverter.convert(channelAttributes);
    }

    private void prepareChannelEventSurcharges(EventIndexationContext ctx) {
        for (CpanelCanalEventoRecord channelEvent : ctx.getChannelEvents()) {
            Integer channelId = channelEvent.getIdcanal();
            ctx.getEventChannel(channelId).ifPresent(eventChannel ->
                    ctx.getChannelSurcharges().put(channelId.longValue(), catalogSurchargeService.getSurchargeRangesByChannelEventRelationShips(channelEvent, eventChannel)));
        }
    }

    private void prepareProductSessions(EventIndexationContext ctx) {
        List<Long> channelIds = ctx.getChannels().keySet().stream().toList();
        Map<String, List<Long>> channelSessionProducts = new HashMap<>(productChannelDao.findChannelSessionsProducts(channelIds));
        ctx.setChannelSessionProducts(channelSessionProducts);
    }

    private void prepareRelatedPacks(EventIndexationContext ctx) {
        Map<IdNameDTO, List<CpanelPackRecord>> packsByIdNameSession = packDao.getSessionPacks(ctx.getEventId().intValue());
        if (MapUtils.isEmpty(packsByIdNameSession)) {
            return;
        }

        List<Integer> packIds = packsByIdNameSession.values().stream().flatMap(List::stream).map(CpanelPackRecord::getIdpack).distinct().toList();

        preparePacksWithSessionFilter(ctx, packIds);

        List<PackChannelItemsRecord> packChannelItems = packChannelDao.getAcceptedPackChannelsByPackIdWithItems(packIds);
        if (CollectionUtils.isNotEmpty(packChannelItems)) {
            prepareItemDetails(ctx, packsByIdNameSession, packChannelItems);
        }
        ctx.setRelatedPacksItems(packChannelItems);
        Map<Integer, List<CpanelPackRecord>> packsBySession = packsByIdNameSession.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId().intValue(), Map.Entry::getValue));
        ctx.setPacksBySession(packsBySession);
    }

    private void preparePacksWithSessionFilter(EventIndexationContext ctx, List<Integer> packIds) {
        Set<Integer> packIdsMainItemTypeEvent = packDao.getPackIdsWithMainItemTypeEvent(packIds);
        if (CollectionUtils.isEmpty(packIdsMainItemTypeEvent)) {
            return;
        }

        Map<Integer, ChannelPack> packsWithSessionFilterByPackId = new HashMap<>();
        packIdsMainItemTypeEvent.forEach(packId -> {
            ChannelPack sessionFilter = buildSessionFilterForPack(packId);
            if (sessionFilter != null) {
                packsWithSessionFilterByPackId.put(packId, sessionFilter);
            }
        });

        if (MapUtils.isNotEmpty(packsWithSessionFilterByPackId)) {
            ctx.setPacksWithSessionFilterByPackId(packsWithSessionFilterByPackId);
        }
    }

    private ChannelPack buildSessionFilterForPack(Integer packId) {
        CpanelPackItemRecord mainPackItemRecord = packItemsDao.getPackMainItemRecordById(packId);
        if (mainPackItemRecord == null) {
            return null;
        }

        List<CpanelPackItemSubsetRecord> subsetRecords = packItemSubsetDao.getSubsetsByPackItemId(mainPackItemRecord.getIdpackitem());
        if (CollectionUtils.isEmpty(subsetRecords)) {
            return null;
        }

        List<Long> sessionIds = subsetRecords.stream()
                .filter(r -> PackItemSubsetType.SESSION.equals(PackItemSubsetType.getById(r.getType())))
                .map(r -> r.getIdsubitem().longValue())
                .toList();
        if (CollectionUtils.isEmpty(sessionIds)) {
            return null;
        }

        ChannelPack sessionFilter = new ChannelPack();
        sessionFilter.setId(packId.longValue());
        List<ChannelPackItem> items = sessionIds.stream().map(sessionId -> {
                    ChannelPackItem item = new ChannelPackItem();
                    item.setItemId(sessionId);
                    return item;
                }).collect(Collectors.toList());
        sessionFilter.setItems(items);
        return sessionFilter;
    }

    private void prepareItemDetails(EventIndexationContext ctx, Map<IdNameDTO, List<CpanelPackRecord>> packsBySession,
                                    List<PackChannelItemsRecord> packChannelItems) {
        Set<Integer> eventItemIds = new HashSet<>();
        Set<Integer> sessionItemIds = new HashSet<>();
        Set<Integer> productItemIds = new HashSet<>();
        packChannelItems.forEach(item -> {
            Integer itemId = item.getItemId().intValue();
            if (PackItemType.EVENT.getId().equals(item.getItemType())) {
                eventItemIds.add(itemId);
            } else if (PackItemType.SESSION.getId().equals(item.getItemType())) {
                sessionItemIds.add(itemId);
            } else if (PackItemType.PRODUCT.getId().equals(item.getItemType())) {
                productItemIds.add(itemId);
            }
        });

        Map<Integer, String> eventNames = new HashMap<>();
        if (CollectionUtils.isNotEmpty(eventItemIds)) {
            eventNames.put(ctx.getEvent().getIdevento(), ctx.getEvent().getNombre());
        }
        Map<Integer, String> sessionNames = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sessionItemIds)) {

            Set<Integer> sessionsNotFound = new HashSet<>();
            sessionItemIds.forEach(s -> {
                sessionsNotFound.add(s);
                packsBySession.keySet().forEach(key -> {
                    if (key.getId().equals(s.longValue())) {
                        sessionNames.put(s, key.getName());
                        sessionsNotFound.remove(s);
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(sessionsNotFound)) {
                sessionNames.putAll(sessionDao.getSessionsInfo(sessionsNotFound.stream().toList()).stream()
                        .collect(Collectors.toMap(CpanelSesionRecord::getIdsesion, CpanelSesionRecord::getNombre)));
            }
        }
        Map<Integer, String> productNames = new HashMap<>();
        if (CollectionUtils.isNotEmpty(productItemIds)) {
            productNames = productDao.getProductsInfo(productItemIds.stream().toList()).stream()
                    .collect(Collectors.toMap(CpanelProductRecord::getProductid, CpanelProductRecord::getName));
        }

        fillPackChannelItemName(eventNames, sessionNames, productNames, packChannelItems);
    }

    private void fillPackChannelItemName(Map<Integer, String> eventNames, Map<Integer, String> sessionNames,
                                         Map<Integer, String> productNames, List<PackChannelItemsRecord> packChannelItems) {
        packChannelItems.forEach(item -> {
            Integer itemId = item.getItemId().intValue();
            if (PackItemType.EVENT.getId().equals(item.getItemType())) {
                String name = eventNames.get(itemId);
                item.setItemName(name != null ? name : "");
            } else if (PackItemType.SESSION.getId().equals(item.getItemType())) {
                String name = sessionNames.get(itemId);
                item.setItemName(name != null ? name : "");
            } else if (PackItemType.PRODUCT.getId().equals(item.getItemType())) {
                String name = productNames.get(itemId);
                item.setItemName(name != null ? name : "");
            }
        });
    }

    private static List<EventAttendantField> getEventAttendantFields(List<AttendantFieldRecord> attendantFields) {
        return attendantFields.stream().map(f -> {
            var field = new EventAttendantField();
            field.setEventFieldId(f.getEventfieldid());
            field.setFieldId(f.getFieldid());
            field.setKey(f.getSid());
            field.setType(f.getFieldType());
            field.setMandatory(ConverterUtils.isByteAsATrue(f.getMandatory()));
            field.setOrder(f.getFieldorder().intValue());
            field.setMaxLength(f.getMaxlength());
            field.setMinLength(f.getMinlength());
            buildValidators(field, f.getValidators());
            return field;
        }).collect(Collectors.toList());
    }

    private static void buildValidators(EventAttendantField field,
                                        List<AttendantFieldValidatorRecord> validators) {
        if (CollectionUtils.isNotEmpty(validators)) {
            List<EventAttendantFieldValidator> vals = validators.stream()
                    .map(AttendantFieldConverter::convertValidator
                    ).toList();
            field.setValidators(vals);
        }
    }

    private void prepareCollectivesPresalesInCtx(EventIndexationContext ctx) {
        if (MapUtils.isNotEmpty(ctx.getSessionPresaleConfigMap())) {
            Set<Integer> collectiveIds = new HashSet<>();
            ctx.getSessionPresaleConfigMap()
                    .values()
                    .forEach(presales -> {
                        List<PresaleRecord> presaleActives = PresaleConfigUtil.getPresalesConfigActives(presales);
                        if (CollectionUtils.isNotEmpty(presaleActives)) {
                            Set<Integer> collectiveIdsPresales = presaleActives.stream()
                                    .filter(presale -> PresaleValidatorType.COLLECTIVE.getId().equals(presale.getTipovalidador()))
                                    .map(PresaleRecord::getIdvalidador)
                                    .collect(Collectors.toSet());
                            if (CollectionUtils.isNotEmpty(collectiveIdsPresales)) {
                                collectiveIds.addAll(collectiveIdsPresales);
                            }
                        }
                    });
            if (CollectionUtils.isNotEmpty(collectiveIds)) {
                ctx.setPresaleCollectives(collectiveDao.getBasicCollectiveInfoByIds(collectiveIds));
            }
        }
    }

    private void prepareAttendantConfig(EventIndexationContext ctx) {
        EventAttendantsConfigDTO eventsAttendantConfig = attendantsConfigService.getEventsAttendantConfig(ctx.getEventId());
        if (eventsAttendantConfig != null) {
            ctx.setEventAttendantsConfig(eventsAttendantConfig);
            var attendantFields = attendantFieldDao.getEventFieldsByEventId(ctx.getEventId());
            if (CollectionUtils.isNotEmpty(attendantFields)) {
                var validators = attendantFieldDao.getEventFieldsValidators(attendantFields.stream().map(AttendantFieldRecord::getEventfieldid).toList());
                AttendantFieldConverter.convertValidator(attendantFields, validators);
                ctx.setEventAttendantFields(getEventAttendantFields(attendantFields));

            }
        }
    }
}
