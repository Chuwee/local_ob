package es.onebox.event.priceengine.simulation.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.simulation.converter.PromotionUtils;
import es.onebox.event.priceengine.simulation.dao.AssignmentPriceZoneDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfig;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfigWithPrice;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.RateMap;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.priceengine.simulation.domain.enums.PromotionType;
import es.onebox.event.priceengine.simulation.domain.enums.SurchargeType;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.event.priceengine.taxes.domain.Taxes;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAuto10Percent;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAuto10PercentNoCumulative;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutoNewPrice;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutoNewPrice2;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutoNewPriceBig;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutomaticFixed;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutomaticNBP;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutomaticNBPHigher;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getAutomaticPercentage;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getCombinesOfAutomaticAndDiscountAndPromo;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getCombinesOfAutomaticAndDiscountAndPromoAndAutoNotCumulative;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getCombinesOfAutomaticNBPAndDiscountNBP;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getCombinesOfDiscountAndPromo;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscount1E;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscountFixed;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscountNBP;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscountNBPHigher;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscountNewPrice;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getDiscountPercentage;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromo2E;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromo2EDifferentChannel;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromo2EDifferentPriceZone;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromo2EDifferentRate;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromo2ESpecificRate;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromotionFixed;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromotionNegative;
import static es.onebox.event.priceengine.simulation.converter.PromotionUtils.getPromotionPercentage;
import static es.onebox.event.priceengine.simulation.converter.SurchargeUtil.getEventSurchargesFixedValue;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getChannelEventRecord;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getEventChannelRecord;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getExpected;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getListOfPriceZoneRateVenueConfigCustomRecord;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getPriceZoneConfig;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getRate;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.getVenueConfig;
import static es.onebox.event.priceengine.simulation.converter.VenueConfigRatePriceZonesUtil.updateSurchargesExpected;
import static es.onebox.utils.ObjectRandomizer.randomLong;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

public class PriceEngineSimulationServiceTest extends PriceEngineSimulationAbstractTest {

    private static final String RESOURCE_PATH = "expectedsimulations/";
    public static final String TIME_STAMP = "2019-05-02T00:00:00Z";

    @Mock
    private EventChannelDao eventChannelDao;
    @Mock
    private ChannelEventDao channelEventDao;
    @Mock
    private CatalogSurchargeService surchargeService;
    @Mock
    private AssignmentPriceZoneDao assignmentPriceZoneDao;
    @Mock
    private EventPromotionTemplateDao eventPromotionTemplateDao;
    @InjectMocks
    PriceEngineSimulationService priceEngineSimulationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Venue, Rates and Price zones
    private static final Long SALE_REQUEST_ID = 8L;
    private final CpanelEventoCanalRecord eventChannel = getEventChannelRecord(true);
    private final List<CpanelTarifaRecord> rates = Arrays.asList(getRate(1, "General"), getRate(2, "Junior"));
    private final CpanelConfigRecintoRecord venueConfig = getVenueConfig(1, "Recinto OB");
    private final List<Double> basePrices = Arrays.asList(5.5, 15.5, 10.0);
    private final List<PriceZoneConfigWithPrice> priceZones =
            Arrays.asList(new PriceZoneConfigWithPrice(basePrices.get(0), getPriceZoneConfig(1, "Platea")),
                    new PriceZoneConfigWithPrice(basePrices.get(1), getPriceZoneConfig(2, "VIP")),
                    new PriceZoneConfigWithPrice(basePrices.get(2), getPriceZoneConfig(3, "Anfiteatro")));
    private final List<PriceZoneRateVenueConfigCustomRecord> listEventConfig
            = getListOfPriceZoneRateVenueConfigCustomRecord(priceZones, rates, venueConfig);

    private static final Long CHANNEL_ID = randomLong();
    private static final Long VENUE_CONFIG_ID = randomLong();
    private static final Long RATE_ID = randomLong();
    private static final Double ORIGINAL_PRICE = 100.0;
    private static final Long SESSION_TAX_1_ID = 1L;
    private static final Double SESSION_TAX_1_VALUE = 21.0;
    private static final Long SESSION_TAX_2_ID = 2L;
    private static final Double SESSION_TAX_2_VALUE = 18.0;
    private static final Long SURCHARGE_TAX_1_ID = 3L;
    private static final Double SURCHARGE_TAX_1_VALUE = 10.0;
    private static final Long SURCHARGE_TAX_2_ID = 4L;
    private static final Double SURCHARGE_TAX_2_VALUE = 20.0;
    private static final Long CHANNEL_SURCHARGE_TAX_1_ID = 5L;
    private static final Double CHANNEL_SURCHARGE_TAX_1_VALUE = 5.0;
    private static final Long CHANNEL_SURCHARGE_TAX_2_ID = 6L;
    private static final Double CHANNEL_SURCHARGE_TAX_2_VALUE = 12.0;
    private static final Double CHANNEL_SURCHARGE_VALUE = 2.0;
    private static final Double PROMOTER_SURCHARGE_VALUE = 4.0;

    @Test
    void getPriceSimulationForCatalogTaxModeIncluded() {
        VenueConfigPricesSimulation result = priceEngineSimulationService.getPriceSimulationForCatalog(
                CHANNEL_ID, getVenueConfigMap(), getEventSurcharges(), false,
                getEventPromotionRecords(), getTaxes());

        List<PriceSimulation> simulations = result.getRates().get(0).getPriceTypes().get(0).getSimulations();

        Price noPromotions = simulations.stream().filter(s -> s.getBasePromotions() == null).findFirst().get().getPrice();
        checkPriceTotals(noPromotions, ORIGINAL_PRICE, 71.94, 106.0);
        checkSessionTaxes(noPromotions, 15.11, 12.95);
        checkSurchargesPrices(noPromotions,
                CHANNEL_SURCHARGE_VALUE, 1.71, 0.09, 0.20,
                PROMOTER_SURCHARGE_VALUE, 3.08, 0.31, 0.61);

        Price bothPromotions = simulations.stream().filter(s -> s.getBasePromotions() != null && s.getBasePromotions().size() == 2).findFirst().get().getPrice();
        checkPriceTotals(bothPromotions, 84.0, 60.43, 90.0);
        checkSessionTaxes(bothPromotions, 12.69, 10.88);
        checkSurchargesPrices(bothPromotions,
                CHANNEL_SURCHARGE_VALUE, 1.71, 0.09, 0.20,
                PROMOTER_SURCHARGE_VALUE, 3.08, 0.31, 0.61);
    }

    private static void checkPriceTotals(Price noPromotions, Double base, Double net, Double total) {
        assertEquals(base, noPromotions.getBase());
        assertEquals(net, noPromotions.getNet());
        assertEquals(total, noPromotions.getTotal());
    }

    private static void checkSessionTaxes(Price price, Double tax1, Double tax2) {
        Taxes taxes = price.getTaxes();
        Double total = NumberUtils.minus(price.getBase(), price.getNet());
        assertEquals(total, taxes.getTotal());
        assertEquals(tax1, taxes.getBreakdown().stream().filter(t -> t.getId().equals(SESSION_TAX_1_ID)).findFirst().get().getAmount());
        assertEquals(tax2, taxes.getBreakdown().stream().filter(t -> t.getId().equals(SESSION_TAX_2_ID)).findFirst().get().getAmount());
    }

    private static void checkSurchargesPrices(Price price,
                                              Double channelValue, Double channelNet, Double channelTax1, Double channelTax2,
                                              Double promoterValue, Double promoterNet, Double promoterTax1, Double promoterTax2) {
        Surcharge channelSurcharge = price.getSurcharges().stream().filter(s -> SurchargeType.CHANNEL.equals(s.getType())).findFirst().get();
        assertEquals(channelValue, channelSurcharge.getValue());
        assertEquals(channelNet, channelSurcharge.getNet());
        Double totalChannelTaxes = NumberUtils.minus(channelSurcharge.getValue(), channelSurcharge.getNet());
        assertEquals(totalChannelTaxes, channelSurcharge.getTaxes().getTotal());
        assertEquals(channelTax1, channelSurcharge.getTaxes().getBreakdown().stream().filter(t -> t.getId().equals(CHANNEL_SURCHARGE_TAX_1_ID)).findFirst().get().getAmount());
        assertEquals(channelTax2, channelSurcharge.getTaxes().getBreakdown().stream().filter(t -> t.getId().equals(CHANNEL_SURCHARGE_TAX_2_ID)).findFirst().get().getAmount());

        Surcharge promoterSurcharge = price.getSurcharges().stream().filter(s -> SurchargeType.PROMOTER.equals(s.getType())).findFirst().get();
        assertEquals(promoterValue, promoterSurcharge.getValue());
        assertEquals(promoterNet, promoterSurcharge.getNet());
        Double totalPromoterTaxes = NumberUtils.minus(promoterSurcharge.getValue(), promoterSurcharge.getNet());
        assertEquals(totalPromoterTaxes, promoterSurcharge.getTaxes().getTotal());
        assertEquals(promoterTax1, promoterSurcharge.getTaxes().getBreakdown().stream().filter(t -> t.getId().equals(SURCHARGE_TAX_1_ID)).findFirst().get().getAmount());
        assertEquals(promoterTax2, promoterSurcharge.getTaxes().getBreakdown().stream().filter(t -> t.getId().equals(SURCHARGE_TAX_2_ID)).findFirst().get().getAmount());
    }

    private static List<EventPromotionRecord> getEventPromotionRecords() {
        EventPromotionRecord promotion1 = new EventPromotionRecord();
        promotion1.setEventPromotionTemplateId(1);
        promotion1.setDiscountType(0);
        promotion1.setSubtype(PromotionType.AUTOMATIC.getId());
        promotion1.setFixedDiscountValue(10.5);
        promotion1.setNotCumulative(false);
        EventPromotionRecord promotion2 = new EventPromotionRecord();
        promotion2.setEventPromotionTemplateId(2);
        promotion2.setSubtype(PromotionType.DISCOUNT.getId());
        promotion2.setDiscountType(0);
        promotion2.setFixedDiscountValue(5.5);
        promotion2.setNotCumulative(false);
        return List.of(promotion1, promotion2);
    }

    private static SessionTaxes getTaxes() {
        SessionTaxInfo priceTax1 = new SessionTaxInfo();
        priceTax1.setId(SESSION_TAX_1_ID);
        priceTax1.setValue(SESSION_TAX_1_VALUE);
        SessionTaxInfo priceTax2 = new SessionTaxInfo();
        priceTax2.setId(SESSION_TAX_2_ID);
        priceTax2.setValue(SESSION_TAX_2_VALUE);

        SessionTaxInfo surchargeTax1 = new SessionTaxInfo();
        surchargeTax1.setId(SURCHARGE_TAX_1_ID);
        surchargeTax1.setValue(SURCHARGE_TAX_1_VALUE);
        SessionTaxInfo surchargeTax2 = new SessionTaxInfo();
        surchargeTax2.setId(SURCHARGE_TAX_2_ID);
        surchargeTax2.setValue(SURCHARGE_TAX_2_VALUE);

        ChannelTaxInfo channelSurchargeTax1 = new ChannelTaxInfo();
        channelSurchargeTax1.setId(CHANNEL_SURCHARGE_TAX_1_ID);
        channelSurchargeTax1.setValue(CHANNEL_SURCHARGE_TAX_1_VALUE);
        ChannelTaxInfo channelSurchargeTax2 = new ChannelTaxInfo();
        channelSurchargeTax2.setId(CHANNEL_SURCHARGE_TAX_2_ID);
        channelSurchargeTax2.setValue(CHANNEL_SURCHARGE_TAX_2_VALUE);

        SessionTaxes sessionTaxes = new SessionTaxes();
        sessionTaxes.setPricesTaxes(List.of(priceTax1, priceTax2));
        sessionTaxes.setSurchargesTaxes(List.of(surchargeTax1, surchargeTax2));
        sessionTaxes.setChannelSurchargesTaxes(List.of(channelSurchargeTax1, channelSurchargeTax2));
        return sessionTaxes;
    }

    private static Map<Integer, VenueConfigMap> getVenueConfigMap() {
        Map<Integer, VenueConfigMap> venueConfigMap = new HashMap<>();
        VenueConfigMap venueConfig = new VenueConfigMap();
        venueConfig.setId(VENUE_CONFIG_ID);
        Map<Integer, RateMap> rateMap = new HashMap<>();
        RateMap rate = new RateMap();
        rate.setId(RATE_ID.intValue());
        PriceZone priceZone = new PriceZone();
        priceZone.setId(1L);
        priceZone.setConfig(new PriceZoneConfig());
        priceZone.setPrice(ORIGINAL_PRICE);
        rate.setPriceZones(List.of(priceZone));
        rateMap.put(RATE_ID.intValue(), rate);
        venueConfig.setRate(rateMap);
        venueConfigMap.put(VENUE_CONFIG_ID.intValue(), venueConfig);
        return venueConfigMap;
    }


    @Test
    public void simulationSaleRequestNotFoundOK() {
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(null);
        Assertions.assertThrows(OneboxRestException.class, () ->
                priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID));
    }

    @Test
    public void simulationEventChannelNotFoundOK() {
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.empty());
        Assertions.assertThrows(OneboxRestException.class, () ->
                priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID));
    }

    @Test
    public void simulationSaleRequestNoPromotionsNoSurchargesOK() {

        //given: defined config

        //when
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        CpanelCanalEventoRecord channelEvent = getChannelEventRecord(false);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.of(channelEvent));
        Mockito.when(assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventChannel.getIdevento())).thenReturn(listEventConfig);
        List<VenueConfigPricesSimulation> actual = priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID);

        //then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));
        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both
    public void simulationSaleRequestWithSurchargesNoPromotionsOK() {

        /////given: defined config + surcharges promoter and channel
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(false);

        /////when
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        CpanelCanalEventoRecord channelEvent = getChannelEventRecord(false);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.of(channelEvent));
        Mockito.when(assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventChannel.getIdevento())).thenReturn(listEventConfig);
        Mockito.when(surchargeService.getSurchargeRangesByChannelEventRelationShips(any(CpanelCanalEventoRecord.class),
                        any(CpanelEventoCanalRecord.class)))
                .thenReturn(eventSurcharges);
        List<VenueConfigPricesSimulation> actual = priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));
        //Geneal
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(9.5, 19.5, 14.0));
        //Junior
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(9.5, 19.5, 14.0));
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with two ranges both and fixed value
    public void simulationSaleRequestWithRangeOfSurchargesNoPromotionsOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);

        /////when
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        CpanelCanalEventoRecord channelEvent = getChannelEventRecord(false);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.of(channelEvent));
        Mockito.when(assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventChannel.getIdevento())).thenReturn(listEventConfig);
        Mockito.when(surchargeService.getSurchargeRangesByChannelEventRelationShips(any(CpanelCanalEventoRecord.class),
                        any(CpanelEventoCanalRecord.class)))
                .thenReturn(eventSurcharges);
        List<VenueConfigPricesSimulation> actual = priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));
        //General - expected totals
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //Junior - expected totals
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both and Promotion 2€
    public void simulationSaleRequestWithRangeOfSurchargesPromo2EOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getPromo2E());
        List<List<Promotion>> promotions = new ArrayList<>();
        promotions.add(PromotionUtils.convertToPromotions(promotionsRecord));

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 1);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 1);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both, Discount 1€ and Promotion 2€
    public void simulationSaleRequestWithRangeOfSurchargesDiscount1EAndPromo2EOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Arrays.asList(getPromo2E(), getDiscount1E());
        List<List<Promotion>> promotions = getCombinesOfDiscountAndPromo();

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 1);

        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 2);

        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 3);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 1);
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 2);
        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 3);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both, Automatic 10% and Discount 1€ and Promotion 2€
    public void simulationSaleRequestWithRangeOfSurchargesAndAutomatic10PercentAndDiscount1EAndPromo2EOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Arrays.asList(getAuto10Percent(), getPromo2E(), getDiscount1E());
        List<List<Promotion>> promotions = getCombinesOfAutomaticAndDiscountAndPromo();

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation automatic 5.5, 15.5, 10.0
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.55, 21.55, 14.6),
                Arrays.asList(4.95, 13.95, 9.0), 1);

        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 2);

        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 3);

        // simulation automatic - discount
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.55, 20.55, 13.6),
                Arrays.asList(3.95, 12.95, 8.0), 4);

        // simulation automatic - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.55, 19.55, 12.6),
                Arrays.asList(2.95, 11.95, 7.0), 5);

        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 6);

        // simulation automatic - discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(7.55, 18.55, 11.6),
                Arrays.asList(1.95, 10.95, 6.0), 7); //1.95


        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation automatic 5.5, 15.5, 10.0
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.55, 21.55, 14.6),
                Arrays.asList(4.95, 13.95, 9.0), 1);

        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 2);

        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 3);

        // simulation automatic - discount
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.55, 20.55, 13.6),
                Arrays.asList(3.95, 12.95, 8.0), 4);

        // simulation automatic - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.55, 19.55, 12.6),
                Arrays.asList(2.95, 11.95, 7.0), 5);

        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 6);

        // simulation automatic - discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(7.55, 18.55, 11.6),
                Arrays.asList(1.95, 10.95, 6.0), 7);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test
//With surcharges for channel and promoter with 2€ both, Automatic 10% and Discount 1€, Promotion 2€ and Auto no cumulative 3€
    public void simulationSaleRequestWithRangeOfSurchargesAndAutomatic10PercentAndDiscount1EAndPromo2EAndAutoNotCumulativeOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Arrays.asList(getAuto10PercentNoCumulative(), getAuto10Percent(), getPromo2E(), getDiscount1E());
        List<List<Promotion>> promotions = getCombinesOfAutomaticAndDiscountAndPromoAndAutoNotCumulative();

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation automatic not cumulative 5.5, 15.5, 10.0
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 1);

        //simulation automatic 10% cumulative
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.55, 21.55, 14.6),
                Arrays.asList(4.95, 13.95, 9.0), 2);

        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 3);

        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 4);

        // simulation automatic - discount
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(9.55, 20.55, 13.6),
                Arrays.asList(3.95, 12.95, 8.0), 5);

        // simulation automatic - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.55, 19.55, 12.6),
                Arrays.asList(2.95, 11.95, 7.0), 6);

        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 7);

        // simulation automatic - discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(7.55, 18.55, 11.6),
                Arrays.asList(1.95, 10.95, 6.0), 8);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation automatic not cumulative 5.5, 15.5, 10.0
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 1);

        //simulation automatic 10% cumulative
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.55, 21.55, 14.6),
                Arrays.asList(4.95, 13.95, 9.0), 2);

        //simulation discount
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.1, 22.1, 14.6),
                Arrays.asList(4.5, 14.5, 9.0), 3);

        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 4);

        // simulation automatic - discount
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.55, 20.55, 13.6),
                Arrays.asList(3.95, 12.95, 8.0), 5);

        // simulation automatic - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.55, 19.55, 12.6),
                Arrays.asList(2.95, 11.95, 7.0), 6);

        //simulation discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.1, 20.1, 12.6),
                Arrays.asList(2.5, 12.5, 7.0), 7);

        // simulation automatic - discount - promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(7.55, 18.55, 11.6),
                Arrays.asList(1.95, 10.95, 6.0), 8);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both and Promotion 2€ for different channel
    public void simulationSaleRequestWithRangeOfSurchargesPromo2EDifferentChannelOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getPromo2EDifferentChannel());

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both and Promotion 2€ for different price zone
    public void simulationSaleRequestWithRangeOfSurchargesPromo2EDifferentPriceZoneOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getPromo2EDifferentPriceZone());

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both and Promotion 2€ for different Rate
    public void simulationSaleRequestWithRangeOfSurchargesPromo2EDifferentRateOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getPromo2EDifferentRate());

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, null));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with 2€ both and Promotion 2€ for specific rate
    public void simulationSaleRequestWithRangeOfSurchargesPromo2ESpecificRateOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getPromo2ESpecificRate());
        List<List<Promotion>> promotions = new ArrayList<>();
        promotions.add(PromotionUtils.convertToPromotions(promotionsRecord));

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //update
        updateExpectedRemovePromotionByRate(expected.get(0), 1);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(9.1, 21.1, 13.6),
                Arrays.asList(3.5, 13.5, 8.0), 1);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with promo automatic with new Price (ranges)
    public void simulationSaleRequestWithRangeOfSurchargesAutomaticNewPriceOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getAutoNewPrice());
        List<List<Promotion>> promotions = new ArrayList<>();
        promotions.add(PromotionUtils.convertToPromotions(promotionsRecord));

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //update
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.48, 14.48, 14.48),
                Arrays.asList(4.88, 8.88, 8.88), 1);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.48, 14.48, 14.48),
                Arrays.asList(4.88, 8.88, 8.88), 1);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with new Price bigger than original price -> keep Original
    public void simulationSaleRequestWithRangeOfSurchargesAutomaticNewPriceBiggerThanBaseOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Collections.singletonList(getAutoNewPriceBig());
        List<List<Promotion>> promotions = new ArrayList<>();
        promotions.add(PromotionUtils.convertToPromotions(promotionsRecord));

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //update
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(11.1, 23.1, 17.6),
                Arrays.asList(5.5, 15.5, 10.0), 1);

        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));
        //simulation promo
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(11.1, 23.1, 17.6),
                Arrays.asList(5.5, 15.5, 10.0), 1);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test//With surcharges for channel and promoter with promo automatic and discount with new base Price (ranges) both
    public void simulationSaleRequestWithRangeOfSurchargesAutomaticNewPriceAndDiscountNewPriceOK() {

        /////given:
        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(true);
        List<EventPromotionRecord> promotionsRecord = Arrays.asList(getAutoNewPrice2(), getDiscountNewPrice());
        List<List<Promotion>> promotions = getCombinesOfAutomaticNBPAndDiscountNBP();

        /////when
        List<VenueConfigPricesSimulation> actual = getMockConfig(eventSurcharges, promotionsRecord);

        /////then
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(getExpected(venueConfig, rates, priceZones, promotions));

        ///////////////////////////////Rate: General - prices config
        // simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(0).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation automatic NPB
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.6, 13.6, 13.6),
                Arrays.asList(3.0, 8.0, 8.0), 1);

        //simulation discount NPB
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(10.6, 14.6, 14.6),
                Arrays.asList(5.0, 9.0, 9.0), 2);

        // Promo Automatic NBP + Discount NBP
        setBasesAndTotals(expected.get(0).getRates().get(0).getPriceTypes(),
                Arrays.asList(8.1, 7.1, 12.6),
                Arrays.asList(2.5, 1.5, 7.0), 3);


        ///////////////////////////////Rate: Junior - prices config
        //Simulation without promo
        setTotalsDefault(expected.get(0).getRates().get(1).getPriceTypes(), Arrays.asList(11.1, 23.1, 17.6));

        //simulation promo
        //simulation automatic NPB
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.6, 13.6, 13.6),
                Arrays.asList(3.0, 8.0, 8.0), 1);

        //simulation discount NPB
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(10.6, 14.6, 14.6),
                Arrays.asList(5.0, 9.0, 9.0), 2);

        // Promo Automatic NBP + Discount NBP
        setBasesAndTotals(expected.get(0).getRates().get(1).getPriceTypes(),
                Arrays.asList(8.1, 7.1, 12.6),
                Arrays.asList(2.5, 1.5, 7.0), 3);

        //Adding surcharges
        updateSurchargesExpected(expected.get(0), eventSurcharges);

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);
    }

    @Test //AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE
    public void simulationTwoNewBasePricesOK() throws Exception {
        /*Combinations:
        Two new base prices
        ------------------------------------------------------------------------
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE HIGHER
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE HIGHER + PROMOTION FIXED
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE HIGHER + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE HIGHER + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE + PROMOTION FIXED
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT NEW PRICE + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE HIGHER
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE HIGHER + PROMOTION FIXED
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE HIGHER + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE HIGHER + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE + PROMOTION FIXED
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE + DISCOUNT NEW PRICE + PROMOTION NEGATIVE

        One new base price (AUTOMATIC)
        ------------------------------------------------------------------------
        AUTOMATIC NEW PRICE
        AUTOMATIC NEW PRICE + DISCOUNT FIXED
        AUTOMATIC NEW PRICE + DISCOUNT FIXED + PROMOTION FIXED
        AUTOMATIC NEW PRICE + DISCOUNT FIXED + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE + DISCOUNT FIXED + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE + DISCOUNT PERCENTAGE + PROMOTION FIXED
        AUTOMATIC NEW PRICE + DISCOUNT PERCENTAGE + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE + DISCOUNT PERCENTAGE + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE + PROMOTION FIXED
        AUTOMATIC NEW PRICE + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE HIGHER
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT FIXED
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT FIXED + PROMOTION FIXED
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT FIXED + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT FIXED + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT PERCENTAGE + PROMOTION FIXED
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT PERCENTAGE + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE HIGHER + DISCOUNT PERCENTAGE + PROMOTION NEGATIVE
        AUTOMATIC NEW PRICE HIGHER + PROMOTION FIXED
        AUTOMATIC NEW PRICE HIGHER + PROMOTION PERCENTAGE
        AUTOMATIC NEW PRICE HIGHER + PROMOTION NEGATIVE

        One new base price (DISCOUNT)
        ------------------------------------------------------------------------
        DISCOUNT NEW PRICE
        DISCOUNT NEW PRICE + AUTOMATIC FIXED
        DISCOUNT NEW PRICE + AUTOMATIC FIXED + PROMOTION FIXED
        DISCOUNT NEW PRICE + AUTOMATIC FIXED + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE + AUTOMATIC FIXED + PROMOTION NEGATIVE
        DISCOUNT NEW PRICE + AUTOMATIC PERCENTAGE
        DISCOUNT NEW PRICE + AUTOMATIC PERCENTAGE + PROMOTION FIXED
        DISCOUNT NEW PRICE + AUTOMATIC PERCENTAGE + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE + AUTOMATIC PERCENTAGE + PROMOTION NEGATIVE
        DISCOUNT NEW PRICE + PROMOTION FIXED
        DISCOUNT NEW PRICE + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE + PROMOTION NEGATIVE
        DISCOUNT NEW PRICE HIGHER
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC FIXED
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC FIXED + PROMOTION FIXED
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC FIXED + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC FIXED + PROMOTION NEGATIVE
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC PERCENTAGE
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC PERCENTAGE + PROMOTION FIXED
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC PERCENTAGE + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE HIGHER + AUTOMATIC PERCENTAGE + PROMOTION NEGATIVE
        DISCOUNT NEW PRICE HIGHER + PROMOTION FIXED
        DISCOUNT NEW PRICE HIGHER + PROMOTION PERCENTAGE
        DISCOUNT NEW PRICE HIGHER + PROMOTION NEGATIVE

        No new base price
        ------------------------------------------------------------------------
        AUTOMATIC FIXED
        AUTOMATIC PERCENTAGE
        DISCOUNT FIXED
        DISCOUNT PERCENTAGE
        PROMOTION FIXED
        PROMOTION PERCENTAGE
        PROMOTION NEGATIVE
        AUTOMATIC FIXED + DISCOUNT FIXED
        AUTOMATIC FIXED + DISCOUNT FIXED + PROMOTION FIXED
        AUTOMATIC FIXED + DISCOUNT FIXED + PROMOTION PROMOTION PERCENTAGE
        AUTOMATIC FIXED + DISCOUNT FIXED + PROMOTION PROMOTION NEGATIVE
        AUTOMATIC FIXED + DISCOUNT PERCENTAGE
        AUTOMATIC FIXED + DISCOUNT PERCENTAGE + PROMOTION FIXED
        AUTOMATIC FIXED + DISCOUNT PERCENTAGE + PROMOTION PERCENTAGE
        AUTOMATIC FIXED + DISCOUNT PERCENTAGE + PROMOTION NEGATIVE
        AUTOMATIC FIXED + PROMOTION FIXED
        AUTOMATIC FIXED + PROMOTION PERCENTAGE
        AUTOMATIC FIXED + PROMOTION NEGATIVE
        AUTOMATIC PERCENTAGE + DISCOUNT FIXED
        AUTOMATIC PERCENTAGE + DISCOUNT FIXED + PROMOTION FIXED
        AUTOMATIC PERCENTAGE + DISCOUNT FIXED + PROMOTION PROMOTION PERCENTAGE
        AUTOMATIC PERCENTAGE + DISCOUNT FIXED + PROMOTION PROMOTION NEGATIVE
        AUTOMATIC PERCENTAGE + DISCOUNT PERCENTAGE
        AUTOMATIC PERCENTAGE + DISCOUNT PERCENTAGE + PROMOTION FIXED
        AUTOMATIC PERCENTAGE + DISCOUNT PERCENTAGE + PROMOTION PERCENTAGE
        AUTOMATIC PERCENTAGE + DISCOUNT PERCENTAGE + PROMOTION NEGATIVE
        AUTOMATIC PERCENTAGE + PROMOTION FIXED
        AUTOMATIC PERCENTAGE + PROMOTION PERCENTAGE
        AUTOMATIC PERCENTAGE + PROMOTION NEGATIVE
        DISCOUNT FIXED + PROMOTION FIXED
        DISCOUNT FIXED + PROMOTION PERCENTAGE
        DISCOUNT FIXED + PROMOTION NEGATIVE
        DISCOUNT PERCENTAGE + PROMOTION FIXED
        DISCOUNT PERCENTAGE + PROMOTION PERCENTAGE
        DISCOUNT PERCENTAGE+ PROMOTION NEGATIVE
         */

        //given:
        List<Double> generalBasePrices = Arrays.asList(12.0, 16.0, 10.0);
        List<Double> juniorBasePrices = Arrays.asList(6.0, 8.0, 5.0);
        List<PriceZoneConfigWithPrice> generalPriceZones = Arrays.asList(
                new PriceZoneConfigWithPrice(generalBasePrices.get(0), getPriceZoneConfig(1, "Platea")),
                new PriceZoneConfigWithPrice(generalBasePrices.get(1), getPriceZoneConfig(2, "VIP")),
                new PriceZoneConfigWithPrice(generalBasePrices.get(2), getPriceZoneConfig(3, "Anfiteatro")));
        List<PriceZoneConfigWithPrice> juniorPriceZones = Arrays.asList(
                new PriceZoneConfigWithPrice(juniorBasePrices.get(0), getPriceZoneConfig(1, "Platea")),
                new PriceZoneConfigWithPrice(juniorBasePrices.get(1), getPriceZoneConfig(2, "VIP")),
                new PriceZoneConfigWithPrice(juniorBasePrices.get(2), getPriceZoneConfig(3, "Anfiteatro")));

        ChannelEventSurcharges eventSurcharges = getEventSurchargesFixedValue(false);
        List<EventPromotionRecord> promotionsRecords = Arrays.asList(
                getAutomaticNBP(), getAutomaticNBPHigher(), getAutomaticFixed(), getAutomaticPercentage(),
                getDiscountNBP(), getDiscountNBPHigher(), getDiscountFixed(), getDiscountPercentage(),
                getPromotionFixed(), getPromotionPercentage(), getPromotionNegative());

        List<PriceZoneRateVenueConfigCustomRecord> priceZoneRateVenueConfig = new ArrayList<>();
        priceZoneRateVenueConfig.addAll(getListOfPriceZoneRateVenueConfigCustomRecord(generalPriceZones, Arrays.asList(rates.get(0)), venueConfig));
        priceZoneRateVenueConfig.addAll(getListOfPriceZoneRateVenueConfigCustomRecord(juniorPriceZones, Arrays.asList(rates.get(1)), venueConfig));

        //when:
        List<VenueConfigPricesSimulation> actual = getSimulations(eventSurcharges, promotionsRecords, priceZoneRateVenueConfig);

        //then:
        List<VenueConfigPricesSimulation> expected = Collections.singletonList(loadJson(RESOURCE_PATH + "simulationExpectedPrices.json", VenueConfigPricesSimulation.class));

        assertNotNull(actual);
        validateListOfVenueConfigPricesSimulation(actual, expected);

    }

    @Test
    void setSaleRequestIdGetPriceSimulationSaleRequestIdReturnsOK() {
        CpanelEventoCanalRecord eventChannel = new CpanelEventoCanalRecord();
        eventChannel.setIdevento(1);
        eventChannel.setIdcanal(1);
        eventChannel.setTaxonomiapropia(1);
        eventChannel.setAplicarrecargoscanalespecificos((byte) 1);

        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setUsafechasevento((byte) 1);
        channelEvent.setFechapublicacion(toTimestamp(TIME_STAMP));
        channelEvent.setFechaventa(toTimestamp(TIME_STAMP));
        channelEvent.setFechafin(toTimestamp(TIME_STAMP));

        PriceZoneRateVenueConfigCustomRecord priceZoneRateVenueConfigCustomRecord = new PriceZoneRateVenueConfigCustomRecord();
        ChannelEventSurcharges surcharges = ObjectRandomizer.random(ChannelEventSurcharges.class);
        List<EventPromotionRecord> promotionsRecords = Arrays.asList(
                getAutomaticNBP(), getAutomaticNBPHigher(), getAutomaticFixed(), getAutomaticPercentage(),
                getDiscountNBP(), getDiscountNBPHigher(), getDiscountFixed(), getDiscountPercentage(),
                getPromotionFixed(), getPromotionPercentage(), getPromotionNegative());

        doReturn(eventChannel).when(eventChannelDao).findById(anyInt());
        doReturn(Optional.of(channelEvent)).when(channelEventDao).getChannelEvent(anyInt(), anyInt());
        doReturn(List.of(priceZoneRateVenueConfigCustomRecord)).when(assignmentPriceZoneDao).getPriceZonesRatesGroupSalesVenueConfigByEventId(anyInt());
        doReturn(surcharges).when(surchargeService).getSurchargeRangesByChannelEventRelationShips(any(), any());
        doReturn(promotionsRecords).when(eventPromotionTemplateDao).getPromotionsByEventId(anyInt());

        List<VenueConfigPricesSimulation> actual = priceEngineSimulationService.getPricesSimulationBySaleRequestId(1L);

        assertDoesNotThrow(() -> actual);
    }

    private List<VenueConfigPricesSimulation> getMockConfig(ChannelEventSurcharges eventSurcharges, List<EventPromotionRecord> promotionsRecord) {
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        CpanelCanalEventoRecord channelEvent = getChannelEventRecord(false);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.of(channelEvent));
        Mockito.when(assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventChannel.getIdevento())).thenReturn(listEventConfig);
        Mockito.when(eventPromotionTemplateDao.getPromotionsByEventId(eventChannel.getIdevento())).thenReturn(promotionsRecord);
        Mockito.when(surchargeService.getSurchargeRangesByChannelEventRelationShips(any(CpanelCanalEventoRecord.class), any(CpanelEventoCanalRecord.class))).thenReturn(eventSurcharges);
        return priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID);
    }

    private List<VenueConfigPricesSimulation> getSimulations(ChannelEventSurcharges eventSurcharges, List<EventPromotionRecord> promotionsRecord,
                                                             List<PriceZoneRateVenueConfigCustomRecord> priceZoneRateVenueConfigCustomRecord) {
        Mockito.when(eventChannelDao.findById(SALE_REQUEST_ID.intValue())).thenReturn(eventChannel);
        CpanelCanalEventoRecord channelEvent = getChannelEventRecord(false);
        Mockito.when(channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento())).thenReturn(Optional.of(channelEvent));
        Mockito.when(assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventChannel.getIdevento())).thenReturn(priceZoneRateVenueConfigCustomRecord);
        Mockito.when(eventPromotionTemplateDao.getPromotionsByEventId(eventChannel.getIdevento())).thenReturn(promotionsRecord);
        Mockito.when(surchargeService.getSurchargeRangesByChannelEventRelationShips(any(CpanelCanalEventoRecord.class), any(CpanelEventoCanalRecord.class))).thenReturn(eventSurcharges);
        return priceEngineSimulationService.getPricesSimulationBySaleRequestId(SALE_REQUEST_ID);
    }

    private void updateExpectedRemovePromotionByRate(VenueConfigPricesSimulation venueConfigPricesSimulation, int rateId) {
        venueConfigPricesSimulation.getRates()
                .stream()
                .filter(r -> r.getId().intValue() == rateId)
                .forEach(rate -> {
                    rate.getPriceTypes().forEach(pz -> pz.getSimulations().remove(1));
                });
    }


    private void setTotalsDefault(List<PriceType> priceTypes, List<Double> totals) {
        for (int i = 0; i < priceTypes.size(); i++) {
            priceTypes.get(i).getSimulations().get(0).getPrice().setTotal(totals.get(i));
        }
    }

    private void setBasesAndTotals(List<PriceType> priceTypes, List<Double> totals, List<Double> bases, int simulationIndex) {
        for (int i = 0; i < priceTypes.size(); i++) {
            setBaseAndTotal(priceTypes.get(i).getSimulations(), totals.get(i), bases.get(i), simulationIndex);
        }
    }

    private void setBaseAndTotal(List<PriceSimulation> simulations, double total, double base, int simulationIndex) {
        simulations.get(simulationIndex).getPrice().setBase(base);
        simulations.get(simulationIndex).getPrice().setTotal(total);
    }

    private <T> T loadJson(String fileName, Class<T> obj) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        return JsonMapper.jacksonMapper().readValue(file, obj);
    }


    private Timestamp toTimestamp(String dateString) {
        return dateString == null ? null : Timestamp.from(ZonedDateTime.parse(dateString).toInstant());
    }

    public static ChannelEventSurcharges getEventSurcharges() {
        SurchargeRange channelSurchargeRange = new SurchargeRange();
        channelSurchargeRange.setFrom(83.9d);
        channelSurchargeRange.setTo(100.1);
        channelSurchargeRange.setFixedValue(CHANNEL_SURCHARGE_VALUE);
        SurchargeRanges channelSurchargeRanges = new SurchargeRanges();
        channelSurchargeRanges.setMain(List.of(channelSurchargeRange));
        channelSurchargeRanges.setPromotion(List.of(channelSurchargeRange));

        SurchargeRange promoterSurchargeRange = new SurchargeRange();
        promoterSurchargeRange.setFrom(83.9d);
        promoterSurchargeRange.setTo(100.1);
        promoterSurchargeRange.setFixedValue(PROMOTER_SURCHARGE_VALUE);
        SurchargeRanges promoterSurchargeRanges = new SurchargeRanges();
        promoterSurchargeRanges.setMain(List.of(promoterSurchargeRange));
        promoterSurchargeRanges.setPromotion(List.of(promoterSurchargeRange));

        ChannelEventSurcharges surcharges = new ChannelEventSurcharges();
        surcharges.setChannel(channelSurchargeRanges);
        surcharges.setPromoter(promoterSurchargeRanges);
        return surcharges;
    }


}
