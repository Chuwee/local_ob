package es.onebox.event.priceengine.simulation.util;

import es.onebox.event.priceengine.simulation.converter.VenueConfigMapConverter;
import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.PromotionsCombines;
import es.onebox.event.priceengine.simulation.domain.Rate;
import es.onebox.event.priceengine.simulation.domain.RateMap;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.event.priceengine.simulation.domain.VenueConfigBase;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.priceengine.simulation.domain.enums.PromotionType;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimulationUtils {

    private SimulationUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static List<VenueConfigPricesSimulation> simulatePrices(List<PriceZoneRateVenueConfigCustomRecord> result,
                                                                   ChannelEventSurcharges surcharges,
                                                                   List<EventPromotionRecord> promotionsRecord,
                                                                   Integer channelId,
                                                                   boolean eventChannelUseSpecificChannelSurcharges) {
        List<EventPromotionRecord> promotionsRecordFiltered = PromotionUtils.filterByChannelId(promotionsRecord, channelId);
        PromotionsCombines combines = PromotionCombinatorUtils.combinations(promotionsRecordFiltered);
        Map<Integer, VenueConfigMap> venueConfigMap = VenueConfigMapConverter.convertToVenueConfigMap(result);
        List<VenueConfigPricesSimulation> venueConfigPricesSimulation = new ArrayList<>();
        venueConfigMap.forEach( (k,v) -> venueConfigPricesSimulation.add(simulate(v, surcharges, combines, eventChannelUseSpecificChannelSurcharges, null)));
        return venueConfigPricesSimulation;
    }

    public static List<VenueConfigPricesSimulation> simulatePrices(Map<Integer, VenueConfigMap> venueConfigMap,
                                                                   ChannelEventSurcharges surcharges,
                                                                   List<EventPromotionRecord> promotionsRecord,
                                                                   Integer channelId,
                                                                   boolean eventChannelUseSpecificChannelSurcharges,
                                                                   SessionTaxes taxes) {
        List<EventPromotionRecord> promotionsRecordFiltered = PromotionUtils.filterByChannelId(promotionsRecord, channelId);
        PromotionsCombines combines = PromotionCombinatorUtils.combinations(promotionsRecordFiltered);
        List<VenueConfigPricesSimulation> venueConfigPricesSimulation = new ArrayList<>();
        venueConfigMap.forEach( (k,v) -> venueConfigPricesSimulation.add(simulate(v, surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes)));
        return venueConfigPricesSimulation;
    }



    private static VenueConfigPricesSimulation simulate(VenueConfigMap venueConfigMap,
                                                        ChannelEventSurcharges surcharges,
                                                        PromotionsCombines combines,
                                                        boolean eventChannelUseSpecificChannelSurcharges,
                                                        SessionTaxes taxes) {
        VenueConfigPricesSimulation simulationDTO = new VenueConfigPricesSimulation();
        simulationDTO.setVenueConfig(getVenueConfig(venueConfigMap));
        simulationDTO.setRates(getRates(venueConfigMap.getRate(), surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes));
        return simulationDTO;
    }

    private static VenueConfigBase getVenueConfig(VenueConfigMap venueConfigMap) {
        VenueConfigBase venueConfigDto = new VenueConfigBase();
        venueConfigDto.setId(venueConfigMap.getId());
        venueConfigDto.setName(venueConfigMap.getName());
        return venueConfigDto;
    }

    private static List<Rate> getRates(Map<Integer, RateMap> rateMap, ChannelEventSurcharges surcharges,
                                       PromotionsCombines combines, boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        List<Rate> rates = new ArrayList<>();
        rateMap.forEach((k,v) -> rates.add(getRate(v, surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes)));
        return rates;
    }

    private static Rate getRate(RateMap rate, ChannelEventSurcharges surcharges, PromotionsCombines combines,
                                boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        PromotionsCombines combinesFiltered = filterCombinesByRateId(rate.getId(), combines);
        Rate rateDto = new Rate();
        rateDto.setId(rate.getId().longValue());
        rateDto.setName(rate.getName());
        rateDto.setPriceTypes(getPriceTypes(rate.getPriceZones(), surcharges, combinesFiltered, eventChannelUseSpecificChannelSurcharges, taxes));
        return rateDto;
    }

    public static PromotionsCombines filterCombinesByRateId(Integer rateId, PromotionsCombines combines) {
        PromotionsCombines combinesFiltered = new PromotionsCombines();
        List<List<Promotion>> result = combines
                .stream()
                .map(listPromo -> getPromosFilteredByRateId(rateId, listPromo))
                .filter(CollectionUtils::isNotEmpty).toList();
        combinesFiltered.addAll(result);
        return combinesFiltered;
    }

    private static List<Promotion> getPromosFilteredByRateId(Integer rateId, List<Promotion> listPromo) {
        List<Promotion> listFiltered = listPromo.stream().map(promo -> {
            if (Objects.nonNull(promo.getSelectedRates())
                    && BooleanUtils.toBoolean(promo.getSelectedRates())
                    && !promo.getRates().contains(rateId)){
                return null;
            }
            return promo;
        }).filter(Objects::nonNull).toList();
        return listFiltered.size() == listPromo.size() ? listPromo : null;
    }

    private static List<PriceType> getPriceTypes(List<PriceZone> priceZones, ChannelEventSurcharges surcharges,
                                                 PromotionsCombines combines, boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        return priceZones.stream()
                .map(pt -> getPriceType(pt, surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes))
                .collect(Collectors.toList());
    }

    private static PriceType getPriceType(PriceZone priceZone, ChannelEventSurcharges surcharges, PromotionsCombines combines,
                                          boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        PriceType priceType = new PriceType();
        priceType.setId(priceZone.getId());
        priceType.setName(priceZone.getConfig().getDescription());
        priceType.setSimulations(getPriceSimulations(priceZone, surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes));
        return priceType;
    }

    public static List<PriceSimulation> getPriceSimulations(PriceZone item, ChannelEventSurcharges surcharges,
                                                             PromotionsCombines combines, boolean eventChannelUseSpecificChannelSurcharges,
                                                             SessionTaxes taxes) {
        List<PriceSimulation> results = new ArrayList<>();
        results.add(getDefaultPricesZoneSimulation(item, surcharges, eventChannelUseSpecificChannelSurcharges, taxes));
        results.addAll(getListPriceZonesSimulation(item, surcharges, combines, eventChannelUseSpecificChannelSurcharges, taxes));
        return results;
    }

    private static PriceSimulation getDefaultPricesZoneSimulation(PriceZone priceZone, ChannelEventSurcharges surcharges,
                                                                  boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        PriceSimulation simulation = new PriceSimulation();
        simulation.setPrice(PricesUtils.getPrice(priceZone, surcharges, eventChannelUseSpecificChannelSurcharges, taxes));
        return simulation;
    }

    private static List<PriceSimulation> getListPriceZonesSimulation(PriceZone priceZone, ChannelEventSurcharges surcharges, PromotionsCombines combines,
                                                                     boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        List<PriceSimulation> result = new ArrayList<>();
        PromotionsCombines combinesFiltered = filterCombinesByPriceZoneId(priceZone.getId().intValue(), combines);
        combinesFiltered.forEach(list
                -> result.add(getPriceZonesSimulation(priceZone, surcharges, list, eventChannelUseSpecificChannelSurcharges, taxes)));
        return result;
    }

    private static PromotionsCombines filterCombinesByPriceZoneId(Integer priceZoneId, PromotionsCombines combines) {
        PromotionsCombines combinesFiltered = new PromotionsCombines();
        List<List<Promotion>> result = combines
                .stream()
                .map(listPromo -> getPromosFilteredByPriceZoneId(priceZoneId, listPromo))
                .filter(CollectionUtils::isNotEmpty).collect(Collectors.toList());
        combinesFiltered.addAll(result);
        return combinesFiltered;
    }

    private static List<Promotion> getPromosFilteredByPriceZoneId(Integer priceZoneId, List<Promotion> listPromo) {
        List<Promotion> listFiltered = listPromo.stream().map(promo -> {
            if (Objects.nonNull(promo.getSelectedPriceZones())
                    && BooleanUtils.toBoolean(promo.getSelectedPriceZones())
                    && !promo.getPriceZones().contains(priceZoneId)){
                return null;
            } else {
                return promo;
            }
        } ).filter(Objects::nonNull).collect(Collectors.toList());
        return listFiltered.size() == listPromo.size() ? listPromo : null;
    }

    private static PriceSimulation getPriceZonesSimulation(PriceZone priceZone,
                                                           ChannelEventSurcharges surcharges,
                                                           List<Promotion> promotions,
                                                           boolean eventChannelUseSpecificChannelSurcharges,
                                                           SessionTaxes taxes) {
        PriceSimulation simulation = new PriceSimulation();
        simulation.setBasePromotions(convertToListPromotion(promotions));
        simulation.setPrice(PricesUtils.getPrice(priceZone, surcharges, promotions, eventChannelUseSpecificChannelSurcharges, taxes));
        return simulation;
    }

    private static List<BasePromotion> convertToListPromotion(List<Promotion> promotions) {
        return promotions.stream().map(SimulationUtils::convertToPromotion).collect(Collectors.toList());
    }

    private static BasePromotion convertToPromotion(Promotion promotion) {
        BasePromotion basePromotionDto = new BasePromotion();
        basePromotionDto.setId(promotion.getId().longValue());
        basePromotionDto.setName(promotion.getName());
        basePromotionDto.setType(PromotionType.getById(promotion.getSubtype()));
        return basePromotionDto;
    }

}
