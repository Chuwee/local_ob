package es.onebox.event.catalog.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dao.couch.CatalogPrice;
import es.onebox.event.catalog.dao.couch.CatalogPriceSimulation;
import es.onebox.event.catalog.dao.couch.CatalogPriceTaxes;
import es.onebox.event.catalog.dao.couch.CatalogPriceType;
import es.onebox.event.catalog.dao.couch.CatalogRate;
import es.onebox.event.catalog.dao.couch.CatalogSurcharge;
import es.onebox.event.catalog.dao.couch.CatalogTaxesBreakdown;
import es.onebox.event.catalog.dao.couch.CatalogVenueConfigPricesSimulation;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.catalog.dao.couch.CatalogSessionTaxInfo;
import es.onebox.event.catalog.dto.CatalogTaxInfoDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceSimulationDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceTaxesDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceTypeDTO;
import es.onebox.event.catalog.dto.price.CatalogRateDTO;
import es.onebox.event.catalog.dto.price.CatalogSurchargeDTO;
import es.onebox.event.catalog.dto.price.CatalogTaxesBreakdownDTO;
import es.onebox.event.catalog.dto.price.CatalogVenueConfigPricesSimulationDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.Rate;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.taxes.domain.Taxes;
import es.onebox.event.priceengine.simulation.domain.VenueConfigBase;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.priceengine.taxes.domain.TaxBreakdown;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.pricesengine.dto.PromotionDTO;
import es.onebox.event.pricesengine.dto.enums.PromotionType;
import es.onebox.event.pricesengine.dto.enums.SurchargeType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class CatalogPriceSimulationConverter {

    private CatalogPriceSimulationConverter() {
        throw new UnsupportedOperationException("Cannot instantiate convert class");
    }

    public static CatalogVenueConfigPricesSimulationDTO convertToDTO(ChannelSessionPricesDocument doc, ChannelSessionAgency csa) {
        CatalogVenueConfigPricesSimulation simulation = doc.getSimulation();
        if (simulation == null) {
            return null;
        }
        CatalogVenueConfigPricesSimulationDTO result = new CatalogVenueConfigPricesSimulationDTO();
        result.setVenueConfig(simulation.getVenueConfig());
        result.setRates(toCatalogRatesDTO(simulation.getRates(), csa));
        result.setTaxes(toCatalogTaxInfo(doc.getTaxes()));
        result.setInvitationTaxes(toCatalogTaxInfo((doc.getInvitationTaxes())));
        result.setSurchargesTaxes(toCatalogTaxInfo(doc.getSurchargesTaxes()));
        result.setChannelSurchargesTaxes(toCatalogTaxInfo(doc.getChannelSurchargesTaxes()));
        return result;
    }

    public static CatalogVenueConfigPricesSimulation toCouchDoc(VenueConfigPricesSimulation item, Long defaultRateId) {
        if (isNull(item)) {
            return null;
        }
        CatalogVenueConfigPricesSimulation venueConfPricesSimulation = new CatalogVenueConfigPricesSimulation();
        venueConfPricesSimulation.setVenueConfig(toDTO(item.getVenueConfig()));
        venueConfPricesSimulation.setRates(toCouchDoc(item.getRates(), defaultRateId));
        return venueConfPricesSimulation;
    }

    private static List<CatalogTaxInfoDTO> toCatalogTaxInfo(List<CatalogSessionTaxInfo> taxes) {
        if (CollectionUtils.isEmpty(taxes)) {
            return null;
        }
        return taxes.stream()
                .map(t ->
                        TaxSimulationUtils.createTaxInfo(t.getId(), t.getValue(), t.getName(), CatalogTaxInfoDTO::new)
                ).toList();
    }

    private static IdNameDTO toDTO(VenueConfigBase venueConfig) {
        if (isNull(venueConfig)) {
            return null;
        }
        IdNameDTO idNameDTO = new IdNameDTO();
        idNameDTO.setId(venueConfig.getId());
        idNameDTO.setName(venueConfig.getName());
        return idNameDTO;
    }

    private static List<CatalogRate> toCouchDoc(List<Rate> rates, Long defaultRateId) {
        if (CollectionUtils.isEmpty(rates)) {
            return new ArrayList<>();
        }
        return rates.stream()
                .map(r -> CatalogPriceSimulationConverter.toCouchDoc(r, defaultRateId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<CatalogRateDTO> toCatalogRatesDTO(List<CatalogRate> source, ChannelSessionAgency csa) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        if (csa != null) {
            var priceMatrix = csa.getPrices();
            if (priceMatrix == null || CollectionUtils.isEmpty(priceMatrix.getPrices())) {
                throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_SESSION_PRICES_NOT_FOUND).build();
            }
            var priceZonePrices = priceMatrix.getPrices();
            Map<Long, List<Long>> ratesByPriceZone = new HashMap<>();
            priceZonePrices.forEach(pz -> pz.getRates().forEach(r -> {
                if (!ratesByPriceZone.containsKey(r.getId())) {
                    ratesByPriceZone.put(r.getId(), new ArrayList<>());
                }
                ratesByPriceZone.get(r.getId()).add(pz.getPriceZoneId());
            }));
            return source.stream()
                    .map(s -> CatalogPriceSimulationConverter.toDTO(s, ratesByPriceZone))
                    .filter(Objects::nonNull)
                    .collect(toList());
        }
        return source.stream()
                .map(CatalogPriceSimulationConverter::toDTO)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static CatalogRateDTO toDTO(CatalogRate source, Map<Long, List<Long>> ratesByPriceZone) {
        if (source == null) {
            return null;
        }
        Long rateId = source.getId();
        if (!ratesByPriceZone.containsKey(rateId)) {
            return null;
        }
        CatalogRateDTO result = new CatalogRateDTO();
        List<Long> priceZonesByAgency = ratesByPriceZone.get(rateId);
        List<CatalogPriceTypeDTO> priceTypes = convertToCatalogPriceTypeDTO(source.getPriceTypes(), priceZonesByAgency);
        result.setId(rateId);
        result.setName(source.getName());
        result.setDefaultRate(source.isDefaultRate());
        result.setPriceTypes(priceTypes);
        return result;
    }

    private static CatalogRate toCouchDoc(Rate source, Long defaultRateId) {
        if (isNull(source)) {
            return null;
        }
        CatalogRate catalogRate = new CatalogRate();
        catalogRate.setId(source.getId());
        catalogRate.setName(source.getName());
        catalogRate.setDefaultRate(defaultRateId.equals(source.getId()));
        catalogRate.setPriceTypes(toPriceTypesDTO(source.getPriceTypes()));
        return catalogRate;
    }

    private static CatalogRateDTO toDTO(CatalogRate source) {
        if (isNull(source)) {
            return null;
        }
        CatalogRateDTO result = new CatalogRateDTO();
        Long rateId = source.getId();
        List<CatalogPriceTypeDTO> priceTypes = convertToCatalogPriceTypeDTO(source.getPriceTypes());
        result.setId(rateId);
        result.setName(source.getName());
        result.setDefaultRate(source.isDefaultRate());
        result.setPriceTypes(priceTypes);
        return result;
    }

    private static List<CatalogPriceType> toPriceTypesDTO(List<PriceType> priceTypes) {
        if (CollectionUtils.isEmpty(priceTypes)) {
            return new ArrayList<>();
        }
        return priceTypes.stream()
                .map(CatalogPriceSimulationConverter::toCouchDoc)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<CatalogPriceTypeDTO> convertToCatalogPriceTypeDTO(List<CatalogPriceType> priceTypes) {
        if (CollectionUtils.isEmpty(priceTypes)) {
            return new ArrayList<>();
        }
        return priceTypes.stream()
                .map(CatalogPriceSimulationConverter::toDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<CatalogPriceTypeDTO> convertToCatalogPriceTypeDTO(List<CatalogPriceType> priceTypes,
                                                                          List<Long> priceZonesByAgency) {
        if (CollectionUtils.isEmpty(priceTypes) || CollectionUtils.isEmpty(priceZonesByAgency)) {
            return new ArrayList<>();
        }
        return priceTypes.stream()
                .map(s -> CatalogPriceSimulationConverter.toDTO(s, priceZonesByAgency))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private static CatalogPriceType toCouchDoc(PriceType source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPriceType result = new CatalogPriceType();
        result.setId(source.getId());
        result.setName(source.getName());
        Price unpromotedPrice = null;
        for (Iterator<PriceSimulation> i = source.getSimulations().iterator(); i.hasNext(); ) {
            PriceSimulation aPriceSimulation = i.next();
            if (CollectionUtils.isEmpty(aPriceSimulation.getBasePromotions())) {
                unpromotedPrice = aPriceSimulation.getPrice();
                i.remove();
                break;
            }
        }
        result.setPrice(toCouchDoc(unpromotedPrice));
        result.setSimulations(convertToCatalogPriceSimulation(source.getSimulations()));
        return result;
    }

    private static CatalogPriceTypeDTO toDTO(CatalogPriceType source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPriceTypeDTO result = new CatalogPriceTypeDTO();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setPrice(toDTO(source.getPrice()));
        result.setSimulations(convertToCatalogPriceSimulationDTO(source.getSimulations()));
        return result;
    }

    private static CatalogPriceTypeDTO toDTO(CatalogPriceType source, List<Long> priceZonesByAgency) {
        if (isNull(source) || CollectionUtils.isEmpty(priceZonesByAgency)) {
            return null;
        }
        Long id = source.getId();
        if (!priceZonesByAgency.contains(id)) {
            return null;
        }
        CatalogPriceTypeDTO result = new CatalogPriceTypeDTO();
        result.setId(id);
        result.setName(source.getName());
        result.setPrice(toDTO(source.getPrice()));
        result.setSimulations(convertToCatalogPriceSimulationDTO(source.getSimulations()));
        return result;
    }


    public static List<CatalogPriceSimulation> convertToCatalogPriceSimulation(List<PriceSimulation> simulations) {
        if (CollectionUtils.isEmpty(simulations)) {
            return new ArrayList<>();
        }
        return simulations.stream()
                .map(CatalogPriceSimulationConverter::toCouchDocument)
                .filter(Objects::nonNull).collect(toList());
    }

    static List<CatalogPriceSimulationDTO> convertToCatalogPriceSimulationDTO(List<CatalogPriceSimulation> simulations) {
        if (CollectionUtils.isEmpty(simulations)) {
            return new ArrayList<>();
        }
        return simulations.stream()
                .map(CatalogPriceSimulationConverter::toDTO)
                .filter(Objects::nonNull)
                .collect(toList());
    }


    private static CatalogPriceSimulation toCouchDocument(PriceSimulation source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPriceSimulation result = new CatalogPriceSimulation();
        result.setPrice(toCouchDoc(source.getPrice()));
        result.setPromotions(convertToListPromotionDto(source.getBasePromotions()));
        return result;
    }

    private static CatalogPriceSimulationDTO toDTO(CatalogPriceSimulation source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPriceSimulationDTO result = new CatalogPriceSimulationDTO();
        result.setPrice(toDTO(source.getPrice()));
        result.setPromotions(source.getPromotions());
        return result;
    }


    private static CatalogPrice toCouchDoc(Price source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPrice result = new CatalogPrice();
        result.setBase(source.getBase());
        result.setNet(source.getNet());
        result.setTaxes(toCatalogPriceTaxes(source.getTaxes()));
        result.setTotal(source.getTotal());
        result.setSurcharges(getListSurcharges(source.getSurcharges()));
        return result;
    }

    private static CatalogPriceTaxes toCatalogPriceTaxes(Taxes taxes) {
        if (taxes == null) {
            return null;
        }
        CatalogPriceTaxes catalogPriceTaxes = new CatalogPriceTaxes();
        catalogPriceTaxes.setTotal(taxes.getTotal());
        catalogPriceTaxes.setBreakdown(toCatalogTaxesBreakdown(taxes.getBreakdown()));
        return catalogPriceTaxes;
    }

    private static List<CatalogTaxesBreakdown> toCatalogTaxesBreakdown(List<TaxBreakdown> breakdown) {
        if (CollectionUtils.isEmpty(breakdown)) {
            return null;
        }
        return breakdown.stream()
                .map(t -> {
                    CatalogTaxesBreakdown taxInfo = new CatalogTaxesBreakdown();
                    taxInfo.setId(t.getId());
                    taxInfo.setAmount(t.getAmount());
                    return taxInfo;
                }).toList();
    }


    private static CatalogPriceDTO toDTO(CatalogPrice source) {
        if (isNull(source)) {
            return null;
        }
        CatalogPriceDTO result = new CatalogPriceDTO();
        result.setBase(source.getBase());
        result.setNet(source.getNet());
        result.setTaxes(toDTO(source.getTaxes()));
        result.setTotal(source.getTotal());
        result.setOriginal(source.getOriginal());
        result.setSurcharges(getListSurchargesDTO(source.getSurcharges()));
        return result;
    }

    private static CatalogPriceTaxesDTO toDTO(CatalogPriceTaxes taxes) {
        if (taxes == null) {
            return null;
        }
        CatalogPriceTaxesDTO catalogPriceTaxesDTO = new CatalogPriceTaxesDTO();
        catalogPriceTaxesDTO.setTotal(taxes.getTotal());
        catalogPriceTaxesDTO.setBreakdown(toDTO(taxes.getBreakdown()));
        return catalogPriceTaxesDTO;
    }

    private static List<CatalogTaxesBreakdownDTO> toDTO(List<CatalogTaxesBreakdown> breakdown) {
        if (CollectionUtils.isEmpty(breakdown)) {
            return null;
        }
        return breakdown.stream()
                .map(t -> {
                    CatalogTaxesBreakdownDTO taxInfo = new CatalogTaxesBreakdownDTO();
                    taxInfo.setId(t.getId());
                    taxInfo.setAmount(t.getAmount());
                    return taxInfo;
                }).toList();
    }


    private static List<CatalogSurcharge> getListSurcharges(List<Surcharge> surcharges) {
        if (CollectionUtils.isEmpty(surcharges)) {
            return new ArrayList<>();
        }
        return surcharges.stream()
                .map(CatalogPriceSimulationConverter::toCouchDoc)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static List<CatalogSurchargeDTO> getListSurchargesDTO(List<CatalogSurcharge> surcharges) {
        if (CollectionUtils.isEmpty(surcharges)) {
            return new ArrayList<>();
        }
        return surcharges.stream()
                .map(CatalogPriceSimulationConverter::toDTO)
                .filter(Objects::nonNull)
                .collect(toList());
    }


    private static CatalogSurcharge toCouchDoc(Surcharge surcharge) {
        if (isNull(surcharge)) {
            return null;
        }
        CatalogSurcharge surchargeDto = new CatalogSurcharge();
        surchargeDto.setType(convertToSurchargeTypeDto(surcharge.getType()));
        surchargeDto.setValue(surcharge.getValue());
        surchargeDto.setNet(surcharge.getNet());
        surchargeDto.setTaxes(toCatalogPriceTaxes(surcharge.getTaxes()));
        return surchargeDto;
    }

    private static CatalogSurchargeDTO toDTO(CatalogSurcharge source) {
        if (isNull(source)) {
            return null;
        }
        CatalogSurchargeDTO result = new CatalogSurchargeDTO();
        result.setType(source.getType());
        result.setValue(source.getValue());
        result.setNet(source.getNet());
        result.setTaxes(toDTO(source.getTaxes()));
        return result;
    }


    private static SurchargeType convertToSurchargeTypeDto(es.onebox.event.priceengine.simulation.domain.enums.SurchargeType type) {
        return Arrays.stream(SurchargeType.values())
                .filter(item -> item.name().equals(type.name()))
                .findAny().orElse(null);
    }

    private static List<PromotionDTO> convertToListPromotionDto(List<BasePromotion> basePromotions) {
        if (CollectionUtils.isEmpty(basePromotions)) {
            return null;
        }
        return basePromotions.stream()
                .map(CatalogPriceSimulationConverter::convertToPromotionDto)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static PromotionDTO convertToPromotionDto(BasePromotion basePromotion) {
        if (nonNull(basePromotion)) {
            PromotionDTO promotionDto = new PromotionDTO();
            promotionDto.setId(basePromotion.getId());
            promotionDto.setName(basePromotion.getName());
            promotionDto.setType(convertToPromotionTypeDto(basePromotion.getType()));
            return promotionDto;
        }
        return null;
    }

    private static PromotionType convertToPromotionTypeDto(es.onebox.event.priceengine.simulation.domain.enums.PromotionType type) {
        return Arrays.stream(PromotionType.values())
                .filter(item -> item.name().equals(type.name()))
                .findAny().orElse(null);
    }

}
