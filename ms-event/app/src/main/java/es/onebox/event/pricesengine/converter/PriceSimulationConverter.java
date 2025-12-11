package es.onebox.event.pricesengine.converter;

import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.Rate;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.VenueConfigBase;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.priceengine.simulation.domain.enums.PromotionType;
import es.onebox.event.priceengine.simulation.domain.enums.SurchargeType;
import es.onebox.event.pricesengine.dto.PriceDTO;
import es.onebox.event.pricesengine.dto.PriceSimulationDTO;
import es.onebox.event.pricesengine.dto.PriceTypeDTO;
import es.onebox.event.pricesengine.dto.PromotionDTO;
import es.onebox.event.pricesengine.dto.RateDTO;
import es.onebox.event.pricesengine.dto.SurchargeDTO;
import es.onebox.event.pricesengine.dto.VenueConfigBaseDTO;
import es.onebox.event.pricesengine.dto.VenueConfigPricesSimulationDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class PriceSimulationConverter {

    private PriceSimulationConverter(){throw new UnsupportedOperationException("Cannot instantiate convert class");}

    public static List<VenueConfigPricesSimulationDTO> convertToDto(List<VenueConfigPricesSimulation> list, Long currencyId) {

        if (CollectionUtils.isNotEmpty(list) && currencyId != null) {
            return list.stream()
                    .map(c -> convertToVenueConfigPriceSimulationDto(c, currencyId))
                    .filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static VenueConfigPricesSimulationDTO convertToVenueConfigPriceSimulationDto(
                                                    VenueConfigPricesSimulation item, Long currencyId) {
        if (nonNull(item) && currencyId != null) {
            VenueConfigPricesSimulationDTO venueConfPricesSimulationDto = new VenueConfigPricesSimulationDTO();
            venueConfPricesSimulationDto.setVenueConfig(convertToVenueVaseConfigDto(item.getVenueConfig()));
            venueConfPricesSimulationDto.setRates(convertToListRatesDto(item.getRates(), currencyId));
            return venueConfPricesSimulationDto;
        }
        return null;
    }

    private static VenueConfigBaseDTO convertToVenueVaseConfigDto(VenueConfigBase venueConfig) {
        if (nonNull(venueConfig)) {
            VenueConfigBaseDTO venueConfigBaseDto = new VenueConfigBaseDTO();
            venueConfigBaseDto.setId(venueConfig.getId());
            venueConfigBaseDto.setName(venueConfig.getName());
            return venueConfigBaseDto;
        }
        return null;
    }

    private static List<RateDTO> convertToListRatesDto(List<Rate> rates, Long currencyId) {
        if (CollectionUtils.isNotEmpty(rates)) {
            return rates.stream().map( c-> convertToRateDto(c, currencyId)).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static RateDTO  convertToRateDto(Rate rate, Long currencyId) {
        if (nonNull(rate)){
            RateDTO rateDto = new RateDTO();
            rateDto.setId(rate.getId());
            rateDto.setName(rate.getName());
            rateDto.setCurrencyId(currencyId);
            rateDto.setPriceTypes(convertToListPriceTypeDto(rate.getPriceTypes()));
            return rateDto;
        }
        return null;
    }

    private static List<PriceTypeDTO> convertToListPriceTypeDto(List<PriceType> priceTypes) {
        if (CollectionUtils.isNotEmpty(priceTypes)) {
            return priceTypes.stream().map(PriceSimulationConverter::convertToPriceTypeDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static PriceTypeDTO convertToPriceTypeDto(PriceType priceType) {
        if (nonNull(priceType)) {
            PriceTypeDTO priceTypeDto = new PriceTypeDTO();
            priceTypeDto.setId(priceType.getId());
            priceTypeDto.setName(priceType.getName());
            priceTypeDto.setSimulations(convertToListPriceSimulationDto(priceType.getSimulations()));
            return priceTypeDto;
        }
        return null;
    }

    private static List<PriceSimulationDTO> convertToListPriceSimulationDto(List<PriceSimulation> simulations) {
        if (CollectionUtils.isNotEmpty(simulations)) {
            return simulations.stream().map(PriceSimulationConverter::convertToPriceSimulationDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static PriceSimulationDTO convertToPriceSimulationDto(PriceSimulation priceSimulation) {
        if (nonNull(priceSimulation)) {
            PriceSimulationDTO priceSimulationDto = new PriceSimulationDTO();
            priceSimulationDto.setPrice(convertToPriceDto(priceSimulation.getPrice()));
            priceSimulationDto.setPromotions(convertToListPromotionDto(priceSimulation.getBasePromotions()));
            return priceSimulationDto;
        }
        return null;
    }

    private static PriceDTO convertToPriceDto(Price price) {
        if (nonNull(price)) {
            PriceDTO priceDto = new PriceDTO();
            priceDto.setBase(price.getBase());
            priceDto.setTotal(price.getTotal());
            priceDto.setSurcharges(getListSurchargesDto(price.getSurcharges()));
            return priceDto;
        }
        return null;
    }

    private static List<SurchargeDTO> getListSurchargesDto(List<Surcharge> surcharges) {
        if (CollectionUtils.isNotEmpty(surcharges)) {
            return surcharges.stream().map(PriceSimulationConverter::convertToSurchargeDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static SurchargeDTO convertToSurchargeDto(Surcharge surcharge) {
        if (nonNull(surcharge)) {
            SurchargeDTO surchargeDto = new SurchargeDTO();
            surchargeDto.setType(convertToSurchargeTypeDto(surcharge.getType()));
            surchargeDto.setValue(surcharge.getValue());
            return surchargeDto;
        }
        return null;
    }

    private static es.onebox.event.pricesengine.dto.enums.SurchargeType convertToSurchargeTypeDto(SurchargeType type) {
        return Arrays.stream(es.onebox.event.pricesengine.dto.enums.SurchargeType.values())
                .filter(item -> item.name().equals(type.name()))
                .findAny().orElse(null);
    }

    private static List<PromotionDTO> convertToListPromotionDto(List<BasePromotion> basePromotions) {
        if (CollectionUtils.isNotEmpty(basePromotions)) {
            return basePromotions.stream().map(PriceSimulationConverter::convertToPromotionDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
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

    private static es.onebox.event.pricesengine.dto.enums.PromotionType convertToPromotionTypeDto(PromotionType type) {
        return Arrays.stream(es.onebox.event.pricesengine.dto.enums.PromotionType.values())
                .filter(item -> item.name().equals(type.name()))
                .findAny().orElse(null);
    }

}
