package es.onebox.event.packs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dao.venue.VenueConfigurationDao;
import es.onebox.event.catalog.dto.VenueTemplateType;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelEventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.QuotaAssignmentDao;
import es.onebox.event.events.dao.QuotaConfigDao;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.catalog.elasticsearch.service.PackIndexationService;
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
import es.onebox.event.packs.dao.domain.ItemPackPriceInfoRecord;
import es.onebox.event.packs.dao.domain.PackRateRecord;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.priceengine.packs.PackRateBase;
import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.products.dao.ProductCatalogCouchDao;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantSessionStockCouchDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalCurrencyRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalSolicitudVentaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.onebox.event.packs.enums.PackItemType.*;
import static es.onebox.event.packs.enums.PackPricingType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PackIndexationServiceTest {

    @Mock
    private PackDao packDao;
    @Mock
    private PackItemsDao packItemsDao;
    @Mock
    private PackItemSubsetDao packItemSubsetDao;
    @Mock
    private PackChannelDao packChannelDao;
    @Mock
    private PackChannelSaleRequestDao packChannelSaleRequestDao;
    @Mock
    private PackCommunicationElementDao packCommunicationElementDao;
    @Mock
    private PackItemsPriceTypeDao packItemsPriceTypeDao;
    @Mock
    private PackPriceTypeMappingDao packPriceTypeMappingDao;
    @Mock
    private CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    @Mock
    private ChannelEventDao channelEventDao;
    @Mock
    private ChannelCurrenciesDao channelCurrenciesDao;
    @Mock
    private CatalogEventCouchDao catalogEventCouchDao;
    @Mock
    private QuotaConfigDao quotaConfigDao;
    @Mock
    private QuotaAssignmentDao quotaAssignmentDao;
    @Mock
    private CatalogSessionCouchDao catalogSessionCouchDao;
    @Mock
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Mock
    private VenueConfigurationDao venueConfigurationDao;
    @Mock
    private ProductCatalogCouchDao productCatalogCouchDao;
    @Mock
    private ProductEventDao productEventDao;
    @Mock
    private ProductChannelDao productChannelDao;
    @Mock
    private ProductVariantStockCouchDao productVariantStockCouchDao;
    @Mock
    private ProductVariantSessionStockCouchDao productVariantSessionStockCouchDao;
    @Mock
    private SessionOccupationRepository sessionOccupationRepository;
    @Mock
    private StaticDataContainer staticDataContainer;
    @Mock
    private ProductVariantDao productVariantDao;
    @Mock
    private PackRateDao packRateDao;
    @Mock
    private PriceZoneAssignmentDao priceZoneAssignmentDao;
    @Mock
    private TaxDao taxDao;
    @Mock
    private PriceEngineSimulationService priceEngineSimulationService;
    @Mock
    private CatalogSurchargeService catalogSurchargeService;
    @Mock
    private EventPromotionTemplateDao eventPromotionTemplateDao;

    private PackIndexationService packIndexationService;

    @BeforeEach
    public void setUpGetSessionFilterTests() {
        MockitoAnnotations.openMocks(this);
        packIndexationService = new PackIndexationService(
                packDao, packItemsDao, packItemSubsetDao, packChannelDao, packChannelSaleRequestDao,
                packCommunicationElementDao, packItemsPriceTypeDao, packPriceTypeMappingDao, null,
                channelEventDao, channelCurrenciesDao, catalogEventCouchDao, quotaConfigDao,
                quotaAssignmentDao, catalogSessionCouchDao, catalogChannelEventCouchDao,
                channelSessionElasticDao, venueConfigurationDao, productCatalogCouchDao,
                productEventDao, productChannelDao, productVariantStockCouchDao,
                productVariantSessionStockCouchDao, sessionOccupationRepository, staticDataContainer,
                productVariantDao, packRateDao, priceZoneAssignmentDao, taxDao,
                priceEngineSimulationService, catalogSurchargeService, eventPromotionTemplateDao
        );
    }


    // Tests of PackIndexationService.sessionSoldOutForPriceType(ctx, channelId, sessionId);

    private final static Long SESSION_1 = 1L;
    private final static Long SESSION_2 = 2L;
    private final static Long PRICE_TYPE = 3L;
    private final static Long PACK_ID = 4L;
    private final static Long CHANNEL_ID = 5L;

    @Test
    public void sessionSoldOutForPriceType_availableOk_Test() {
        sessionSoldOutForPriceType(1L, false, false);
    }

    @Test
    public void sessionSoldOutForPriceType_notAvailable_Test() {
        sessionSoldOutForPriceType(0L, false, true);
    }

    @Test
    public void sessionSoldOutForPriceType_unlimitedOk_Test() {
        sessionSoldOutForPriceType(0L, true, false);
    }

    private void sessionSoldOutForPriceType(Long availableOccupation, boolean unlimitedOccupation, boolean expectedSoldOut) {
        PackIndexationContext ctx = buildPackContext(availableOccupation, unlimitedOccupation);

        boolean soldoOut = PackIndexationService.sessionSoldOutForPriceType(ctx, CHANNEL_ID, SESSION_2);
        Assertions.assertEquals(soldoOut, expectedSoldOut);
    }

    // Utilities

    private static PackIndexationContext buildPackContext(Long availableOccupation, boolean unlimitedOccupation) {
        Session relatedSession = new Session();
        relatedSession.setSessionId(SESSION_2);
        relatedSession.setVenueTemplateType(VenueTemplateType.ACTIVITY.getId());

        CpanelPackItemRecord mainItem = new CpanelPackItemRecord();
        mainItem.setPrincipal(true);
        mainItem.setTipoitem(SESSION.getId());
        mainItem.setIditem(SESSION_1.intValue());

        CpanelPackItemRecord relatedItem = new CpanelPackItemRecord();
        relatedItem.setPrincipal(false);
        relatedItem.setIditem(SESSION_2.intValue());
        relatedItem.setTipoitem(SESSION.getId());
        relatedItem.setIdzonaprecio(PRICE_TYPE.intValue());

        PackIndexationContext ctx = new PackIndexationContext(PACK_ID);
        ctx.setPackItemRecords(List.of(mainItem, relatedItem));
        ctx.setNoMainSessionPackItemRecordsWithPriceTypeConfig(List.of(relatedItem));
        ctx.setSessionsById(Map.of(SESSION_2, relatedSession));
        ctx.getSessionOccupationByEventTypeByChannelId().put(CHANNEL_ID, buildSessionOccupationsByEventType(availableOccupation, unlimitedOccupation));
        return ctx;
    }

    private static Map<EventType, List<SessionOccupationByPriceZoneDTO>> buildSessionOccupationsByEventType(Long availableOccupation, boolean unlimitedOccupation) {
        SessionWithQuotasDTO sessionWithQuotas = new SessionWithQuotasDTO();
        sessionWithQuotas.setSessionId(SESSION_2);

        SessionPriceZoneOccupationDTO occupation = new SessionPriceZoneOccupationDTO();
        occupation.setPriceZoneId(PRICE_TYPE);
        occupation.setUnlimited(unlimitedOccupation);
        occupation.setStatus(Map.of(TicketStatus.AVAILABLE, availableOccupation));

        SessionOccupationByPriceZoneDTO sessionWithOccupation = new SessionOccupationByPriceZoneDTO();
        sessionWithOccupation.setSession(sessionWithQuotas);
        sessionWithOccupation.setOccupation(List.of(occupation));

        return Map.of(EventType.ACTIVITY, List.of(sessionWithOccupation));
    }

    // Tests of PackIndexationService.getPackRates(ctx);

    @Test
    public void testPackMainSessionWithSessionAndProductAndCombinedPrices() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3455L)
                .withMainPackItemType(SESSION)
                .withMainPackItemId(3210)
                .withRelatedSessionPackItemId(3217)
                .withRelatedProductPackItemId(3218)
                .withDefaultRateId(249959)
                .withNoDefaultRateId(249960)
                .withPricingType(COMBINED)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_session_and_zoned_mapped_combined_pricing");
    }

    @Test
    public void testPackMainSessionWithSessionAndProductAndIncrementalPricing() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3457L)
                .withMainPackItemType(SESSION)
                .withMainPackItemId(3212)
                .withRelatedSessionPackItemId(3219)
                .withRelatedProductPackItemId(3220)
                .withDefaultRateId(249963)
                .withNoDefaultRateId(249964)
                .withPricingType(INCREMENTAL)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_session_and_zoned_mapped_incremental_pricing");
    }

    @Test
    public void testPackMainSessionWithSessionAndProductAndNewPricing() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3458L)
                .withMainPackItemType(SESSION)
                .withMainPackItemId(3213)
                .withRelatedSessionPackItemId(3221)
                .withRelatedProductPackItemId(3222)
                .withDefaultRateId(249965)
                .withNoDefaultRateId(249966)
                .withPricingType(NEW_PRICE)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_session_and_zoned_mapped_new_pricing");
    }

    @Test
    public void testPackMainEventWithSessionAndProductAndCombinedPrices() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3459L)
                .withMainPackItemType(EVENT)
                .withMainPackItemId(3214)
                .withRelatedSessionPackItemId(3223)
                .withRelatedProductPackItemId(3224)
                .withDefaultRateId(249967)
                .withNoDefaultRateId(249968)
                .withPricingType(COMBINED)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_event_and_zoned_mapped_combined_pricing");
    }

    @Test
    public void testPackMainEventWithSessionAndProductAndIncrementalPrices() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3460L)
                .withMainPackItemType(EVENT)
                .withMainPackItemId(3215)
                .withRelatedSessionPackItemId(3225)
                .withRelatedProductPackItemId(3226)
                .withDefaultRateId(249969)
                .withNoDefaultRateId(249970)
                .withPricingType(INCREMENTAL)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_event_and_zoned_mapped_incremental_pricing");
    }

    @Test
    public void testPackMainEventWithSessionAndProductAndNewPricing() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3461L)
                .withMainPackItemType(EVENT)
                .withMainPackItemId(3216)
                .withRelatedSessionPackItemId(3227)
                .withRelatedProductPackItemId(3228)
                .withDefaultRateId(249971)
                .withNoDefaultRateId(249972)
                .withPricingType(NEW_PRICE)
                .withZoneMappingRelation(ZoneMappingRelation.ALL_MAPPINGS)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_event_and_zoned_mapped_new_pricing");
    }

    @Test
    public void testPackMainSessionWithSessionAndProductAndCombinedPricingAnd1ZonedSession() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3493L)
                .withMainPackItemType(SESSION)
                .withMainPackItemId(3314)
                .withRelatedSessionPackItemId(3315)
                .withRelatedProductPackItemId(3316)
                .withDefaultRateId(250890)
                .withNoDefaultRateId(250891)
                .withPricingType(COMBINED)
                .withZoneMappingRelation(ZoneMappingRelation.ONE_ZONED_MAPPING)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_session_and_one_zoned_mapped_combined_pricing");
    }

    @Test
    public void testPackMainSessionWithSessionAndProductAndCombinedPricingAndSameVenueMapping() throws IOException {
        PackIndexationContext ctx = new PackIndexationContextBuilder(3544L)
                .withMainPackItemType(SESSION)
                .withMainPackItemId(3390)
                .withRelatedSessionPackItemId(3393)
                .withRelatedProductPackItemId(3392)
                .withDefaultRateId(251213)
                .withNoDefaultRateId(251214)
                .withPricingType(COMBINED)
                .withZoneMappingRelation(ZoneMappingRelation.SAME_VENUE_MAPPING)
                .build();
        List<PackRateBase> packRates = PackIndexationService.getPackRates(ctx);
        assertEqualPackRates(packRates, "pack_with_main_session_and_same_venue_mapping_combined_pricing");
    }

    private void assertEqualPackRates(List<PackRateBase> packRates, String expectedJsonFile) throws IOException {
        Assertions.assertNotNull(packRates);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        InputStream expectedJson = getClass().getResourceAsStream("/expected/" + expectedJsonFile + ".json");

        String expectedStr = mapper.writeValueAsString(mapper.readTree(expectedJson));
        String actualStr = mapper.writeValueAsString(mapper.valueToTree(packRates));

        Assertions.assertEquals(expectedStr, actualStr);
    }

    private enum ZoneMappingRelation {
        ALL_MAPPINGS, ONE_ZONED_MAPPING, SAME_VENUE_MAPPING
    }

    private static class PackIndexationContextBuilder {

        private final long packId;
        private PackItemType mainPackItemType;
        private int mainPackItemId;
        private int relatedSessionPackItemId;
        private int relatedProductPackItemId;
        private int defaultRateId;
        private int noDefaultRateId;
        private PackPricingType pricingType;
        private ZoneMappingRelation zoneMappingRelation;

        private PackIndexationContextBuilder(Long packId) {
            this.packId = packId;
        }

        private PackIndexationContextBuilder withMainPackItemType(PackItemType mainPackItemType) {
            this.mainPackItemType = mainPackItemType;
            return this;
        }

        private PackIndexationContextBuilder withMainPackItemId(int mainPackItemId) {
            this.mainPackItemId = mainPackItemId;
            return this;
        }

        private PackIndexationContextBuilder withRelatedSessionPackItemId(int relatedSessionPackItemId) {
            this.relatedSessionPackItemId = relatedSessionPackItemId;
            return this;
        }

        private PackIndexationContextBuilder withRelatedProductPackItemId(int relatedProductPackItemId) {
            this.relatedProductPackItemId = relatedProductPackItemId;
            return this;
        }

        private PackIndexationContextBuilder withDefaultRateId(int defaultRateId) {
            this.defaultRateId = defaultRateId;
            return this;
        }

        private PackIndexationContextBuilder withNoDefaultRateId(int noDefaultRateId) {
            this.noDefaultRateId = noDefaultRateId;
            return this;
        }

        private PackIndexationContextBuilder withPricingType(PackPricingType pricingType) {
            this.pricingType = pricingType;
            return this;
        }

        private PackIndexationContextBuilder withZoneMappingRelation(ZoneMappingRelation zoneMappingRelation) {
            this.zoneMappingRelation = zoneMappingRelation;
            return this;
        }

        private PackIndexationContext build() {
            PackIndexationContext ctx = new PackIndexationContext(packId);

            ctx.setPackRates(buildPackRates(defaultRateId, noDefaultRateId));
            ctx.setMainPackItemPriceRecordsByRateId(buildMainPackItemPriceRecordsByRateId(defaultRateId, noDefaultRateId, NEW_PRICE.equals(pricingType)));

            int mainItemId = SESSION.equals(mainPackItemType) ? 646936 : 87002;
            CpanelPackItemRecord mainPackItemRecord = buildMainPackItemRecord(mainPackItemId, mainItemId, mainPackItemType);
            CpanelPackItemRecord relatedSessionPackItemRecord = buildRelatedSessionPackItemRecord(relatedSessionPackItemId);
            if (ZoneMappingRelation.ONE_ZONED_MAPPING.equals(zoneMappingRelation)) {
                relatedSessionPackItemRecord.setIditem(646937);
                relatedSessionPackItemRecord.setIdzonaprecio(441398);
            } else if(ZoneMappingRelation.SAME_VENUE_MAPPING.equals(zoneMappingRelation)) {
                relatedSessionPackItemRecord.setIditem(648090);
            }
            CpanelPackItemRecord relatedProductPackItemRecord = buildRelatedProductPackItemRecord(relatedProductPackItemId);

            ctx.setPackItemRecords(List.of(mainPackItemRecord, relatedSessionPackItemRecord, relatedProductPackItemRecord));
            ctx.setMainPackItemRecord(mainPackItemRecord);


            List<ItemPackPriceInfoRecord> mainItemPackPriceInfoRecords = new ArrayList<>();
            switch (zoneMappingRelation) {
                case ALL_MAPPINGS -> {
                    ctx.setPriceTypesMappingByPackItemId(Map.of(relatedSessionPackItemId, List.of(new CpanelPackZonaPrecioMappingRecord())));
                    mainItemPackPriceInfoRecords = List.of(
                            buildItemPackPriceInfoRecord(relatedSessionPackItemId, 249973, 429844, "General", 1.0),
                            buildItemPackPriceInfoRecord(relatedSessionPackItemId, 249973, 429845, "Premium", 5.0));
                }

                case ONE_ZONED_MAPPING ->
                        mainItemPackPriceInfoRecords = List.of(buildItemPackPriceInfoRecord(relatedSessionPackItemId, 233247, 441398, "General", 2.0));

                case SAME_VENUE_MAPPING -> {
                }
            }
            ItemPackPriceInfoRecord relatedItemPackPriceInfoRecord1 = buildItemPackPriceInfoRecord(mainPackItemId, 233248, 429844, "General", 5.0);
            ItemPackPriceInfoRecord relatedItemPackPriceInfoRecord2 = buildItemPackPriceInfoRecord(mainPackItemId, 233247, 429844, "General", 10.0);
            ItemPackPriceInfoRecord relatedItemPackPriceInfoRecord3 = buildItemPackPriceInfoRecord(mainPackItemId, 233248, 429845, "Premium", 15.0);
            ItemPackPriceInfoRecord relatedItemPackPriceInfoRecord4 = buildItemPackPriceInfoRecord(mainPackItemId, 233247, 429845, "Premium", 20.0);
            ctx.setItemPackPriceInfoRecordsByPackItemId(Map.of(
                    mainPackItemId, List.of(relatedItemPackPriceInfoRecord1, relatedItemPackPriceInfoRecord2, relatedItemPackPriceInfoRecord3, relatedItemPackPriceInfoRecord4),
                    relatedSessionPackItemId, mainItemPackPriceInfoRecords));


            ctx.setPriceByProductPackItemId(Map.of(relatedProductPackItemId, 10.0));
            ctx.setPricingType(pricingType);

            if (INCREMENTAL.equals(pricingType)) {
                PackDetailRecord packDetailRecord = new PackDetailRecord();
                packDetailRecord.setIncremementoprecio(1.0);
                ctx.setPackDetailRecord(packDetailRecord);
            }
            return ctx;
        }

        private static CpanelPackItemRecord buildMainPackItemRecord(int id, int itemId, PackItemType type) {
            return buildPackItemRecord(id, true, itemId, type);
        }

        private static CpanelPackItemRecord buildRelatedSessionPackItemRecord(int id) {
            return buildPackItemRecord(id, false, 646939, SESSION);
        }

        private static CpanelPackItemRecord buildRelatedProductPackItemRecord(int id) {
            return buildPackItemRecord(id, false, 7214, PRODUCT);
        }

        private static CpanelPackItemRecord buildPackItemRecord(int id, boolean principal, int itemId, PackItemType type) {
            CpanelPackItemRecord record = new CpanelPackItemRecord();
            record.setIdpackitem(id);
            record.setPrincipal(principal);
            record.setIditem(itemId);
            record.setTipoitem(type.getId());
            return record;
        }

        private static Map<Integer, List<PriceRecord>> buildMainPackItemPriceRecordsByRateId(int defaultRateId, int noDefaultRateId, boolean isNewPrice) {
            PriceRecord defaultRatePriceRecord1 = buildPriceRecord(defaultRateId, 429844, "General", isNewPrice ? 100.0 : 10.0);
            PriceRecord defaultRatePriceRecord2 = buildPriceRecord(defaultRateId, 429845, "Premium", isNewPrice ? 200.0 : 20.0);
            PriceRecord noDefaultRatePriceRecord1 = buildPriceRecord(noDefaultRateId, 429844, "General", isNewPrice ? 300.0 : 5.0);
            PriceRecord noDefaultRatePriceRecord2 = buildPriceRecord(noDefaultRateId, 429845, "Premium", isNewPrice ? 400.0 : 15.0);

            return Map.of(defaultRateId, List.of(defaultRatePriceRecord1, defaultRatePriceRecord2),
                    noDefaultRateId, List.of(noDefaultRatePriceRecord1, noDefaultRatePriceRecord2));
        }

        private static PriceRecord buildPriceRecord(int noDefaultRateId, int priceZoneId, String code, double price) {
            PriceRecord priceRecord = new PriceRecord();
            priceRecord.setPriceZoneId(priceZoneId);
            priceRecord.setPriceZoneCode(code);
            priceRecord.setRateId(noDefaultRateId);
            priceRecord.setPrice(price);
            return priceRecord;
        }

        private static List<PackRateRecord> buildPackRates(int defaultRateId, int noDefaultRateId) {
            PackRateRecord packRateRecord1 = buildPackRateRecord(defaultRateId, "Tarifa general", true, 233247);
            PackRateRecord packRateRecord2 = buildPackRateRecord(noDefaultRateId, "Tarifa especial", false, 233248);
            return List.of(packRateRecord1, packRateRecord2);
        }

        private static PackRateRecord buildPackRateRecord(int noDefaultRateId, String name, boolean isDefault, int eventRateId) {
            PackRateRecord packRateRecord2 = new PackRateRecord();
            packRateRecord2.setIdtarifa(noDefaultRateId);
            packRateRecord2.setName(name);
            packRateRecord2.setDefecto(isDefault);
            packRateRecord2.setIdtarifaevento(eventRateId);
            return packRateRecord2;
        }

        private static ItemPackPriceInfoRecord buildItemPackPriceInfoRecord(int packItemId, int itemRateId, int mainPriceZone, String general, double price) {
            ItemPackPriceInfoRecord record = new ItemPackPriceInfoRecord();
            record.setPackItemId(packItemId);
            record.setItemRateId(itemRateId);
            record.setMainPriceZone(mainPriceZone);
            record.setMainPriceZoneName(general);
            record.setItemPrice(price);
            return record;
        }

    }

    @Test
    void testGetSessionFilter_EventMainItemWithSessionSubsets_ShouldReturnSessionIds() {
        Long packId = 100L;
        Integer packItemId = 200;
        Integer eventId = 500;
        Integer sessionId1 = 301;
        Integer sessionId2 = 302;
        Integer sessionId3 = 303;

        CpanelPackItemRecord mainPackItemRecord = new CpanelPackItemRecord();
        mainPackItemRecord.setIdpackitem(packItemId);
        mainPackItemRecord.setTipoitem(PackItemType.EVENT.getId());
        mainPackItemRecord.setPrincipal(true);
        mainPackItemRecord.setIditem(eventId);
        mainPackItemRecord.setIdconfiguracion(100);

        CpanelPackItemSubsetRecord subsetRecord1 = new CpanelPackItemSubsetRecord();
        subsetRecord1.setIdpackitem(packItemId);
        subsetRecord1.setIdsubitem(sessionId1);
        subsetRecord1.setType(PackItemSubsetType.SESSION.getId());

        CpanelPackItemSubsetRecord subsetRecord2 = new CpanelPackItemSubsetRecord();
        subsetRecord2.setIdpackitem(packItemId);
        subsetRecord2.setIdsubitem(sessionId2);
        subsetRecord2.setType(PackItemSubsetType.SESSION.getId());

        CpanelPackItemSubsetRecord subsetRecord3 = new CpanelPackItemSubsetRecord();
        subsetRecord3.setIdpackitem(packItemId);
        subsetRecord3.setIdsubitem(sessionId3);
        subsetRecord3.setType(PackItemSubsetType.SESSION.getId());

        List<CpanelPackItemSubsetRecord> subsetRecords = List.of(subsetRecord1, subsetRecord2, subsetRecord3);

        Event event = new Event();
        event.setEventId(eventId.longValue());
        event.setEventStatus(EventStatus.READY.getId());
        event.setCurrency(1);

        when(packDao.getPackDetailRecordById(packId.intValue())).thenReturn(createPackDetailRecord(packId));
        when(packItemsDao.getPackItemRecordsById(packId.intValue())).thenReturn(List.of(mainPackItemRecord));
        when(packItemsDao.getPackMainItemRecordById(packId.intValue())).thenReturn(mainPackItemRecord);
        when(packItemSubsetDao.getSubsetsByPackItemId(packItemId)).thenReturn(subsetRecords);
        when(catalogEventCouchDao.get(anyString())).thenReturn(event);
        when(packChannelDao.getPackChannels(any())).thenReturn(createPackChannelRecords());
        when(packChannelSaleRequestDao.getPackSaleRequests(any(), anyList())).thenReturn(createPackSaleRequestRecords());
        when(channelCurrenciesDao.getCurrencies(anyList())).thenReturn(createChannelCurrencyRecords());
        when(venueConfigurationDao.getVenueIdByVenueConfigId(anyInt())).thenReturn(1);

        PackIndexationContext ctx = packIndexationService.preparePackContext(packId, null, false);

        List<Long> sessionsFilter = ctx.getSessionsFilter();
        Assertions.assertNotNull(sessionsFilter, "sessionsFilter should not be null");
        Assertions.assertEquals(3, sessionsFilter.size(), "sessionsFilter should contain 3 session IDs");
        Assertions.assertTrue(sessionsFilter.contains(sessionId1.longValue()), "sessionsFilter should contain sessionId1");
        Assertions.assertTrue(sessionsFilter.contains(sessionId2.longValue()), "sessionsFilter should contain sessionId2");
        Assertions.assertTrue(sessionsFilter.contains(sessionId3.longValue()), "sessionsFilter should contain sessionId3");
    }

    private PackDetailRecord createPackDetailRecord(Long packId) {
        PackDetailRecord packDetailRecord = new PackDetailRecord();
        packDetailRecord.setIdpack(packId.intValue());
        packDetailRecord.setEstado(1);
        packDetailRecord.setTipo(0);
        return packDetailRecord;
    }

    private List<CpanelPackCanalRecord> createPackChannelRecords() {
        CpanelPackCanalRecord packChannelRecord = new CpanelPackCanalRecord();
        packChannelRecord.setIdpack(100);
        packChannelRecord.setIdcanal(1);
        packChannelRecord.setEstado(1);
        return List.of(packChannelRecord);
    }

    private List<CpanelPackCanalSolicitudVentaRecord> createPackSaleRequestRecords() {
        CpanelPackCanalSolicitudVentaRecord saleRequestRecord = new CpanelPackCanalSolicitudVentaRecord();
        saleRequestRecord.setIdpack(100);
        saleRequestRecord.setIdcanal(1);
        saleRequestRecord.setEstado(1);
        return List.of(saleRequestRecord);
    }

    private List<CpanelCanalCurrencyRecord> createChannelCurrencyRecords() {
        CpanelCanalCurrencyRecord currencyRecord = new CpanelCanalCurrencyRecord();
        currencyRecord.setIdcanal(1);
        currencyRecord.setIdcurrency(1);
        return List.of(currencyRecord);
    }

}
