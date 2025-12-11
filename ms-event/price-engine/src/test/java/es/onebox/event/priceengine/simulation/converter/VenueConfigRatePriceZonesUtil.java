package es.onebox.event.priceengine.simulation.converter;

import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfigWithPrice;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.Rate;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.VenueConfigBase;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.priceengine.simulation.domain.enums.PromotionType;
import es.onebox.event.priceengine.simulation.domain.enums.SurchargeType;
import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class VenueConfigRatePriceZonesUtil {
    private static final Integer EVENT_ID = 1;

    private static final Integer CHANNEL_ID = 1;
    private static final Integer CHANNEL_EVENT_ID = 1;

    private VenueConfigRatePriceZonesUtil() {throw new UnsupportedOperationException("Cannot instantiate util class");}

    public static List<PriceZoneRateVenueConfigCustomRecord> getListOfPriceZoneRateVenueConfigCustomRecord(
            List<PriceZoneConfigWithPrice> priceZones,
            List<CpanelTarifaRecord> rates,
            CpanelConfigRecintoRecord venueConfig) {

        List<PriceZoneRateVenueConfigCustomRecord> result = new ArrayList<>();
        for (CpanelTarifaRecord rate : rates) {
            priceZones.stream()
                    .map(pz -> getPriceZoneRateVenueConfigCustomRecord(pz.getPrice(), rate, pz.getPriceZoneConfig(), venueConfig))
                    .forEach(result::add);
        }

        return result;
    }

    public static CpanelConfigRecintoRecord getVenueConfig(Integer idConfig, String name) {
        CpanelConfigRecintoRecord venueConfig = new CpanelConfigRecintoRecord();
        venueConfig.setIdconfiguracion(idConfig);
        venueConfig.setNombreconfiguracion(name);
        return venueConfig;
    }

    public static CpanelZonaPreciosConfigRecord getPriceZoneConfig(int id, String description) {
        CpanelZonaPreciosConfigRecord priceZoneConfig = new CpanelZonaPreciosConfigRecord();
        priceZoneConfig.setIdzona(id);
        priceZoneConfig.setDescripcion(description);
        return priceZoneConfig;
    }

    public static CpanelTarifaRecord getRate(Integer id, String name) {
        CpanelTarifaRecord rate = new CpanelTarifaRecord();
        rate.setIdtarifa(id);
        rate.setNombre(name);
        return rate;
    }

    private static PriceZoneRateVenueConfigCustomRecord getPriceZoneRateVenueConfigCustomRecord(Double price,
                                                                                         CpanelTarifaRecord rate,
                                                                                         CpanelZonaPreciosConfigRecord priceZoneConfig,
                                                                                         CpanelConfigRecintoRecord venueConfig) {
        PriceZoneRateVenueConfigCustomRecord  item = new PriceZoneRateVenueConfigCustomRecord();
        item.setPrecio(price);
        item.setIdzona(priceZoneConfig.getIdzona());
        item.setRate(rate);
        item.setPriceZoneConfig(priceZoneConfig);
        item.setVenueConfig(venueConfig);
        return item;
    }

    public static CpanelEventoCanalRecord getEventChannelRecord(boolean applySpecificChannelSurcharges) {
        CpanelEventoCanalRecord eventChannelRecord = new CpanelEventoCanalRecord();
        eventChannelRecord.setIdevento(EVENT_ID);
        eventChannelRecord.setIdcanal(CHANNEL_ID);
        if (applySpecificChannelSurcharges) {
            eventChannelRecord.setAplicarrecargoscanalespecificos((byte) 1);
        }
        return eventChannelRecord;
    }

    public static CpanelCanalEventoRecord getChannelEventRecord(boolean allSaleGroups) {
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();
        record.setIdcanaleevento(CHANNEL_EVENT_ID);
        if (allSaleGroups) {
            record.setTodosgruposventa((byte) 1);
        }
        return record;
    }

    public static VenueConfigPricesSimulation getExpected(CpanelConfigRecintoRecord venueConfig,
                                                          List<CpanelTarifaRecord> rates,
                                                          List<PriceZoneConfigWithPrice> priceZones,
                                                          List<List<Promotion>> promotions) {
        VenueConfigPricesSimulation expected = new VenueConfigPricesSimulation();

        expected.setVenueConfig(getExpectedVenueConfig(venueConfig.getIdconfiguracion(), venueConfig.getNombreconfiguracion()));
        expected.setRates(getExpectedRates(rates, priceZones, promotions));

        return expected;
    }

    private static VenueConfigBase getExpectedVenueConfig(Integer venueId, String venueConfigName) {
        VenueConfigBase venueConfigBase = new VenueConfigBase();
        venueConfigBase.setId(venueId.longValue());
        venueConfigBase.setName(venueConfigName);
        return venueConfigBase;
    }

    private static List<Rate> getExpectedRates(List<CpanelTarifaRecord> rates,
                                               List<PriceZoneConfigWithPrice> priceZones,
                                               List<List<Promotion>> promotions) {
        return rates.stream().map(r -> getExpectedRate(r, priceZones, promotions)).collect(Collectors.toList());
    }

    private static Rate getExpectedRate(CpanelTarifaRecord cpanelTarifaRecord,
                                        List<PriceZoneConfigWithPrice> priceZones,
                                        List<List<Promotion>> promotions) {
        Rate rate = new Rate();
        rate.setId(cpanelTarifaRecord.getIdtarifa().longValue());
        rate.setName(cpanelTarifaRecord.getNombre());
        rate.setPriceTypes(getExpectedPriceZones(priceZones, promotions));
        return rate;
    }

    private static List<PriceType> getExpectedPriceZones(List<PriceZoneConfigWithPrice> priceZones,
                                                         List<List<Promotion>> promotions) {
        return priceZones.stream().map(pz -> getExpectedPriceZone(pz, promotions)).collect(Collectors.toList());
    }

    private static PriceType getExpectedPriceZone(PriceZoneConfigWithPrice priceZoneConfigWithPrice,
                                                  List<List<Promotion>> promotions) {
        PriceType priceType = new PriceType();
        priceType.setId(priceZoneConfigWithPrice.getPriceZoneConfig().getIdzona().longValue());
        priceType.setName(priceZoneConfigWithPrice.getPriceZoneConfig().getDescripcion());
        List<PriceSimulation> simulations = new ArrayList<>();
        simulations.add(getExpectedDefaultSimulations(priceZoneConfigWithPrice.getPrice()));
        if (CollectionUtils.isNotEmpty(promotions)) {
            simulations.addAll(getExpectedSimulations(priceZoneConfigWithPrice.getPrice(), promotions));
        }
        priceType.setSimulations(simulations);
        return priceType;
    }

    private static PriceSimulation getExpectedDefaultSimulations(Double basePrice) {
        PriceSimulation priceSimulation = new PriceSimulation();
        priceSimulation.setPrice(getExpectedDefaultBasePrice(basePrice));
        return priceSimulation;
    }

    private static List<PriceSimulation> getExpectedSimulations(Double basePrice,
                                                                       List<List<Promotion>> promotions) {
        List<PriceSimulation> priceSimulations = new ArrayList<>();
        for (List<Promotion> listPromos : promotions) {
            PriceSimulation priceSimulation = new PriceSimulation();
            priceSimulation.setPrice(getExpectedDefaultBasePrice(basePrice));
            priceSimulation.setBasePromotions(getExpectedListPromotions(listPromos));
            priceSimulations.add(priceSimulation);
        }
        return priceSimulations;
    }

    private static List<BasePromotion> getExpectedListPromotions(List<Promotion> listPromos) {
        return listPromos.stream().map(VenueConfigRatePriceZonesUtil::convertToExpectedPromo).collect(Collectors.toList());
    }

    private static BasePromotion convertToExpectedPromo(Promotion promotion) {
        BasePromotion basePromotion = new BasePromotion();
        basePromotion.setName(promotion.getName());
        basePromotion.setType(Arrays.stream(PromotionType.values())
                .filter(item -> promotion.getSubtype().equals(item.getId()))
                .findAny().orElse(null));
        return basePromotion;
    }

    private static Price getExpectedDefaultBasePrice(Double basePrice) {
        Price price = new Price();
        price.setBase(basePrice);
        price.setTotal(basePrice);
        return price;
    }

    public static void updateSurchargesExpected(VenueConfigPricesSimulation expected,
                                                ChannelEventSurcharges surcharges) {
        for (Rate rate : expected.getRates()) {
            for (PriceType pt : rate.getPriceTypes()) {
                for (PriceSimulation simulation : pt.getSimulations()) {
                    simulation.getPrice().setSurcharges(getExpectedSurcharges(simulation.getPrice().getBase(), surcharges));
                }
            }
        }
    }

    private static List<Surcharge> getExpectedSurcharges(Double base, ChannelEventSurcharges surcharges) {
        return Arrays.asList(getExpectedSurcharge(base, surcharges, true),
                            getExpectedSurcharge(base, surcharges, false));
    }

    private static Surcharge getExpectedSurcharge(Double base, ChannelEventSurcharges surcharges, boolean isPromoter) {
        Surcharge surcharge = new Surcharge();
        if (isPromoter) {
            surcharge.setValue(getValueSurcharge(base, surcharges.getPromoter()));
            surcharge.setType(SurchargeType.PROMOTER);
        } else {
            surcharge.setValue(getValueSurcharge(base, surcharges.getChannel()));
            surcharge.setType(SurchargeType.CHANNEL);
        }
        return surcharge;
    }

    private static Double getValueSurcharge(Double base, SurchargeRanges ranges ) {
        SurchargeRange range;
        if (CollectionUtils.isNotEmpty(ranges.getMain())) {
            range = getRange(base, ranges.getMain());
        } else {
            range = getRange(base, ranges.getPromotion());
        }
        return getValueRange(base, range);
    }

    private static SurchargeRange getRange(double base, List<SurchargeRange> surchargeRages) {
        return surchargeRages
                .stream()
                .filter(range -> base >= range.getFrom()
                        && base < range.getTo())
                .findAny()
                .orElse(null);
    }

    private static Double getValueRange(Double base, SurchargeRange surchargeRange) {
        Double surchargeValue = 0.0;
        if (Objects.nonNull(surchargeRange.getFixedValue())) {
            surchargeValue = surchargeRange.getFixedValue();
        }
        if (Objects.nonNull(surchargeRange.getPercentageValue())) {
            surchargeValue = base * surchargeRange.getPercentageValue() / 100;
        }
        if (Objects.nonNull(surchargeRange.getMaximumValue()) && surchargeValue > surchargeRange.getMaximumValue()) {
            surchargeValue = surchargeRange.getMaximumValue();
        } else if (Objects.nonNull(surchargeRange.getMinimumValue()) && surchargeValue < surchargeRange.getMinimumValue()) {
            surchargeValue = surchargeRange.getMinimumValue();
        }
        return surchargeValue;
    }

}
