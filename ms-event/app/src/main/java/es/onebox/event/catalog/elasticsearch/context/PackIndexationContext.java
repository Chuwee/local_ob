package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.packs.dao.domain.PackRateRecord;
import es.onebox.event.packs.dao.domain.ItemPackPriceInfoRecord;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesBase;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalCurrencyRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalSolicitudVentaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class PackIndexationContext {

    private List<Long> channelIds;
    private final Long packId;
    private final boolean isFullUpsert;

    // Pack
    private PackStatus packStatus;
    private PackDetailRecord packDetailRecord;
    private Integer packCurrency;
    private List<CpanelElementosComPackRecord> communicationElements;

    // Pack items
    private List<CpanelPackItemRecord> packItemRecords;
    private CpanelPackItemRecord mainPackItemRecord;
    private PackItemType mainPackItemType;
    private CpanelConfigRecintoRecord mainVenueConfig;

    // Pack Channels
    Map<Long, CpanelPackCanalRecord> packChannelRecordsByChannelId;

    // Price types
    private Map<Integer, Set<Integer>> priceTypesByPackItemId;
    private Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> priceTypesMappingByPackItemId;

    // Price simulation
    private PackPricingType pricingType;
    private Map<Integer, Double> priceByProductPackItemId;
    private Map<Integer, List<ItemPackPriceInfoRecord>> itemPackPriceInfoRecordsByPackItemId;
    private List<PackRateRecord> packRates;
    private Map<Integer, List<PriceRecord>> mainPackItemPriceRecordsByRateId;
    private PackVenueConfigPricesBase packVenueConfigPricesBase;
    private CpanelImpuestoRecord packTax;
    private final Map<Long, PackVenueConfigPricesSimulation> packVenueConfigMapSimulationByChannelId = new HashMap<>();

    //Sale Request
    private Map<Integer, CpanelPackCanalSolicitudVentaRecord> saleRequestByChannelId;

    // S3
    private String s3Repository;
    private IntFunction<String> getTag;
    private IntFunction<String> getLanguageCode;

    // Event
    private Boolean isPackWithEvent;
    private Event mainEvent;
    private final Map<Long, ChannelEvent> channelEventsByChannelId = new HashMap<>();
    private final Map<Long, ChannelSession> channelSessionOfMainEventByChannelId = new HashMap<>();
    private Integer mainEventVenueId;
    private List<Long> sessionsFilter;
    private Boolean isEventReady;

    // Session
    private Boolean isPackWithSessions;
    private Session mainSession;
    private List<CpanelPackItemRecord> sessionPackItemRecords;
    private List<CpanelPackItemRecord> noMainSessionPackItemRecordsWithPriceTypeConfig;
    private final Map<Long, List<ChannelSession>> channelSessionListByChannelId = new HashMap<>();
    private Map<Long, Session> sessionsById;
    private Map<Long, Event> eventsBySessionId;
    private final Map<Long, Map<Long, ChannelSession>> channelSessionsBySessionIdByChannelId = new HashMap<>();
    private final Map<Long, Map<EventType, List<SessionOccupationByPriceZoneDTO>>> sessionOccupationByEventTypeByChannelId = new HashMap<>();
    private Boolean areSessionEventsReady;
    private Boolean areSessionsReady;

    // Product
    private Boolean isPackWithProducts;
    private List<CpanelPackItemRecord> productPackItemRecords;
    private Map<Long, ProductCatalogDocument> catalogProductsById;

    // Sale conditions
    private Map<Long, Boolean> packSoldOutByChannelId = new HashMap<>();
    private Map<Long, Boolean> packForSaleByChannelId = new HashMap<>();
    private Map<Long, Boolean> packOnSaleByChannelId = new HashMap<>();

    // CommElement functions
    private Consumer<List<Integer>> deletePackComElementByIdsMethod;

    // Channel Currency
    private Map<Integer, List<CpanelCanalCurrencyRecord>> channelCurrenciesByChannelId;

    // Channel context exceptions
    private final Map<Long, Exception> channelContextExceptionsByChannelId = new HashMap<>();

    // Channel packs
    private Map<Long, ChannelPack> channelPacksByChannelId;

    // Channel pack price matrix
    private final Map<Long, PriceMatrix> priceMatrixByChannelId = new HashMap<>();

    public PackIndexationContext(Long packId, boolean isFullUpsert) {
        this.packId = packId;
        this.isFullUpsert = isFullUpsert;
    }

    public PackIndexationContext(Long packId) {
        this.packId = packId;
        this.isFullUpsert = true;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Long getPackId() {
        return packId;
    }

    public boolean isFullUpsert() {
        return isFullUpsert;
    }

    public PackStatus getPackStatus() {
        return packStatus;
    }

    public void setPackStatus(PackStatus packStatus) {
        this.packStatus = packStatus;
    }

    public PackDetailRecord getPackDetailRecord() {
        return packDetailRecord;
    }

    public void setPackDetailRecord(PackDetailRecord packDetailRecord) {
        this.packDetailRecord = packDetailRecord;
    }

    public List<CpanelPackItemRecord> getPackItemRecords() {
        return packItemRecords;
    }

    public void setPackItemRecords(List<CpanelPackItemRecord> packItemRecords) {
        this.packItemRecords = packItemRecords;
    }

    public CpanelPackItemRecord getMainPackItemRecord() {
        return mainPackItemRecord;
    }

    public void setMainPackItemRecord(CpanelPackItemRecord mainPackItemRecord) {
        this.mainPackItemRecord = mainPackItemRecord;
    }

    public PackItemType getMainPackItemType() {
        return mainPackItemType;
    }

    public void setMainPackItemType(PackItemType mainPackItemType) {
        this.mainPackItemType = mainPackItemType;
    }

    public CpanelConfigRecintoRecord getMainVenueConfig() {
        return mainVenueConfig;
    }

    public void setMainVenueConfig(CpanelConfigRecintoRecord mainVenueConfig) {
        this.mainVenueConfig = mainVenueConfig;
    }

    public Map<Long, CpanelPackCanalRecord> getPackChannelRecordsByChannelId() {
        return packChannelRecordsByChannelId;
    }

    public void setPackChannelRecordsByChannelId(Map<Long, CpanelPackCanalRecord> packChannelRecordsByChannelId) {
        this.packChannelRecordsByChannelId = packChannelRecordsByChannelId;
    }

    public Integer getPackCurrency() {
        return packCurrency;
    }

    public void setPackCurrency(Integer packCurrency) {
        this.packCurrency = packCurrency;
    }

    public Map<Integer, Set<Integer>> getPriceTypesByPackItemId() {
        return priceTypesByPackItemId;
    }

    public void setPriceTypesByPackItemId(Map<Integer, Set<Integer>> priceTypesByPackItemId) {
        this.priceTypesByPackItemId = priceTypesByPackItemId;
    }

    public Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> getPriceTypesMappingByPackItemId() {
        return priceTypesMappingByPackItemId;
    }

    public void setPriceTypesMappingByPackItemId(Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> priceTypesMappingByPackItemId) {
        this.priceTypesMappingByPackItemId = priceTypesMappingByPackItemId;
    }

    public List<CpanelElementosComPackRecord> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CpanelElementosComPackRecord> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public Map<Integer, Double> getPriceByProductPackItemId() {
        return priceByProductPackItemId;
    }

    public void setPriceByProductPackItemId(Map<Integer, Double> priceByProductPackItemId) {
        this.priceByProductPackItemId = priceByProductPackItemId;
    }

    public Map<Integer, List<ItemPackPriceInfoRecord>> getItemPackPriceInfoRecordsByPackItemId() {
        return itemPackPriceInfoRecordsByPackItemId;
    }

    public void setItemPackPriceInfoRecordsByPackItemId(Map<Integer, List<ItemPackPriceInfoRecord>> itemPackPriceInfoRecordsByPackItemId) {
        this.itemPackPriceInfoRecordsByPackItemId = itemPackPriceInfoRecordsByPackItemId;
    }

    public List<PackRateRecord> getPackRates() {
        return packRates;
    }

    public void setPackRates(List<PackRateRecord> packRates) {
        this.packRates = packRates;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public Map<Integer, List<PriceRecord>> getMainPackItemPriceRecordsByRateId() {
        return mainPackItemPriceRecordsByRateId;
    }

    public void setMainPackItemPriceRecordsByRateId(Map<Integer, List<PriceRecord>> mainPackItemPriceRecordsByRateId) {
        this.mainPackItemPriceRecordsByRateId = mainPackItemPriceRecordsByRateId;
    }

    public PackVenueConfigPricesBase getPackVenueConfigPricesBase() {
        return packVenueConfigPricesBase;
    }

    public void setPackVenueConfigPricesBase(PackVenueConfigPricesBase packVenueConfigPricesBase) {
        this.packVenueConfigPricesBase = packVenueConfigPricesBase;
    }

    public CpanelImpuestoRecord getPackTax() {
        return packTax;
    }

    public void setPackTax(CpanelImpuestoRecord packTax) {
        this.packTax = packTax;
    }

    public Map<Long, PackVenueConfigPricesSimulation> getPackVenueConfigMapSimulationByChannelId() {
        return packVenueConfigMapSimulationByChannelId;
    }

    public Map<Integer, CpanelPackCanalSolicitudVentaRecord> getSaleRequestByChannelId() {
        return saleRequestByChannelId;
    }

    public void setSaleRequestByChannelId(Map<Integer, CpanelPackCanalSolicitudVentaRecord> saleRequestByChannelId) {
        this.saleRequestByChannelId = saleRequestByChannelId;
    }

    public String getS3Repository() {
        return s3Repository;
    }

    public void setS3Repository(String s3Repository) {
        this.s3Repository = s3Repository;
    }

    public IntFunction<String> getGetTag() {
        return getTag;
    }

    public void setGetTag(IntFunction<String> getTag) {
        this.getTag = getTag;
    }

    public IntFunction<String> getGetLanguageCode() {
        return getLanguageCode;
    }

    public void setGetLanguageCode(IntFunction<String> getLanguageCode) {
        this.getLanguageCode = getLanguageCode;
    }

    public Boolean getPackWithEvent() {
        return isPackWithEvent;
    }

    public void setPackWithEvent(Boolean packWithEvent) {
        isPackWithEvent = packWithEvent;
    }

    public Event getMainEvent() {
        return mainEvent;
    }

    public void setMainEvent(Event mainEvent) {
        this.mainEvent = mainEvent;
    }

    public Map<Long, ChannelEvent> getChannelEventsByChannelId() {
        return channelEventsByChannelId;
    }

    public Map<Long, ChannelSession> getChannelSessionOfMainEventByChannelId() {
        return channelSessionOfMainEventByChannelId;
    }

    public Integer getMainEventVenueId() {
        return mainEventVenueId;
    }

    public void setMainEventVenueId(Integer mainEventVenueId) {
        this.mainEventVenueId = mainEventVenueId;
    }

    public List<Long> getSessionsFilter() {
        return sessionsFilter;
    }

    public void setSessionsFilter(List<Long> sessionsFilter) {
        this.sessionsFilter = sessionsFilter;
    }

    public Boolean isEventReady() {
        return isEventReady;
    }

    public void setEventReady(Boolean eventReady) {
        isEventReady = eventReady;
    }

    public Boolean getPackWithSessions() {
        return isPackWithSessions;
    }

    public void setPackWithSessions(Boolean packWithSessions) {
        isPackWithSessions = packWithSessions;
    }

    public Session getMainSession() {
        return mainSession;
    }

    public void setMainSession(Session mainSession) {
        this.mainSession = mainSession;
    }

    public List<CpanelPackItemRecord> getSessionPackItemRecords() {
        return sessionPackItemRecords;
    }

    public void setSessionPackItemRecords(List<CpanelPackItemRecord> sessionPackItemRecords) {
        this.sessionPackItemRecords = sessionPackItemRecords;
    }

    public List<CpanelPackItemRecord> getNoMainSessionPackItemRecordsWithPriceTypeConfig() {
        return noMainSessionPackItemRecordsWithPriceTypeConfig;
    }

    public void setNoMainSessionPackItemRecordsWithPriceTypeConfig(List<CpanelPackItemRecord> noMainSessionPackItemRecordsWithPriceTypeConfig) {
        this.noMainSessionPackItemRecordsWithPriceTypeConfig = noMainSessionPackItemRecordsWithPriceTypeConfig;
    }

    public Map<Long, List<ChannelSession>> getChannelSessionListByChannelId() {
        return channelSessionListByChannelId;
    }

    public Map<Long, Session> getSessionsById() {
        return sessionsById;
    }

    public void setSessionsById(Map<Long, Session> sessionsById) {
        this.sessionsById = sessionsById;
    }

    public Map<Long, Event> getEventsBySessionId() {
        return eventsBySessionId;
    }

    public void setEventsBySessionId(Map<Long, Event> eventsBySessionId) {
        this.eventsBySessionId = eventsBySessionId;
    }

    public Map<Long, Map<Long, ChannelSession>> getChannelSessionsBySessionIdByChannelId() {
        return channelSessionsBySessionIdByChannelId;
    }

    public Map<Long, Map<EventType, List<SessionOccupationByPriceZoneDTO>>> getSessionOccupationByEventTypeByChannelId() {
        return sessionOccupationByEventTypeByChannelId;
    }

    public Boolean areSessionEventsReady() {
        return areSessionEventsReady;
    }

    public void setAreSessionEventsReady(Boolean areSessionEventsReady) {
        this.areSessionEventsReady = areSessionEventsReady;
    }

    public Boolean areSessionsReady() {
        return areSessionsReady;
    }

    public void setAreSessionsReady(Boolean areSessionsReady) {
        this.areSessionsReady = areSessionsReady;
    }

    public Boolean getPackWithProducts() {
        return isPackWithProducts;
    }

    public void setPackWithProducts(Boolean packWithProducts) {
        isPackWithProducts = packWithProducts;
    }

    public List<CpanelPackItemRecord> getProductPackItemRecords() {
        return productPackItemRecords;
    }

    public void setProductPackItemRecords(List<CpanelPackItemRecord> productPackItemRecords) {
        this.productPackItemRecords = productPackItemRecords;
    }

    public Map<Long, ProductCatalogDocument> getCatalogProductsById() {
        return catalogProductsById;
    }

    public void setCatalogProductsById(Map<Long, ProductCatalogDocument> catalogProductsById) {
        this.catalogProductsById = catalogProductsById;
    }

    public Map<Long, Boolean> getPackSoldOutByChannelId() {
        return packSoldOutByChannelId;
    }

    public void setPackSoldOutByChannelId(Map<Long, Boolean> packSoldOutByChannelId) {
        this.packSoldOutByChannelId = packSoldOutByChannelId;
    }

    public Map<Long, Boolean> getPackForSaleByChannelId() {
        return packForSaleByChannelId;
    }

    public void setPackForSaleByChannelId(Map<Long, Boolean> packForSaleByChannelId) {
        this.packForSaleByChannelId = packForSaleByChannelId;
    }

    public Map<Long, Boolean> getPackOnSaleByChannelId() {
        return packOnSaleByChannelId;
    }

    public void setPackOnSaleByChannelId(Map<Long, Boolean> packOnSaleByChannelId) {
        this.packOnSaleByChannelId = packOnSaleByChannelId;
    }

    public Consumer<List<Integer>> getDeletePackComElementByIdsMethod() {
        return deletePackComElementByIdsMethod;
    }

    public void setDeletePackComElementByIdsMethod(Consumer<List<Integer>> deletePackComElementByIdsMethod) {
        this.deletePackComElementByIdsMethod = deletePackComElementByIdsMethod;
    }

    public Map<Integer, List<CpanelCanalCurrencyRecord>> getChannelCurrenciesByChannelId() {
        return channelCurrenciesByChannelId;
    }

    public void setChannelCurrenciesByChannelId(Map<Integer, List<CpanelCanalCurrencyRecord>> channelCurrenciesByChannelId) {
        this.channelCurrenciesByChannelId = channelCurrenciesByChannelId;
    }

    public Map<Long, Exception> getChannelContextExceptionsByChannelId() {
        return channelContextExceptionsByChannelId;
    }

    public Map<Long, ChannelPack> getChannelPacksByChannelId() {
        return channelPacksByChannelId;
    }

    public void setChannelPacksByChannelId(Map<Long, ChannelPack> channelPacksByChannelId) {
        this.channelPacksByChannelId = channelPacksByChannelId;
    }

    public Map<Long, PriceMatrix> getPriceMatrixByChannelId() {
        return priceMatrixByChannelId;
    }

}