package es.onebox.event.catalog.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dao.venue.VenueConfigurationDao;
import es.onebox.event.catalog.dto.VenueTemplateType;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.SessionType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.PackElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.elasticsearch.pricematrix.Price;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.service.ChannelCatalogDefaultService;
import es.onebox.event.catalog.service.ChannelCatalogESQueryAdapter;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelEventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.QuotaAssignmentDao;
import es.onebox.event.events.dao.QuotaConfigDao;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItem;
import es.onebox.event.catalog.elasticsearch.exception.PackContextLoaderException;
import es.onebox.event.catalog.elasticsearch.context.PackIndexationContext;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.dao.PackChannelSaleRequestDao;
import es.onebox.event.packs.dao.PackCommunicationElementDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.PackItemsPriceTypeDao;
import es.onebox.event.packs.dao.PackPriceTypeMappingDao;
import es.onebox.event.packs.dao.PackRateDao;
import es.onebox.event.packs.dao.domain.PackRateRecord;
import es.onebox.event.packs.dao.domain.ItemPackPriceInfoRecord;
import es.onebox.event.packs.enums.PackChannelStatus;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.priceengine.packs.PackPrice;
import es.onebox.event.priceengine.packs.PackPriceItemInfo;
import es.onebox.event.priceengine.packs.PackPriceType;
import es.onebox.event.priceengine.packs.PackPriceTypeBase;
import es.onebox.event.priceengine.packs.PackRate;
import es.onebox.event.priceengine.packs.PackRateBase;
import es.onebox.event.priceengine.packs.PackTaxInfo;
import es.onebox.event.priceengine.packs.PackTaxes;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesBase;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurchargesBuilder;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import es.onebox.event.products.dao.ProductCatalogCouchDao;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.SaleRequestsStatus;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalCurrencyRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalSolicitudVentaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemZonaPrecioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.event.packs.enums.PackStatus.DELETED;
import static es.onebox.event.packs.enums.PackStatus.INACTIVE;
import static es.onebox.event.packs.utils.PackUtils.PACK_CATALOG_REFRESH;
import static java.util.Objects.isNull;

@Component
public class PackIndexationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackIndexationService.class);

    private final PackDao packDao;
    private final PackItemsDao packItemsDao;
    private final PackItemSubsetDao packItemSubsetDao;
    private final PackChannelDao packChannelDao;
    private final PackChannelSaleRequestDao packChannelSaleRequestDao;
    private final PackCommunicationElementDao packCommunicationElementDao;
    private final PackItemsPriceTypeDao packItemsPriceTypeDao;
    private final PackPriceTypeMappingDao packPriceTypeMappingDao;
    private final CatalogChannelPackCouchDao catalogChannelPackCouchDao;
    private final ChannelEventDao channelEventDao;
    private final ChannelCurrenciesDao channelCurrenciesDao;
    private final CatalogEventCouchDao catalogEventCouchDao;
    private final QuotaConfigDao quotaConfigDao;
    private final QuotaAssignmentDao quotaAssignmentDao;
    private final CatalogSessionCouchDao catalogSessionCouchDao;
    private final CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    private final ChannelSessionElasticDao channelSessionElasticDao;
    private final VenueConfigurationDao venueConfigurationDao;
    private final ProductCatalogCouchDao productCatalogCouchDao;
    private final ProductEventDao productEventDao;
    private final ProductChannelDao productChannelDao;
    private final ProductVariantStockCouchDao productVariantStockCouchDao;
    private final ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    private final SessionOccupationRepository sessionOccupationRepository;
    private final StaticDataContainer staticDataContainer;
    private final ProductVariantDao productVariantDao;
    private final PackRateDao packRateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final TaxDao taxDao;
    private final PriceEngineSimulationService priceEngineSimulationService;
    private final CatalogSurchargeService catalogSurchargeService;
    private final EventPromotionTemplateDao eventPromotionTemplateDao;

    @Value("${onebox.repository.S3SecureUrl}")
    private String s3domain;
    @Value("${onebox.repository.fileBasePath}")
    private String fileBasePath;

    public PackIndexationService(PackDao packDao,
                                 PackItemsDao packItemsDao, PackItemSubsetDao packItemSubsetDao,
                                 PackChannelDao packChannelDao,
                                 PackChannelSaleRequestDao packChannelSaleRequestDao,
                                 PackCommunicationElementDao packCommunicationElementDao,
                                 PackItemsPriceTypeDao packItemsPriceTypeDao,
                                 PackPriceTypeMappingDao packPriceTypeMappingDao,
                                 CatalogChannelPackCouchDao catalogChannelPackCouchDao,
                                 ChannelEventDao channelEventDao,
                                 ChannelCurrenciesDao channelCurrenciesDao,
                                 CatalogEventCouchDao catalogEventCouchDao,
                                 QuotaConfigDao quotaConfigDao,
                                 QuotaAssignmentDao quotaAssignmentDao,
                                 CatalogSessionCouchDao catalogSessionCouchDao,
                                 CatalogChannelEventCouchDao catalogChannelEventCouchDao,
                                 ChannelSessionElasticDao channelSessionElasticDao,
                                 VenueConfigurationDao venueConfigurationDao,
                                 ProductCatalogCouchDao productCatalogCouchDao,
                                 ProductEventDao productEventDao,
                                 ProductChannelDao productChannelDao,
                                 ProductVariantStockCouchDao productVariantStockCouchDao,
                                 ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao,
                                 SessionOccupationRepository sessionOccupationRepository,
                                 StaticDataContainer staticDataContainer,
                                 ProductVariantDao productVariantDao,
                                 PackRateDao packRateDao,
                                 PriceZoneAssignmentDao priceZoneAssignmentDao,
                                 TaxDao taxDao,
                                 PriceEngineSimulationService priceEngineSimulationService,
                                 CatalogSurchargeService catalogSurchargeService,
                                 EventPromotionTemplateDao eventPromotionTemplateDao) {
        this.packDao = packDao;
        this.packItemsDao = packItemsDao;
        this.packItemSubsetDao = packItemSubsetDao;
        this.packChannelDao = packChannelDao;
        this.packChannelSaleRequestDao = packChannelSaleRequestDao;
        this.packCommunicationElementDao = packCommunicationElementDao;
        this.packItemsPriceTypeDao = packItemsPriceTypeDao;
        this.packPriceTypeMappingDao = packPriceTypeMappingDao;
        this.catalogChannelPackCouchDao = catalogChannelPackCouchDao;
        this.channelEventDao = channelEventDao;
        this.channelCurrenciesDao = channelCurrenciesDao;
        this.catalogEventCouchDao = catalogEventCouchDao;
        this.quotaConfigDao = quotaConfigDao;
        this.quotaAssignmentDao = quotaAssignmentDao;
        this.catalogSessionCouchDao = catalogSessionCouchDao;
        this.catalogChannelEventCouchDao = catalogChannelEventCouchDao;
        this.channelSessionElasticDao = channelSessionElasticDao;
        this.venueConfigurationDao = venueConfigurationDao;
        this.productCatalogCouchDao = productCatalogCouchDao;
        this.productEventDao = productEventDao;
        this.productChannelDao = productChannelDao;
        this.productVariantStockCouchDao = productVariantStockCouchDao;
        this.productVariantSessionStockCouchDao = productVariantSessionStockCouchDao;
        this.sessionOccupationRepository = sessionOccupationRepository;
        this.staticDataContainer = staticDataContainer;
        this.productVariantDao = productVariantDao;
        this.packRateDao = packRateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.taxDao = taxDao;
        this.priceEngineSimulationService = priceEngineSimulationService;
        this.catalogSurchargeService = catalogSurchargeService;
        this.eventPromotionTemplateDao = eventPromotionTemplateDao;
    }

    public PackIndexationContext preparePackContext(Long packId, List<Long> filteredChannelIds, boolean isFullUpsert) {
        PackIndexationContext ctx = new PackIndexationContext(packId, isFullUpsert);
        fillPackRecordContext(ctx);
        fillPackItemsContext(ctx);
        if (ctx.getMainPackItemType().equals(PackItemType.EVENT)) {
            fillEventContext(ctx);
        }
        if (CollectionUtils.isNotEmpty(ctx.getSessionPackItemRecords())) {
            fillSessionContext(ctx);
        }
        if (CollectionUtils.isNotEmpty(ctx.getProductPackItemRecords())) {
            fillProductsContext(ctx);
        }
        fillChannelsContext(ctx, filteredChannelIds);
        fillMainVenueConfig(ctx);
        //fillPriceItemsInfoContext(ctx);

        //fillPartialChannelPacks(ctx);
        for (Long channelId : ctx.getChannelIds()) {
            try {
                validateChannelSaleRequest(channelId, ctx.getSaleRequestByChannelId());
                fillChannelEventContext(ctx, channelId);
                fillChannelSessionContext(ctx, channelId);
                ctx.getPackOnSaleByChannelId().put(channelId, getPackOnSale(ctx, channelId));
                ctx.getPackForSaleByChannelId().put(channelId, getPackForSale(ctx, channelId));
                ctx.getPackSoldOutByChannelId().put(channelId, getPackSolOut(ctx, channelId));
                //fillChannelPackPricesSimulation(ctx, channelId);
                //fillChannelPackPriceMatrix(ctx, channelId);
            } catch (PackContextLoaderException e) {
                ctx.getChannelContextExceptionsByChannelId().put(channelId, e);
                LOGGER.warn("{} pack : {} channel : {} - {}", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, e.getMessage());
            } catch (Exception e) {
                ctx.getChannelContextExceptionsByChannelId().put(channelId, e);
                LOGGER.error("{} pack : {} channel :{} - {} {}", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, e.getMessage(), e.getStackTrace());
            }
        }
        ctx.setS3Repository(s3domain + fileBasePath);
        ctx.setGetLanguageCode(staticDataContainer::getLanguage);
        ctx.setGetTag(staticDataContainer::getTag);
        ctx.setCommunicationElements(packCommunicationElementDao.findCommunicationElements(ctx.getPackId(), null));
        ctx.setDeletePackComElementByIdsMethod(packCommunicationElementDao::deletePackComElementByIds);
        return ctx;
    }

    private void fillPartialChannelPacks(PackIndexationContext ctx) {
        if (ctx.isFullUpsert()) return;

        List<ChannelPack> channelPacks = catalogChannelPackCouchDao.bulkGet(buildChannelPackKeys(ctx.getChannelIds(), ctx.getPackId()));
        Map<Long, ChannelPack> channelPacksByChannelId = channelPacks.stream()
                .collect(Collectors.toMap(ChannelPack::getChannelId, Function.identity()));

        ctx.setChannelPacksByChannelId(channelPacksByChannelId);
    }

    public static List<Key> buildChannelPackKeys(List<Long> channelIds, Long packId) {
        return channelIds.stream()
                .map(channelId -> new Key(new String[]{channelId.toString(), packId.toString()}))
                .collect(Collectors.toList());
    }

    private void fillPackRecordContext(PackIndexationContext ctx) {
        ctx.setPackDetailRecord(packDao.getPackDetailRecordById(ctx.getPackId().intValue()));
        validatePackDetailFound(ctx.getPackDetailRecord());
        validatePackType(ctx.getPackDetailRecord());
        ctx.setPackStatus(PackStatus.getById(ctx.getPackDetailRecord().getEstado()));
        validatePackStatus(ctx.getPackStatus());
    }

    private static void validatePackDetailFound(PackDetailRecord packDetailRecord) {
        if (isNull(packDetailRecord)) {
            throw new PackContextLoaderException("No pack record found", INACTIVE);
        }
    }

    private static void validatePackType(PackDetailRecord packDetailRecord) {
        if (!PackSubtype.PROMOTER.getId().equals(packDetailRecord.getTipo())) {
            throw new PackContextLoaderException("Pack type is not supported", INACTIVE);
        }
    }

    private static void validatePackStatus(PackStatus packStatus) {
        if (DELETED.equals(packStatus)) {
            throw new PackContextLoaderException("Pack is deleted", DELETED);
        }
        if (INACTIVE.equals(packStatus)) {
            throw new PackContextLoaderException("Pack is inactive", INACTIVE);
        }
    }


    private void fillPackItemsContext(PackIndexationContext ctx) {
        ctx.setPackItemRecords(packItemsDao.getPackItemRecordsById(ctx.getPackId().intValue()));
        validatePackItemRecordsFound(ctx.getPackItemRecords());
        ctx.setMainPackItemRecord(PackUtils.getMainPackItemRecord(ctx.getPackItemRecords()));
        ctx.setMainPackItemType(PackUtils.getType(ctx.getMainPackItemRecord()));
        validateMainItemFound(ctx.getMainPackItemRecord());
        validateMainItemIsNotAProduct(ctx.getMainPackItemRecord());
        ctx.setSessionPackItemRecords(PackUtils.getSessionPackItemRecords(ctx.getPackItemRecords()));
        ctx.setNoMainSessionPackItemRecordsWithPriceTypeConfig(PackUtils.getSessionPackItemNotMainWithPriceTypeConfig(ctx.getPackItemRecords()));
        ctx.setProductPackItemRecords(PackUtils.getProductPackItemRecords(ctx.getPackItemRecords()));
        ctx.setPriceTypesMappingByPackItemId(getPriceZoneMappingRecordsByPackItemId(ctx.getPackItemRecords(), ctx.getMainPackItemRecord()));
        ctx.setPriceTypesByPackItemId(getPriceTypesByPackItemId(ctx.getPackItemRecords()));
    }

    private static void validatePackItemRecordsFound(List<CpanelPackItemRecord> packItemRecords) {
        if (CollectionUtils.isEmpty(packItemRecords)) {
            throw new PackContextLoaderException("No pack item records found", INACTIVE);
        }
    }

    private static void validateMainItemFound(CpanelPackItemRecord mainPackItemRecord) {
        if (mainPackItemRecord == null) {
            throw new PackContextLoaderException("No main item found", INACTIVE);
        }
    }

    private static void validateMainItemIsNotAProduct(CpanelPackItemRecord mainPackItemRecord) {
        if (PackUtils.isProduct(mainPackItemRecord)) {
            throw new PackContextLoaderException("Main item cannot be a product", INACTIVE);
        }
    }

    private Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> getPriceZoneMappingRecordsByPackItemId(
            List<CpanelPackItemRecord> packItemRecords, CpanelPackItemRecord mainPackItemRecord) {
        if (noMappingsPresent(packItemRecords)) return null;

        List<CpanelPackZonaPrecioMappingRecord> priceZoneMappingRecords = packPriceTypeMappingDao.getPackSourceItemMappings(mainPackItemRecord.getIdpackitem());
        return priceZoneMappingRecords.stream()
                .collect(Collectors.groupingBy(CpanelPackZonaPrecioMappingRecord::getIdtargetpackitem,
                        Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    private static boolean noMappingsPresent(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream().map(CpanelPackItemRecord::getZonapreciomapping).noneMatch(CommonUtils::isTrue);
    }

    private void fillEventContext(PackIndexationContext ctx) {
        ctx.setPackWithEvent(true);
        Integer eventId = ctx.getMainPackItemRecord().getIditem();
        Event event = catalogEventCouchDao.get(eventId.toString());
        validateMainEventFound(event, eventId);
        validateMainEventIsNotDeleted(event, eventId);
        ctx.setMainEvent(event);
        ctx.setMainEventVenueId(getMainEventVenueId(ctx.getMainPackItemRecord()));
        ctx.setPackCurrency(event.getCurrency());
        ctx.setSessionsFilter(getSessionFilter(ctx.getPackId()));

        boolean isEventReady = isEventReady(event);
        if (!isEventReady) {
            LOGGER.warn("{} pack : {} event: {} - Event is not ready", PACK_CATALOG_REFRESH, ctx.getPackId(), eventId);
        }
        ctx.setEventReady(isEventReady);
    }

    private static void validateMainEventFound(Event event, Integer eventId) {
        if (event == null) {
            throw new PackContextLoaderException("No main event found for eventId: " + eventId, INACTIVE);
        }
    }

    private static void validateMainEventIsNotDeleted(Event event, Integer eventId) {
        if (EventStatus.DELETED.getId().equals(event.getEventStatus())) {
            throw new PackContextLoaderException("Main event is deleted for eventId: " + eventId, INACTIVE);
        }
    }

    private Integer getMainEventVenueId(CpanelPackItemRecord record) {
        Integer venueConfigId = record.getIdconfiguracion();
        validateMainEventVenueConfigIdFound(record, venueConfigId);
        Integer venueId = getVenueIdByVenueConfigId(venueConfigId);
        validateMainEventVenueIdFound(venueId, venueConfigId);
        return venueId;
    }

    private static void validateMainEventVenueConfigIdFound(CpanelPackItemRecord record, Integer venueConfigId) {
        if (venueConfigId == null) {
            throw new PackContextLoaderException("Main event venueConfigId not found for eventId: " + record.getIditem(), INACTIVE);
        }
    }

    private Integer getVenueIdByVenueConfigId(Integer venueConfigId) {
        return venueConfigurationDao.getVenueIdByVenueConfigId(venueConfigId);
    }

    private static void validateMainEventVenueIdFound(Integer venueId, Integer venueConfigId) {
        if (venueId == null) {
            throw new PackContextLoaderException("Main event venueId not found for venueConfigId: " + venueConfigId, INACTIVE);
        }
    }

    private List<Long> getSessionFilter(Long packId) {
        CpanelPackItemRecord mainPackItemRecord = packItemsDao.getPackMainItemRecordById(packId.intValue());
        if (mainPackItemRecord == null || !PackItemType.EVENT.equals(PackUtils.getType(mainPackItemRecord))) {
            return null;
        }

        List<CpanelPackItemSubsetRecord> subsetRecords = packItemSubsetDao.getSubsetsByPackItemId(mainPackItemRecord.getIdpackitem());
        if (CollectionUtils.isEmpty(subsetRecords)) {
            return null;
        }

        return subsetRecords.stream()
                .filter(r -> PackItemSubsetType.SESSION.equals(PackItemSubsetType.getById(r.getType())))
                .map(r -> r.getIdsubitem().longValue())
                .toList();
    }

    private static boolean isEventReady(Event mainEvent) {
        EventStatus eventStatus = EventStatus.byId(mainEvent.getEventStatus());
        return EventStatus.READY.equals(eventStatus);
    }

    private void fillSessionContext(PackIndexationContext ctx) {
        ctx.setPackWithSessions(true);
        List<Long> sessionIds = PackUtils.getItemIds(ctx.getSessionPackItemRecords());
        List<Session> sessions = catalogSessionCouchDao.bulkGet(sessionIds);
        validateAllSessionsAreFound(sessionIds, sessions);
        Map<Long, Session> sessionsById = sessions.stream().collect(Collectors.toMap(Session::getSessionId, Function.identity()));
        ctx.setSessionsById(sessionsById);
        ctx.setMainSession(getMainSession(ctx));

        List<Long> eventIds = sessions.stream().map(Session::getEventId).distinct().toList();
        List<Event> events = catalogEventCouchDao.bulkGet(eventIds);
        Map<Long, Event> eventsById = events.stream().collect(Collectors.toMap(Event::getEventId, Function.identity()));
        Map<Long, Event> eventsBySessionId = sessions.stream().collect(Collectors.toMap(Session::getSessionId, s -> eventsById.get(s.getEventId())));
        ctx.setEventsBySessionId(eventsBySessionId);
        validateAllSessionsMatchPackCurrency(ctx);
        ctx.setAreSessionEventsReady(areSessionEventsReady(ctx));
        ctx.setAreSessionsReady(areSessionReady(ctx));
    }

    private static void validateAllSessionsAreFound(List<Long> sessionIds, List<Session> sessions) {
        if (CollectionUtils.isEmpty(sessions)) {
            throw new PackContextLoaderException("No sessions found", INACTIVE);
        }
        for (Long sessionId : sessionIds) {
            if (sessions.stream().noneMatch(session -> session.getSessionId().equals(sessionId))) {
                throw new PackContextLoaderException("Session %s not found".formatted(sessionId), INACTIVE);
            }
        }
    }

    private Session getMainSession(PackIndexationContext ctx) {
        if (!ctx.getMainPackItemType().equals(PackItemType.SESSION)) {
            return null;
        }
        Long mainSessionId = ctx.getMainPackItemRecord().getIditem().longValue();
        return ctx.getSessionsById().get(mainSessionId);
    }

    private static void validateAllSessionsMatchPackCurrency(PackIndexationContext ctx) {
        for (Map.Entry<Long, Event> entry : ctx.getEventsBySessionId().entrySet()) {
            Long sessionId = entry.getKey();
            Event event = entry.getValue();
            if (ctx.getPackCurrency() == null) {
                ctx.setPackCurrency(event.getCurrency());
            }
            if (!event.getCurrency().equals(ctx.getPackCurrency())) {
                String msg = "session : %s event : %s - Session doesn't match pack currency";
                throw new PackContextLoaderException(msg.formatted(sessionId, event.getEventId()), INACTIVE);
            }
        }
    }

    private static boolean areSessionReady(PackIndexationContext ctx) {
        return ctx.getSessionsById().values().stream().allMatch(session -> {
            boolean isSessionReady = isSessionReady(session);
            if (!isSessionReady) {
                LOGGER.warn("{} pack : {} session: {} - Session  is not ready", PACK_CATALOG_REFRESH, ctx.getPackId(), session.getSessionId());
            }
            return isSessionReady;
        });
    }

    private static boolean isSessionReady(Session session) {
        SessionStatus sessionStatus = SessionStatus.byId(session.getSessionStatus().intValue());
        return SessionStatus.READY.equals(sessionStatus);
    }


    private static boolean areSessionEventsReady(PackIndexationContext ctx) {
        return ctx.getEventsBySessionId().entrySet().stream().allMatch(entry -> {
            Long sessionId = entry.getKey();
            Event event = entry.getValue();
            boolean isEventReady = isEventReady(event);
            if (!isEventReady) {
                LOGGER.warn("{} pack : {} session: {} event: {} - Session event is not ready", PACK_CATALOG_REFRESH, ctx.getPackId(), sessionId, event.getEventId());
            }
            return isEventReady;
        });
    }

    private void fillProductsContext(PackIndexationContext ctx) {
        ctx.setPackWithProducts(true);
        List<Long> productIds = PackUtils.getItemIds(ctx.getProductPackItemRecords());
        List<ProductCatalogDocument> catalogProducts = getCatalogProducts(productIds);
        validateAllProductsAreFound(catalogProducts, productIds);
        ctx.setCatalogProductsById(catalogProducts.stream().collect(Collectors.toMap(ProductCatalogDocument::getId, Function.identity())));
        validateAllProductsMatchPackCurrency(ctx);
        validateMainItemIsRelatedWithAllProducts(ctx, productIds);
    }

    private void validateMainItemIsRelatedWithAllProducts(PackIndexationContext ctx, List<Long> productIds) {
        for (Long productId : productIds) {
            if (ctx.getMainEvent() != null) {
                Long eventId = ctx.getMainEvent().getEventId();
                ProductEventRecord productEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
                validateMainEventIsRelatedWithProduct(productId, productEventRecord, eventId);
            } else if (ctx.getMainSession() != null) {
                Long sessionId = ctx.getMainSession().getSessionId();
                Long eventId = ctx.getEventsBySessionId().get(sessionId).getEventId();
                ProductEventRecord productEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
                validateMainSessionIsRelatedWithProduct(productId, productEventRecord, sessionId, eventId);
            }
        }
    }

    private static void validateMainEventIsRelatedWithProduct(Long productId, ProductEventRecord productEventRecord, Long eventId) {
        if (productEventRecord == null) {
            String msg = "Main event %s is not related with product %s";
            throw new PackContextLoaderException(msg.formatted(eventId, productId), INACTIVE);
        }
    }

    private static void validateMainSessionIsRelatedWithProduct(Long productId, ProductEventRecord productEventRecord, Long sessionId, Long eventId) {
        if (productEventRecord == null) {
            String msg = "Event %s of the main session %s is not related with product %s";
            throw new PackContextLoaderException(msg.formatted(eventId, sessionId, productId), INACTIVE);
        }
    }

    private List<ProductCatalogDocument> getCatalogProducts(List<Long> productIds) {
        List<Key> keys = productIds.stream()
                .distinct()
                .map(productId -> new Key(new String[]{String.valueOf(productId)}))
                .toList();
        return productCatalogCouchDao.bulkGet(keys);
    }

    private static void validateAllProductsAreFound(List<ProductCatalogDocument> catalogProducts, List<Long> productIds) {
        if (CollectionUtils.isEmpty(catalogProducts)) {
            throw new PackContextLoaderException("No products found", INACTIVE);
        }
        for (ProductCatalogDocument product : catalogProducts) {
            if (!productIds.contains(product.getId())) {
                throw new PackContextLoaderException("Product %s not found".formatted(product.getId()), INACTIVE);
            }
        }
    }

    private static void validateAllProductsMatchPackCurrency(PackIndexationContext ctx) {
        for (Map.Entry<Long, ProductCatalogDocument> entry : ctx.getCatalogProductsById().entrySet()) {
            Long productId = entry.getKey();
            ProductCatalogDocument catalogProduct = entry.getValue();
            if (!catalogProduct.getCurrencyId().equals(ctx.getPackCurrency())) {
                String msg = "Product %s doesn't match pack currency %s";
                throw new PackContextLoaderException(msg.formatted(productId, ctx.getPackCurrency()), INACTIVE);
            }
        }
    }

    private void fillChannelsContext(PackIndexationContext ctx, List<Long> filteredChannelIds) {
        Long packId = ctx.getPackId();
        List<CpanelPackCanalRecord> packChannelRecords = getCpanelPackCanalRecords(packId, filteredChannelIds);
        validatePackChannelRecords(packChannelRecords);
        ctx.setPackChannelRecordsByChannelId(getPackChannelRecordsByChannelId(packChannelRecords));
        List<Long> relatedChannelIds = getRelatedChannelIds(packChannelRecords);
        List<CpanelPackCanalSolicitudVentaRecord> saleRequestRecords = packChannelSaleRequestDao.getPackSaleRequests(packId, relatedChannelIds);
        validateSaleRequestRecords(saleRequestRecords);
        ctx.setChannelIds(getChannelIds(saleRequestRecords));
        ctx.setSaleRequestByChannelId(getSaleRequestRecordsByChannelId(saleRequestRecords));
        ctx.setChannelCurrenciesByChannelId(getChannelCurrenciesByChannelId(ctx.getChannelIds()));
    }

    private List<CpanelPackCanalRecord> getCpanelPackCanalRecords(Long packId, List<Long> filteredChannelIds) {
        List<CpanelPackCanalRecord> packCanalRecords;
        if (CollectionUtils.isEmpty(filteredChannelIds)) {
            packCanalRecords = packChannelDao.getPackChannels(packId);
        } else {
            packCanalRecords = packChannelDao.getPackChannels(packId, filteredChannelIds);
        }
        return packCanalRecords;
    }

    private static void validatePackChannelRecords(List<CpanelPackCanalRecord> packChannelRecords) {
        if (CollectionUtils.isEmpty(packChannelRecords)) {
            throw new PackContextLoaderException("No pack channel records found", INACTIVE);
        }
    }

    private static Map<Long, CpanelPackCanalRecord> getPackChannelRecordsByChannelId(List<CpanelPackCanalRecord> packChannelRecords) {
        return packChannelRecords.stream().collect(Collectors.toMap(r -> r.getIdcanal().longValue(), Function.identity()));
    }


    private static List<Long> getRelatedChannelIds(List<CpanelPackCanalRecord> packCanalRecords) {
        return packCanalRecords.stream().map(CpanelPackCanalRecord::getIdcanal).map(Integer::longValue).toList();
    }

    private static void validateSaleRequestRecords(List<CpanelPackCanalSolicitudVentaRecord> saleRequestRecords) {
        if (CollectionUtils.isEmpty(saleRequestRecords)) {
            throw new PackContextLoaderException("No pack sale request records found", INACTIVE);
        }
    }

    private static List<Long> getChannelIds(List<CpanelPackCanalSolicitudVentaRecord> saleRequestRecords) {
        return saleRequestRecords.stream()
                .map(CpanelPackCanalSolicitudVentaRecord::getIdcanal)
                .map(Integer::longValue)
                .distinct()
                .toList();
    }

    private static Map<Integer, CpanelPackCanalSolicitudVentaRecord> getSaleRequestRecordsByChannelId(List<CpanelPackCanalSolicitudVentaRecord> saleRequestRecords) {
        return saleRequestRecords.stream()
                .collect(Collectors.toMap(CpanelPackCanalSolicitudVentaRecord::getIdcanal, Function.identity()));
    }

    private Map<Integer, List<CpanelCanalCurrencyRecord>> getChannelCurrenciesByChannelId(List<Long> channelIds) {
        List<CpanelCanalCurrencyRecord> channelCurrencyRecords = channelCurrenciesDao.getCurrencies(channelIds);
        validateChannelCurrenciesFound(channelCurrencyRecords);
        Map<Integer, List<CpanelCanalCurrencyRecord>> channelCurrenciesByChannelId = channelCurrencyRecords.stream().collect(Collectors.groupingBy(CpanelCanalCurrencyRecord::getIdcanal));
        validateChannelCurrencies(channelCurrencyRecords, channelIds);
        return channelCurrenciesByChannelId;
    }

    private static void validateChannelCurrenciesFound(List<CpanelCanalCurrencyRecord> channelCurrencyRecords) {
        if (CollectionUtils.isEmpty(channelCurrencyRecords)) {
            throw new PackContextLoaderException("No channel currencies found", PackStatus.INACTIVE);
        }
    }

    private static void validateChannelCurrencies(List<CpanelCanalCurrencyRecord> channelCurrencyRecords, List<Long> channelIds) {
        Set<Integer> currencyChannelIds = channelCurrencyRecords.stream().map(CpanelCanalCurrencyRecord::getIdcanal).collect(Collectors.toSet());
        channelIds.forEach(channelId -> {
            if (!currencyChannelIds.contains(channelId.intValue())) {
                throw new PackContextLoaderException("Channel %s currency not found".formatted(channelId), INACTIVE);
            }
        });
    }

    private void fillMainVenueConfig(PackIndexationContext ctx) {
        Integer venueConfigId = switch (ctx.getMainPackItemType()) {
            case EVENT -> ctx.getMainPackItemRecord().getIdconfiguracion();
            case SESSION -> ctx.getMainSession().getVenueConfigId().intValue();
            case PRODUCT -> throw new UnsupportedOperationException("Invalid PRODUCT pack item type");
        };

        CpanelConfigRecintoRecord venueConfigRecord = venueConfigurationDao.getById(venueConfigId);
        ctx.setMainVenueConfig(venueConfigRecord);

    }

    private void fillPriceItemsInfoContext(PackIndexationContext ctx) {
        ctx.setPricingType(PackPricingType.getById(ctx.getPackDetailRecord().getIdtipopricing()));
        int packId = ctx.getPackId().intValue();

        Map<Integer, Double> priceByProductPackItemId = productVariantDao.getProductVariantPricesByPackId(packId);
        validateProductPackItemsHaveNotMissedPrices(ctx, priceByProductPackItemId);
        ctx.setPriceByProductPackItemId(priceByProductPackItemId);

        ctx.setItemPackPriceInfoRecordsByPackItemId(getItemPackPriceInfoRecordsByPackItemId(ctx, packId));

        List<PackRateRecord> packRates = packRateDao.getDetailedRatesByPackId(packId);
        validatePackHasPackRates(ctx.getPackId(), packRates);
        ctx.setPackRates(packRates);

        Map<Integer, List<PriceRecord>> mainPackItemPriceRecordsByRateId = getMainPackItemPriceRecordsByRateId(ctx);
        ctx.setMainPackItemPriceRecordsByRateId(mainPackItemPriceRecordsByRateId);

        PackVenueConfigPricesBase packVenueConfigMapBase = new PackVenueConfigPricesBase();
        packVenueConfigMapBase.setVenueConfig(getVenueConfig(ctx));
        packVenueConfigMapBase.setRates(getPackRates(ctx));
        ctx.setPackVenueConfigPricesBase(packVenueConfigMapBase);

        CpanelImpuestoRecord packTax = taxDao.getPackTax(ctx.getPackId());
        validatePackHasTax(ctx.getPackId(), packTax);
        ctx.setPackTax(packTax);
    }

    private Map<Integer, List<ItemPackPriceInfoRecord>> getItemPackPriceInfoRecordsByPackItemId(PackIndexationContext ctx, int packId) {
        boolean packHasPriceMappings = MapUtils.isNotEmpty(ctx.getPriceTypesMappingByPackItemId());

        List<ItemPackPriceInfoRecord> itemPackPriceInfoRecords = packHasPriceMappings ?
                packPriceTypeMappingDao.getItemPackPriceInfoRecordsByPackItemId(packId) :
                packPriceTypeMappingDao.getItemPackPriceInfoRecordsByUnmappedPackItemId(packId);
        validatePackItemsHaveNotMissedPrices(ctx.getPackId(), itemPackPriceInfoRecords);

        return itemPackPriceInfoRecords.stream().collect(Collectors.groupingBy(ItemPackPriceInfoRecord::getPackItemId));
    }

    private Map<Integer, List<PriceRecord>> getMainPackItemPriceRecordsByRateId(PackIndexationContext ctx) {
        List<PriceRecord> mainItemPriceRecords = switch (ctx.getPricingType()) {
            case COMBINED, INCREMENTAL -> {
                Map<Integer, Integer> packRateIdByEventRateId = ctx.getPackRates().stream()
                        .collect(Collectors.toMap(CpanelTarifaPackRecord::getIdtarifaevento, CpanelTarifaPackRecord::getIdtarifa));
                Integer mainPackItemId = ctx.getMainPackItemRecord().getIdpackitem();
                List<ItemPackPriceInfoRecord> mainItemPackPriceInfoRecords = ctx.getItemPackPriceInfoRecordsByPackItemId().get(mainPackItemId);
                yield mainItemPackPriceInfoRecords.stream().map(record -> {
                    PriceRecord priceRecord = new PriceRecord();
                    priceRecord.setPrice(record.getItemPrice());
                    priceRecord.setPriceZoneId(record.getMainPriceZone());
                    priceRecord.setPriceZoneCode(record.getMainPriceZoneName());
                    priceRecord.setRateId(packRateIdByEventRateId.get(record.getItemRateId()));
                    return priceRecord;
                }).toList();

            }
            case NEW_PRICE -> {
                Integer[] rateIds = ctx.getPackRates().stream().map(CpanelTarifaPackRecord::getIdtarifa).toArray(Integer[]::new);
                yield priceZoneAssignmentDao.getPrices(rateIds);
            }
        };
        return mainItemPriceRecords.stream().collect(Collectors.groupingBy(PriceRecord::getRateId));
    }

    private static void validateProductPackItemsHaveNotMissedPrices(PackIndexationContext ctx, Map<Integer, Double> map) {
        if (BooleanUtils.isNotTrue(ctx.getPackWithProducts())) return;

        if (MapUtils.isEmpty(map)) {
            throw new PackContextLoaderException("Pack %s product items have missed prices".formatted(ctx.getPackId()), PackStatus.INACTIVE);
        }

        ctx.getProductPackItemRecords().forEach(item -> {
            if (!map.containsKey(item.getIdpackitem())) {
                throw new PackContextLoaderException("Pack %s product %s has missed price".formatted(ctx.getPackId(), item.getIditem()), PackStatus.INACTIVE);
            }
        });
    }

    private static void validatePackItemsHaveNotMissedPrices(Long packId, List<ItemPackPriceInfoRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            throw new PackContextLoaderException("Pack %s items have missed prices".formatted(packId), PackStatus.INACTIVE);
        }
    }

    private static void validatePackHasPackRates(Long packId, List<PackRateRecord> ratesByPackId) {
        if (CollectionUtils.isEmpty(ratesByPackId)) {
            throw new PackContextLoaderException("Pack %s has no pack rates".formatted(packId), PackStatus.INACTIVE);
        }
    }

    private static IdNameDTO getVenueConfig(PackIndexationContext ctx) {
        CpanelConfigRecintoRecord mainVenueConfig = ctx.getMainVenueConfig();
        return new IdNameDTO(mainVenueConfig.getIdconfiguracion().longValue(), mainVenueConfig.getNombreconfiguracion());
    }

    public static List<PackRateBase> getPackRates(PackIndexationContext ctx) {
        List<PackRateBase> packRateBases = new ArrayList<>();
        for (PackRateRecord packRateRecord : ctx.getPackRates()) {
            PackRateBase packRateBase = new PackRateBase();
            packRateBase.setId(packRateRecord.getIdtarifa().longValue());
            packRateBase.setName(packRateRecord.getName());
            packRateBase.setDefaultRate(packRateRecord.getDefecto());
            packRateBase.setPriceTypes(getPackPriceTypes(ctx, packRateRecord));

            packRateBases.add(packRateBase);
        }
        return packRateBases;
    }

    private static List<PackPriceTypeBase> getPackPriceTypes(PackIndexationContext ctx, PackRateRecord packRateRecord) {
        List<PriceRecord> priceRecords = ctx.getMainPackItemPriceRecordsByRateId().get(packRateRecord.getIdtarifa());

        List<PackPriceTypeBase> priceTypes = new ArrayList<>();
        for (PriceRecord priceRecord : priceRecords) {
            PackPriceTypeBase packPriceTypeBase = new PackPriceTypeBase();
            packPriceTypeBase.setId(priceRecord.getPriceZoneId().longValue());
            packPriceTypeBase.setName(priceRecord.getPriceZoneCode());
            packPriceTypeBase.setPrice(getPackPrice(ctx, packRateRecord, priceRecord));

            priceTypes.add(packPriceTypeBase);
        }
        return priceTypes;
    }

    private static PackPrice getPackPrice(PackIndexationContext ctx, PackRateRecord packRateRecord, PriceRecord priceRecord) {
        PackPrice packPrice = new PackPrice();
        List<PackPriceItemInfo> packPriceItemInfos = getPackPriceItemInfos(ctx, packRateRecord, priceRecord);
        Double total = NumberUtils.scale(getPackPriceTypeTotal(ctx, priceRecord, packPriceItemInfos)).doubleValue();
        overrideMainItemWithTotalPrice(packPriceItemInfos, total, ctx.getMainPackItemRecord().getIditem());
        packPrice.setTotal(total);
        packPrice.setItemsInfo(packPriceItemInfos);
        return packPrice;
    }

    private static Double getPackPriceTypeTotal(PackIndexationContext ctx, PriceRecord packPriceRecord, List<PackPriceItemInfo> packPriceItemInfos) {
        return switch (ctx.getPricingType()) {
            case COMBINED -> packPriceItemInfos.stream().mapToDouble(PackPriceItemInfo::getItemPrice).sum();
            case INCREMENTAL -> packPriceRecord.getPrice() + ctx.getPackDetailRecord().getIncremementoprecio();
            case NEW_PRICE -> packPriceRecord.getPrice();
        };
    }

    private static void overrideMainItemWithTotalPrice(List<PackPriceItemInfo> packPriceItemInfos, Double total, Integer mainItemId) {
        packPriceItemInfos.stream()
                .filter(itemInfo -> itemInfo.getItemId().equals(mainItemId.longValue()))
                .findFirst()
                .ifPresent(itemInfo -> itemInfo.setItemPackPrice(NumberUtils.scale(total).doubleValue()));
    }

    private static List<PackPriceItemInfo> getPackPriceItemInfos(PackIndexationContext ctx, PackRateRecord packRateRecord, PriceRecord packPriceRecord) {
        List<PackPriceItemInfo> packPriceItemInfos = new ArrayList<>();
        for (CpanelPackItemRecord packItemRecord : ctx.getPackItemRecords()) {
            PackPriceItemInfo packPriceItemInfo = new PackPriceItemInfo();
            packPriceItemInfo.setItemPrice(getItemPrice(ctx, packRateRecord, packPriceRecord, packItemRecord));
            packPriceItemInfo.setType(es.onebox.event.priceengine.packs.PackItemType.getById(packItemRecord.getTipoitem()));
            packPriceItemInfo.setItemId(packItemRecord.getIditem().longValue());
            packPriceItemInfo.setItemPackPrice(0D); // TODO: it can change to a mapped specific item pack price

            packPriceItemInfos.add(packPriceItemInfo);
        }
        return packPriceItemInfos;
    }

    private static Double getItemPrice(PackIndexationContext ctx, PackRateRecord packRateRecord, PriceRecord packPriceRecord, CpanelPackItemRecord packItemRecord) {
        return switch (PackUtils.getType(packItemRecord)) {
            case EVENT, SESSION -> {
                List<ItemPackPriceInfoRecord> itemPackPriceInfoRecordList =
                        ctx.getItemPackPriceInfoRecordsByPackItemId().get(packItemRecord.getIdpackitem());
                boolean packHasPriceMappings = MapUtils.isNotEmpty(ctx.getPriceTypesMappingByPackItemId());
                ItemPackPriceInfoRecord itemPackPriceInfoRecord;

                if (PackUtils.isMain(packItemRecord)) {
                    itemPackPriceInfoRecord = itemPackPriceInfoRecordList.stream()
                            .filter(r -> packPriceRecord.getPriceZoneId().equals(r.getMainPriceZone())
                                    && packRateRecord.getIdtarifaevento().equals(r.getItemRateId()))
                            .findFirst().orElseThrow(() -> new PackContextLoaderException("Pack %s has invalid item price for main item".formatted(ctx.getPackId()), PackStatus.INACTIVE));
                } else if (packHasPriceMappings) {
                    itemPackPriceInfoRecord = itemPackPriceInfoRecordList.stream()
                            .filter(r -> packPriceRecord.getPriceZoneId().equals(r.getMainPriceZone()))
                            .findFirst().orElseThrow(() -> new PackContextLoaderException("Pack %s has invalid item price for item with mappings".formatted(ctx.getPackId()), PackStatus.INACTIVE));
                } else if (packItemRecord.getIdzonaprecio() != null) {
                    itemPackPriceInfoRecord = itemPackPriceInfoRecordList.get(0);
                } else if (packItemRecord.getIdzonaprecio() == null) {
                    itemPackPriceInfoRecordList = ctx.getItemPackPriceInfoRecordsByPackItemId()
                            .get(ctx.getMainPackItemRecord().getIdpackitem());
                    itemPackPriceInfoRecord = itemPackPriceInfoRecordList.stream()
                            .filter(r -> packPriceRecord.getPriceZoneId().equals(r.getMainPriceZone())
                                    && packRateRecord.getIdtarifaevento().equals(r.getItemRateId()))
                            .findFirst().orElseThrow(() -> new PackContextLoaderException("Pack %s has invalid item price for item with same venue".formatted(ctx.getPackId()), PackStatus.INACTIVE));
                } else {
                    throw new PackContextLoaderException("Pack %s has an invalid price zone mappings configuration".formatted(ctx.getPackId()), PackStatus.INACTIVE);
                }

                yield itemPackPriceInfoRecord.getItemPrice();
            }
            case PRODUCT -> ctx.getPriceByProductPackItemId().get(packItemRecord.getIdpackitem());
        };
    }

    private static void validatePackHasTax(Long packId, CpanelImpuestoRecord packTax) {
        if (isNull(packTax)) {
            throw new PackContextLoaderException("Pack %s has not tax".formatted(packId), PackStatus.INACTIVE);
        }
    }

    private static void validateChannelSaleRequest(Long channelId, Map<Integer, CpanelPackCanalSolicitudVentaRecord> saleRequestRecordsByChannelId) {
        CpanelPackCanalSolicitudVentaRecord saleRequest = saleRequestRecordsByChannelId.get(channelId.intValue());
        if (saleRequest == null || isNotAccepted(saleRequest)) {
            throw new PackContextLoaderException("Sale request not accepted", PackStatus.INACTIVE);
        }
    }

    private static boolean isNotAccepted(CpanelPackCanalSolicitudVentaRecord record) {
        return !PackChannelStatus.ACCEPTED.getId().equals(record.getEstado());
    }

    private void fillChannelEventContext(PackIndexationContext ctx, Long channelId) {
        if (PackUtils.isEvent(ctx.getMainPackItemRecord())) {
            Long eventId = Long.valueOf(ctx.getMainPackItemRecord().getIditem());
            ChannelEvent channelEvent = catalogChannelEventCouchDao.get(channelId.toString(), eventId.toString());
            validateMainChannelEventFound(channelEvent, channelId, eventId);
            ctx.getChannelEventsByChannelId().put(channelId, channelEvent);

            ChannelSession mainEventFirstSession = getMainEventFirstSession(channelId, ctx.getMainPackItemRecord());
            validateMainEventFirstChannelSessionFound(mainEventFirstSession, eventId);
            ctx.getChannelSessionOfMainEventByChannelId().put(channelId, mainEventFirstSession);

            List<CpanelCanalCurrencyRecord> channelCurrencies = ctx.getChannelCurrenciesByChannelId().get(channelId.intValue());
            validateChannelHasCurrencies(channelCurrencies);
            Integer mainEventCurrency = ctx.getMainEvent().getCurrency();
            validatePackCurrencyMatchChannelCurrency(channelCurrencies, mainEventCurrency);
        }
    }

    private static void validateMainChannelEventFound(ChannelEvent channelEvent, Long channelId, Long eventId) {
        if (channelEvent == null) {
            throw new PackContextLoaderException("channel : %s event : %s - No main channel event found".formatted(channelId, eventId), INACTIVE);
        }
    }

    private ChannelSession getMainEventFirstSession(Long channelId, CpanelPackItemRecord mainPackItemRecord) {
        ChannelCatalogSessionsFilter channelCatalogSessionsFilter = new ChannelCatalogSessionsFilter();
        long eventId = mainPackItemRecord.getIditem().longValue();
        channelCatalogSessionsFilter.setEventId(List.of(eventId));
        channelCatalogSessionsFilter.setVenueConfigId(List.of(mainPackItemRecord.getIdconfiguracion().longValue()));
        channelCatalogSessionsFilter.setType(List.of(SessionType.SESSION));
        channelCatalogSessionsFilter.setSoldOut(false);
        channelCatalogSessionsFilter.setForSale(true);
        channelCatalogSessionsFilter.setLimit(1L);
        channelCatalogSessionsFilter.setOffset(0L);

        ZonedDateTime now = es.onebox.core.utils.common.DateUtils.getZonedDateTimeForceUTC(new Date());
        channelCatalogSessionsFilter.setEndDate(Collections.singletonList(FilterWithOperator.build(Operator.GREATER_THAN_OR_EQUALS, now)));
        channelCatalogSessionsFilter.setEndSaleDate(Collections.singletonList(FilterWithOperator.build(Operator.GREATER_THAN_OR_EQUALS, now)));

        Page page = ChannelCatalogDefaultService.preparePage(channelCatalogSessionsFilter);
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelSessionsQuery(channelId, channelCatalogSessionsFilter);
        ElasticSearchResults<ChannelSessionData> result = channelSessionElasticDao.searchChannelSessions(channelSessionQuery, page);
        validateMainEventChannelSessionsFound(result, eventId);
        return result.getResults().get(0).getChannelSession();
    }

    private static void validateMainEventChannelSessionsFound(ElasticSearchResults<ChannelSessionData> result, long eventId) {
        if (CollectionUtils.isEmpty(result.getResults())) {
            throw new PackContextLoaderException("event : %s - No main event channel sessions found".formatted(eventId), INACTIVE);
        }
    }

    private static void validateMainEventFirstChannelSessionFound(ChannelSession mainEventFirstSession, Long eventId) {
        if (mainEventFirstSession == null) {
            throw new PackContextLoaderException("event : %s - No main event first channel session found".formatted(eventId), INACTIVE);
        }
    }

    private static void validateChannelHasCurrencies(List<CpanelCanalCurrencyRecord> channelCurrencies) {
        if (CollectionUtils.isEmpty(channelCurrencies)) {
            throw new PackContextLoaderException("No channel currencies", INACTIVE);
        }
    }

    private static void validatePackCurrencyMatchChannelCurrency(List<CpanelCanalCurrencyRecord> channelCurrencies, Integer mainEventCurrency) {
        if (channelCurrencies.stream().noneMatch(cc -> cc.getIdcurrency().equals(mainEventCurrency))) {
            throw new PackContextLoaderException("currency : %s - Pack currency don't match with channel currencies".formatted(mainEventCurrency), INACTIVE);
        }
    }

    private void fillChannelSessionContext(PackIndexationContext ctx, Long channelId) {
        if (CollectionUtils.isEmpty(ctx.getSessionPackItemRecords())) return;

        List<Long> sessionIds = PackUtils.getItemIds(ctx.getSessionPackItemRecords());
        List<ChannelSession> channelSessions = getChannelSessions(channelId, sessionIds);
        validateAllChannelSessionsWereFound(channelSessions, sessionIds);
        ctx.getChannelSessionListByChannelId().put(channelId, channelSessions);
        Map<Long, ChannelSession> channelSessionsBySessionId = channelSessions.stream().collect(Collectors.toMap(ChannelSession::getSessionId, Function.identity()));
        ctx.getChannelSessionsBySessionIdByChannelId().put(channelId, channelSessionsBySessionId);
        validateSessionCurrenciesByChannel(ctx, channelId);
        fillSessionItemsOccupation(ctx, channelId, channelSessions);
    }

    private List<ChannelSession> getChannelSessions(Long channelId, List<Long> sessionIds) {
        ChannelCatalogSessionsFilter filter = new ChannelCatalogSessionsFilter();
        filter.setSessionId(sessionIds);
        Page page = ChannelCatalogDefaultService.preparePage(filter);
        BoolQuery.Builder channelSessionQuery = ChannelCatalogESQueryAdapter.prepareChannelSessionsQuery(channelId, filter);
        ElasticSearchResults<ChannelSessionData> result = channelSessionElasticDao.searchChannelSessions(channelSessionQuery, page);
        validateChannelSessionsFound(result);
        return result.getResults().stream().map(ChannelSessionData::getChannelSession).toList();
    }

    private static void validateChannelSessionsFound(ElasticSearchResults<ChannelSessionData> result) {
        if (CollectionUtils.isEmpty(result.getResults())) {
            throw new PackContextLoaderException("No channel sessions found", INACTIVE);
        }
    }

    private static void validateAllChannelSessionsWereFound(List<ChannelSession> channelSessions, List<Long> sessionIds) {
        for (Long sessionId : sessionIds) {
            if (channelSessions.stream().noneMatch(channelSession -> channelSession.getSessionId().equals(sessionId))) {
                throw new PackContextLoaderException("session : %s - No channel session found".formatted(sessionId), INACTIVE);
            }
        }
    }

    private static void validateSessionCurrenciesByChannel(PackIndexationContext ctx, Long channelId) {
        ctx.getEventsBySessionId().forEach((sessionId, event) -> {
            List<CpanelCanalCurrencyRecord> channelCurrencies = ctx.getChannelCurrenciesByChannelId().get(channelId.intValue());
            if (channelCurrencies.stream().noneMatch(cc -> event.getCurrency().equals(cc.getIdcurrency()))) {
                throw new PackContextLoaderException("session : %s - Session doesn't match channel currency".formatted(sessionId), INACTIVE);
            }
        });
    }

    private void fillSessionItemsOccupation(PackIndexationContext ctx, Long channelId, List<ChannelSession> channelSessions) {
        if (CollectionUtils.isEmpty(ctx.getNoMainSessionPackItemRecordsWithPriceTypeConfig())) return;

        Map<Long, List<Integer>> sessionQuotaIdsMap = getSessionQuotaIdsMap(channelSessions, channelId);
        Map<EventType, List<Long>> sessionIdsByEventTypeMap =
                getSessionIdsByEventType(ctx, ctx.getNoMainSessionPackItemRecordsWithPriceTypeConfig());

        if (MapUtils.isEmpty(sessionIdsByEventTypeMap) || MapUtils.isEmpty(sessionQuotaIdsMap)) return;

        Map<EventType, List<SessionOccupationByPriceZoneDTO>> sessionOccupationByEventType = getOccupationMap(sessionIdsByEventTypeMap, sessionQuotaIdsMap);
        ctx.getSessionOccupationByEventTypeByChannelId().put(channelId, sessionOccupationByEventType);
    }

    private Map<Long, List<Integer>> getSessionQuotaIdsMap(List<ChannelSession> channelSessions, Long channelId) {
        if (CollectionUtils.isEmpty(channelSessions)) return null;

        Map<Integer, CpanelCanalEventoRecord> eventChannelByEventIdMap = getEvenchannelMap(channelSessions, channelId);
        if (MapUtils.isEmpty(eventChannelByEventIdMap)) return null;

        Map<Long, List<Integer>> sessionQuotaIdsMap = new HashMap<>();
        for (ChannelSession session : channelSessions) {
            Integer venueTemplateId = session.getVenueConfigId().intValue();
            CpanelCanalEventoRecord channelEvent = eventChannelByEventIdMap.get(session.getEventId().intValue());
            List<Integer> quotaIds;
            if (BooleanUtils.isTrue(CommonUtils.isTrue(channelEvent.getTodosgruposventa()))) {
                quotaIds = quotaConfigDao.getQuotasConfigByVenueTemplateId(venueTemplateId);
            } else {
                quotaIds = quotaAssignmentDao.getQuotaIdsByChannelEventIdAndVenueTemplateId(channelEvent.getIdcanaleevento(), venueTemplateId);
            }
            if (CollectionUtils.isNotEmpty(quotaIds)) {
                sessionQuotaIdsMap.put(session.getSessionId(), quotaIds);
            }
        }
        return sessionQuotaIdsMap;
    }

    private Map<Integer, CpanelCanalEventoRecord> getEvenchannelMap(List<ChannelSession> sessions, Long channelId) {
        if (CollectionUtils.isEmpty(sessions)) return null;

        List<Integer> eventIds = sessions.stream()
                .map(ChannelSession::getEventId)
                .filter(Objects::nonNull)
                .map(Long::intValue)
                .toList();

        if (eventIds.isEmpty()) return null;

        List<CpanelCanalEventoRecord> channelEvents =
                channelEventDao.findByChannelIdEventIds(channelId.intValue(), eventIds);

        if (CollectionUtils.isEmpty(channelEvents)) return null;

        return channelEvents.stream()
                .collect(Collectors.toMap(CpanelCanalEventoRecord::getIdevento, Function.identity()));
    }

    public static Map<EventType, List<Long>> getSessionIdsByEventType(PackIndexationContext ctx, List<CpanelPackItemRecord> sessionPackItemRecords) {
        if (CollectionUtils.isEmpty(sessionPackItemRecords) || MapUtils.isEmpty(ctx.getSessionsById())) {
            return null;
        }

        List<Long> sessionIds = PackUtils.getItemIds(sessionPackItemRecords);

        List<Session> sessions = ctx.getSessionsById().values().stream()
                .filter(session -> sessionIds.contains(session.getSessionId()))
                .toList();

        if (sessions.isEmpty()) return null;

        return sessions.stream().collect(Collectors.groupingBy(
                session -> getEventTypeByVenueTemplateType(session.getVenueTemplateType()),
                Collectors.mapping(Session::getSessionId, Collectors.toList())
        ));
    }

    private Map<EventType, List<SessionOccupationByPriceZoneDTO>> getOccupationMap(Map<EventType, List<Long>> sessionIdsByEventTypeMap,
                                                                                   Map<Long, List<Integer>> sessionQuotaIdsMap) {
        Map<EventType, List<SessionOccupationByPriceZoneDTO>> sessionOccupationByEventType = new HashMap<>();
        for (Map.Entry<EventType, List<Long>> entry : sessionIdsByEventTypeMap.entrySet()) {
            EventType eventType = entry.getKey();
            List<Long> sessionIds = entry.getValue();
            SessionOccupationsSearchRequest request = getSessionOccupationsSearchRequest(sessionQuotaIdsMap, eventType, sessionIds);

            List<SessionOccupationByPriceZoneDTO> occupation = sessionOccupationRepository.searchOccupationsByPriceZones(request);
            sessionOccupationByEventType.put(eventType, occupation);
        }
        return sessionOccupationByEventType;
    }

    private static SessionOccupationsSearchRequest getSessionOccupationsSearchRequest(Map<Long, List<Integer>> sessionQuotaIdsMap, EventType eventType, List<Long> sessionIds) {
        List<SessionWithQuotasDTO> sessionsRequest = new LinkedList<>();
        sessionIds.forEach(sessionId -> {
            SessionWithQuotasDTO sessionWithQuotasDTO = new SessionWithQuotasDTO();
            sessionWithQuotasDTO.setSessionId(sessionId);
            sessionWithQuotasDTO.setQuotas(sessionQuotaIdsMap.get(sessionId).stream().map(Integer::longValue).toList());
            sessionsRequest.add(sessionWithQuotasDTO);
        });

        SessionOccupationsSearchRequest request = new SessionOccupationsSearchRequest();
        request.setEventType(eventType);
        request.setSessions(sessionsRequest);
        return request;
    }

    private boolean getPackOnSale(PackIndexationContext ctx, Long channelId) {
        boolean onSaleCondition = true;
        if (BooleanUtils.isTrue(ctx.getPackWithEvent())) {
            onSaleCondition = ctx.isEventReady() && isChannelEventOnSale(ctx, channelId) && isChannelSessionOfMainEventOnSale(ctx, channelId);
        }
        if (onSaleCondition && BooleanUtils.isTrue(ctx.getPackWithSessions())) {
            onSaleCondition = ctx.areSessionEventsReady() && ctx.areSessionsReady() && areChannelSessionsOnSale(ctx, channelId);
        }
        if (onSaleCondition && BooleanUtils.isTrue(ctx.getPackWithProducts())) {
            onSaleCondition = areProductsOnSale(ctx, channelId);
        }
        return onSaleCondition;
    }

    private boolean areProductsOnSale(PackIndexationContext ctx, Long channelId) {
        boolean productsOnSale = ctx.getCatalogProductsById().values().stream().allMatch(catalogProduct -> {
            if (!ProductState.ACTIVE.equals(catalogProduct.getState())) return false;

            Long productId = catalogProduct.getId();
            ProductChannelRecord productChannelRecord = productChannelDao.findByProductIdAndChannelId(productId, channelId);
            if (productChannelRecord == null) {
                LOGGER.warn("{} pack : {} - Product {} has no relation with channel {}", PACK_CATALOG_REFRESH, ctx.getPackId(), productId, channelId);
                return false;
            }
            SaleRequestsStatus saleRequestsStatus = SaleRequestsStatus.get(productChannelRecord.getProductSaleRequestsStatusId());
            boolean productSaleIsAccepted = SaleRequestsStatus.ACCEPTED.equals(saleRequestsStatus);
            if (!productSaleIsAccepted) {
                LOGGER.warn("{} pack : {} channel : {} product : {} - Channel has not accepted product sale request", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, productId);
            }
            return productSaleIsAccepted;
        });
        if (!productsOnSale) {
            LOGGER.warn("{} pack {} channel {} - Products are not on sale", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId);
        }
        return productsOnSale;
    }

    private static boolean isChannelEventOnSale(PackIndexationContext ctx, Long channelId) {
        ChannelEvent channelEvent = ctx.getChannelEventsByChannelId().get(channelId);
        ChannelCatalogEventInfo eventCatalogInfo = channelEvent.getCatalogInfo();

        boolean isChannelEventOnSale = BooleanUtils.isTrue(eventCatalogInfo.getForSale())
                && eventCatalogInfo.getDate() != null
                && eventCatalogInfo.getDate().getSaleEnd() != null
                && ZonedDateTime.now().isBefore(CatalogUtils.toZonedDateTime(eventCatalogInfo.getDate().getSaleEnd()));
        if (!isChannelEventOnSale) {
            LOGGER.warn("{} pack : {} channel : {} event: {} - Channel event is not on sale", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, channelEvent.getEventId());
        }

        return isChannelEventOnSale;
    }

    private static boolean isChannelSessionOfMainEventOnSale(PackIndexationContext ctx, Long channelId) {
        boolean isChannelSessionOfMainEventOnSale = ctx.getChannelSessionOfMainEventByChannelId().get(channelId) != null;
        if (!isChannelSessionOfMainEventOnSale) {
            LOGGER.warn("{} pack : {} channel : {} event: {} - Channel event has no sessions on sale", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, ctx.getMainEvent().getEventId());
        }
        return isChannelSessionOfMainEventOnSale;
    }

    private static boolean areChannelSessionsOnSale(PackIndexationContext ctx, Long channelId) {
        return ctx.getChannelSessionListByChannelId().get(channelId).stream().allMatch(channelSession -> {
            ZonedDateTime sessionSaleStart = CatalogUtils.toZonedDateTime(channelSession.getDate().getSaleStart());
            ZonedDateTime sessionSaleEnd = CatalogUtils.toZonedDateTime(channelSession.getDate().getSaleEnd());

            boolean sessionOnSale = BooleanUtils.isTrue(channelSession.getForSale()) && dateIsBetween(ZonedDateTime.now(), sessionSaleStart, sessionSaleEnd);
            if (!sessionOnSale) {
                LOGGER.warn("{} pack : {} channel : {} session : {} - Channel session is not on sale", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, channelSession.getSessionId());
            }
            return sessionOnSale;
        });
    }

    public static boolean dateIsBetween(ZonedDateTime target, ZonedDateTime begin, ZonedDateTime end) {
        if (target == null || begin == null || end == null) return false;
        return target.isAfter(begin) && target.isBefore(end);
    }

    private static boolean getPackForSale(PackIndexationContext ctx, Long channelId) {
        boolean forSaleCondition = true;
        if (BooleanUtils.isTrue(ctx.getPackWithEvent())) {
            forSaleCondition = BooleanUtils.isTrue(ctx.getChannelEventsByChannelId().get(channelId).getCatalogInfo().getForSale());
        }
        if (forSaleCondition && BooleanUtils.isTrue(ctx.getPackWithSessions())) {
            forSaleCondition = ctx.getChannelSessionListByChannelId().get(channelId).stream()
                    .allMatch(channelSession -> BooleanUtils.isTrue(channelSession.getForSale()));
        }
        return forSaleCondition;
    }

    private boolean getPackSolOut(PackIndexationContext ctx, Long channelId) {
        if (BooleanUtils.isTrue(ctx.getPackWithEvent()) && mainEventIsSoldOut(ctx, channelId)) {
            LOGGER.warn("{} pack : {} channel : {} event : {} - Channel event is soldOut", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, ctx.getMainEvent().getEventId());
            return true;
        }
        if (BooleanUtils.isTrue(ctx.getPackWithSessions()) && anySessionIsSoldOut(ctx, channelId)) {
            return true;
        }
        if (BooleanUtils.isTrue(ctx.getPackWithProducts()) && productsAreSoldOut(ctx, channelId)) {
            LOGGER.warn("{} pack : {} channel : {} - Products are sold out", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId);
            return true;
        }
        return false;
    }

    private static boolean mainEventIsSoldOut(PackIndexationContext ctx, Long channelId) {
        return BooleanUtils.isTrue(ctx.getChannelEventsByChannelId().get(channelId).getCatalogInfo().getSoldOut());
    }

    private static boolean anySessionIsSoldOut(PackIndexationContext ctx, Long channelId) {
        return ctx.getChannelSessionListByChannelId().get(channelId).stream()
                .anyMatch(channelSession -> channelSessionIsSoldOut(ctx, channelId, channelSession));
    }

    private static boolean channelSessionIsSoldOut(PackIndexationContext ctx, Long channelId, ChannelSession channelSession) {
        boolean soldOut = BooleanUtils.isTrue(channelSession.getSoldOut());
        Long sessionId = channelSession.getSessionId();

        if (!soldOut && isNotMainSession(ctx, sessionId)) {
            soldOut = sessionSoldOutForPriceType(ctx, channelId, sessionId);
        }

        if (soldOut) {
            LOGGER.warn("{} pack : {} channel : {} session : {} - Channel session is sold out",
                    PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId);
        }

        return soldOut;
    }

    private static boolean isNotMainSession(PackIndexationContext ctx, Long sessionId) {
        return ctx.getMainSession() == null || !ctx.getMainSession().getSessionId().equals(sessionId);
    }

    public static boolean sessionSoldOutForPriceType(PackIndexationContext ctx, Long channelId, Long sessionId) {
        CpanelPackItemRecord sessionPackItemRecord =
                ctx.getNoMainSessionPackItemRecordsWithPriceTypeConfig().stream()
                        .filter(item -> item.getIditem().equals(sessionId.intValue()))
                        .findAny()
                        .orElse(null);

        if (sessionPackItemRecord == null || sessionPackItemRecord.getIdzonaprecio() == null) {
            return false;
        }

        Session session = ctx.getSessionsById().get(sessionId);
        EventType eventType = getEventTypeByVenueTemplateType(session.getVenueTemplateType());
        List<SessionOccupationByPriceZoneDTO> occupations = ctx.getSessionOccupationByEventTypeByChannelId().get(channelId).get(eventType);

        if (CollectionUtils.isEmpty(occupations)) {
            LOGGER.error("{} pack : {} channel : {} - Session {} has empty occupations", PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId);
            return true;
        }

        SessionOccupationByPriceZoneDTO sessionOccupation = occupations.stream()
                .filter(item -> item.getSession().getSessionId().equals(sessionId))
                .findAny()
                .orElse(null);

        if (sessionOccupation == null || CollectionUtils.isEmpty(sessionOccupation.getOccupation())) {
            LOGGER.error("{} pack : {} channel : {} - Session {} has empty session occupation",
                    PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId);
            return true;
        }

        Long priceTypeId = sessionPackItemRecord.getIdzonaprecio().longValue();
        SessionPriceZoneOccupationDTO priceTypeOccupation = sessionOccupation.getOccupation()
                .stream()
                .filter(item -> item.getPriceZoneId().equals(priceTypeId))
                .findAny()
                .orElse(null);

        if (priceTypeOccupation == null) {
            LOGGER.error("{} (pack : {} channelId : {} - Session {} with price-type {} has null price type occupation",
                    PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId, priceTypeId);
            return true;
        }

        if (BooleanUtils.isTrue(priceTypeOccupation.getUnlimited())) {
            return false;
        }

        if (MapUtils.isEmpty(priceTypeOccupation.getStatus())) {
            LOGGER.error("{} pack : {} channel : {} - Session {} with price-type {} has invalid occupation status",
                    PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId, priceTypeId);
            return true;
        }

        Long available = priceTypeOccupation.getStatus().get(TicketStatus.AVAILABLE);

        if (available == null) {
            LOGGER.error("{} pack : {} channel : {} - Session {} with price-type {} has null available occupation",
                    PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId, priceTypeId);
            return true;
        }

        return available == 0;
    }

    public static EventType getEventTypeByVenueTemplateType(Integer venueTemplateType) {
        return VenueTemplateType.DEFAULT.getId().equals(venueTemplateType) || VenueTemplateType.AVET.getId().equals(venueTemplateType) ?
                EventType.NORMAL : EventType.ACTIVITY;
    }

    private boolean productsAreSoldOut(PackIndexationContext ctx, Long channelId) {
        return BooleanUtils.isTrue(ctx.getPackWithProducts()) && !productsHaveStock(ctx, channelId);
    }

    private boolean productsHaveStock(PackIndexationContext ctx, Long channelId) {
        return ctx.getProductPackItemRecords().stream().allMatch(record -> {
            Long productId = record.getIditem().longValue();
            Long variantId = record.getIdvariante().longValue();
            ProductCatalogDocument catalogProduct = ctx.getCatalogProductsById().get(productId);
            return switch (catalogProduct.getStockType()) {
                case UNBOUNDED -> true;
                case BOUNDED -> productVariantStockCouchDao.get(productId, variantId) > 0;
                case SESSION_BOUNDED -> productHasSessionStock(ctx, productId, variantId, channelId);
            };
        });
    }

    private boolean productHasSessionStock(PackIndexationContext ctx, Long productId, Long variantId, Long channelId) {
        if (ctx.getMainSession() != null) {
            Long sessionId = ctx.getMainSession().getSessionId();
            Long stock = productVariantSessionStockCouchDao.get(productId, variantId, sessionId);
            return stock != null && stock > 0;
        }
        if (ctx.getMainEvent() != null) {
            List<ChannelSession> eventChannelSessions = getEventChannelSessions(ctx, channelId);
            if (CollectionUtils.isEmpty(eventChannelSessions)) {
                return false;
            }
            return eventChannelSessions.stream().anyMatch(channelSession -> {
                Long sessionId = channelSession.getSessionId();
                Long stock = productVariantSessionStockCouchDao.get(productId, variantId, sessionId);
                boolean hasStock = stock != null && stock > 0;
                if (!hasStock) {
                    LOGGER.warn("{} pack : {} channel : {} - Session {} doesn't have stock for product {}",
                            PACK_CATALOG_REFRESH, ctx.getPackId(), channelId, sessionId, productId);
                }
                return hasStock;
            });
        }
        return false;
    }

    private List<ChannelSession> getEventChannelSessions(PackIndexationContext ctx, Long channelId) {
        ChannelCatalogSessionsFilter channelCatalogSessionsFilter = new ChannelCatalogSessionsFilter();
        long eventId = ctx.getMainEvent().getEventId();
        channelCatalogSessionsFilter.setEventId(List.of(eventId));
        channelCatalogSessionsFilter.setVenueConfigId(List.of(ctx.getMainPackItemRecord().getIdconfiguracion().longValue()));
        channelCatalogSessionsFilter.setType(List.of(SessionType.SESSION));
        channelCatalogSessionsFilter.setSoldOut(false);
        channelCatalogSessionsFilter.setForSale(true);
        channelCatalogSessionsFilter.setLimit(100L);

        long offset = 0;
        long total;
        List<ChannelSessionData> channelSessionData = new ArrayList<>();

        do {
            channelCatalogSessionsFilter.setOffset(offset);
            Page page = ChannelCatalogDefaultService.preparePage(channelCatalogSessionsFilter);
            BoolQuery.Builder query = ChannelCatalogESQueryAdapter.prepareChannelSessionsQuery(channelId, channelCatalogSessionsFilter);
            ElasticSearchResults<ChannelSessionData> result = channelSessionElasticDao.searchChannelSessions(query, page);

            if (result == null || CollectionUtils.isEmpty(result.getResults())) break;

            total = result.getMetadata().getTotal();
            channelSessionData.addAll(result.getResults());
            offset += ChannelCatalogSessionsFilter.DEFAULT_MAX_LIMIT;
        } while (offset < total);

        return channelSessionData.stream().map(ChannelSessionData::getChannelSession).toList();
    }

    private Map<Integer, Set<Integer>> getPriceTypesByPackItemId(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream()
                .filter(PackUtils::isEventOrSession)
                .collect(Collectors.toMap(CpanelPackItemRecord::getIdpackitem, this::getPriceTypes));
    }

    private Set<Integer> getPriceTypes(CpanelPackItemRecord packItemRecord) {
        List<CpanelPackItemZonaPrecioRecord> priceTypes = packItemsPriceTypeDao.getPackItemPriceTypesById(packItemRecord.getIdpackitem());
        if (CollectionUtils.isEmpty(priceTypes)) {
            return Collections.emptySet();
        }
        return priceTypes.stream()
                .map(CpanelPackItemZonaPrecioRecord::getIdzonaprecio)
                .collect(Collectors.toSet());
    }

    private void fillChannelPackPricesSimulation(PackIndexationContext ctx, Long channelId) {
        PackTaxes packTaxes = getPackTaxes(ctx);
        List<EventPromotionRecord> eventPromotionRecords =
                eventPromotionTemplateDao.getPackApplicablePromotionsByEventId(getMainEventId(ctx).intValue());
        ChannelEventSurcharges channelEventSurcharges = getChannelEventSurcharges(channelId);
        PackVenueConfigPricesSimulation packPricesSimulationForCatalog =
                priceEngineSimulationService.getPackPricesSimulationForCatalog(
                        channelId, ctx.getPackVenueConfigPricesBase(), channelEventSurcharges, eventPromotionRecords, packTaxes);
        ctx.getPackVenueConfigMapSimulationByChannelId().put(channelId, packPricesSimulationForCatalog);
    }

    private static Long getMainEventId(PackIndexationContext ctx) {
        return switch (ctx.getMainPackItemType()) {
            case SESSION -> ctx.getMainSession().getEventId();
            case EVENT -> ctx.getMainEvent().getEventId();
            case PRODUCT -> throw new UnsupportedOperationException("Product type not supported");
        };
    }

    private static PackTaxes getPackTaxes(PackIndexationContext ctx) {
        PackTaxes packTaxes = new PackTaxes();
        List<CpanelImpuestoRecord> packTaxList = List.of(ctx.getPackTax());
        packTaxes.setPriceTaxes(getTaxes(packTaxList));
        return packTaxes;
    }

    private static List<TaxInfo> getTaxes(List<CpanelImpuestoRecord> packTaxList) {
        return packTaxList.stream().map(e -> {
            TaxInfo taxInfo = new PackTaxInfo();
            taxInfo.setValue(e.getValor());
            taxInfo.setName(e.getNombre());
            taxInfo.setId(e.getIdimpuesto().longValue());
            taxInfo.setDescription(e.getDescripcion());
            return taxInfo;
        }).collect(Collectors.toList());
    }

    public ChannelEventSurcharges getChannelEventSurcharges(Long channelId) {
        List<CpanelRangoRecord> channelMainSurcharges = catalogSurchargeService.getChannelMainSurcharges(channelId.intValue());

        return ChannelEventSurchargesBuilder.builder()
                .channelMainSurcharges(channelMainSurcharges)
                .build();
    }


    private void fillChannelPackPriceMatrix(PackIndexationContext ctx, Long channelId) {
        PriceMatrix priceMatrix;
        if (ctx.isFullUpsert()) {
            priceMatrix = calculateChannelPackPriceMatrix(ctx, channelId);
        } else {
            ChannelPack channelPack = ctx.getChannelPacksByChannelId().get(channelId);
            priceMatrix = channelPack.getPrices();
        }

        ctx.getPriceMatrixByChannelId().put(channelId, priceMatrix);
    }

    private PriceMatrix calculateChannelPackPriceMatrix(PackIndexationContext ctx, Long channelId) {
        PriceMatrix priceMatrix = new PriceMatrix();

        PackVenueConfigPricesSimulation simulation = ctx.getPackVenueConfigMapSimulationByChannelId().get(channelId);
        List<PackPriceType> packDefaultPriceTypes = simulation.getRates().stream()
                .filter(PackRate::isDefaultRate)
                .findFirst()
                .map(PackRate::getPriceTypes)
                .orElseThrow(() -> new PackContextLoaderException("Pack %s in channel %s has invalid default prices".formatted(ctx.getPackId(), channelId), PackStatus.INACTIVE));

        Double minBasePriceValue = packDefaultPriceTypes.stream()
                .map(PackPriceType::getPrice)
                .map(PackPrice::getTotal)
                .min(Double::compare)
                .orElseThrow(() -> new PackContextLoaderException("Pack %s in channel %s has invalid min prices".formatted(ctx.getPackId(), channelId), PackStatus.INACTIVE));

        Price minBasePrice = new Price();
        minBasePrice.setValue(minBasePriceValue);

        priceMatrix.setMinBasePrice(minBasePrice);
        priceMatrix.setMinFinalPrice(minBasePrice);

        Double minNetPriceValue = packDefaultPriceTypes.stream()
                .map(PackPriceType::getSimulations)
                .flatMap(List::stream)
                .map(PriceSimulation::getPrice)
                .map(es.onebox.event.priceengine.simulation.domain.Price::getNet)
                .min(Double::compare)
                .orElseThrow(() -> new PackContextLoaderException("Pack %s in channel %s has invalid min net prices".formatted(ctx.getPackId(), channelId), PackStatus.INACTIVE));

        Price minNetPrice = new Price();
        minNetPrice.setValue(minNetPriceValue);

        priceMatrix.setMinNetPrice(minNetPrice);

        return priceMatrix;
    }

}
