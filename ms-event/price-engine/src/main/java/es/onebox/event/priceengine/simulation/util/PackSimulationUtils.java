package es.onebox.event.priceengine.simulation.util;

import es.onebox.event.priceengine.packs.PackPriceTypeBase;
import es.onebox.event.priceengine.packs.PackPriceType;
import es.onebox.event.priceengine.packs.PackRateBase;
import es.onebox.event.priceengine.packs.PackRate;
import es.onebox.event.priceengine.packs.PackTaxes;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesBase;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfig;
import es.onebox.event.priceengine.simulation.domain.PromotionsCombines;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;

import java.util.List;


public class PackSimulationUtils {

    public static PackVenueConfigPricesSimulation simulatePackPrice(Long channelId,
                                                                    PackVenueConfigPricesBase venueConfigMap,
                                                                    ChannelEventSurcharges surcharges,
                                                                    List<EventPromotionRecord> promotionsRecords,
                                                                    PackTaxes taxes) {
        PromotionsCombines promotionCombines = getPromotionCombines(promotionsRecords, channelId);
        PackVenueConfigPricesSimulation simulation = new PackVenueConfigPricesSimulation();
        simulation.setVenueConfig(venueConfigMap.getVenueConfig());
        simulation.setRates(getRates(venueConfigMap.getRates(), surcharges, promotionCombines, taxes));
        simulation.setTaxes(taxes.getPriceTaxes());
        return simulation;
    }

    private static PromotionsCombines getPromotionCombines(List<EventPromotionRecord> promotionsRecords, Long channelId) {
        List<EventPromotionRecord> promotionsRecordFiltered = PromotionUtils.filterByChannelId(promotionsRecords, channelId.intValue());
        return PromotionCombinatorUtils.combinations(promotionsRecordFiltered);
    }

    private static List<PackRate> getRates(List<PackRateBase> rates, ChannelEventSurcharges surcharges,
                                           PromotionsCombines promotionCombines, PackTaxes taxes) {
        return rates.stream().map(rate -> getRate(rate, surcharges, promotionCombines, taxes)).toList();
    }

    private static PackRate getRate(PackRateBase sourceRate, ChannelEventSurcharges surcharges,
                                    PromotionsCombines promotionCombines, PackTaxes taxes) {
        PromotionsCombines promotionCombinesFiltered = SimulationUtils.filterCombinesByRateId(sourceRate.getId().intValue(), promotionCombines);
        PackRate resultRate = new PackRate();
        resultRate.setDefaultRate(sourceRate.isDefaultRate());
        resultRate.setId(sourceRate.getId());
        resultRate.setName(sourceRate.getName());
        resultRate.setPriceTypes(getPriceTypes(sourceRate.getPriceTypes(), surcharges, promotionCombinesFiltered, taxes));
        return resultRate;
    }

    private static List<PackPriceType> getPriceTypes(List<PackPriceTypeBase> priceTypes, ChannelEventSurcharges surcharges,
                                                     PromotionsCombines promotionCombines, PackTaxes taxes) {
        return priceTypes.stream().map(priceType -> getPriceType(priceType, surcharges, promotionCombines, taxes)).toList();
    }

    private static PackPriceType getPriceType(PackPriceTypeBase priceSource, ChannelEventSurcharges surcharges,
                                              PromotionsCombines promotionCombines, PackTaxes taxes) {

        PackPriceType priceResult = new PackPriceType();
        priceResult.setId(priceSource.getId());
        priceResult.setName(priceSource.getName());
        priceResult.setPrice(priceSource.getPrice());
        priceResult.setSimulations(SimulationUtils.getPriceSimulations(toPriceZone(priceSource), surcharges, promotionCombines,
                false, toSessionTaxes(taxes)));
        return priceResult;
    }

    private static SessionTaxes toSessionTaxes(PackTaxes taxes) {
        SessionTaxes sessionTaxes = new SessionTaxes();
        sessionTaxes.setPricesTaxes(toSessionTaxInfo(taxes.getPriceTaxes()));
        return sessionTaxes;
    }

    private static List<SessionTaxInfo> toSessionTaxInfo(List<TaxInfo> priceTaxes) {
        return priceTaxes.stream().map(taxInfo -> {
            SessionTaxInfo sessionTaxInfo = new SessionTaxInfo();
            sessionTaxInfo.setId(taxInfo.getId());
            sessionTaxInfo.setName(taxInfo.getName());
            sessionTaxInfo.setValue(taxInfo.getValue());
            return sessionTaxInfo;
        }).toList();
    }

    private static PriceZone toPriceZone(PackPriceTypeBase packPrice) {
        PriceZoneConfig priceZoneConfig = new PriceZoneConfig();
        priceZoneConfig.setDescription(packPrice.getName());

        PriceZone priceZone = new PriceZone();
        priceZone.setPrice(packPrice.getPrice().getTotal());
        priceZone.setId(packPrice.getId());
        priceZone.setConfig(priceZoneConfig);

        return priceZone;
    }

}
