package es.onebox.mgmt.salerequests.pricesimulation;

import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.Price;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.PriceSimulation;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.Promotion;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.Surcharge;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.VenueConfigBase;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.VenueConfigPricesSimulation;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceTypeDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PromotionDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.RateDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.SurchargeDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigBaseDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigPricesSimulationDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.PromotionTypeDTO;
import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.SurchargeTypeDTO;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class PriceSimulationConverter {

    private PriceSimulationConverter(){throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static List<VenueConfigPricesSimulationDTO> convertToListDto(List<VenueConfigPricesSimulation> venueConfigPricesSimulations, List<Currency> currencies) {
        if (isNotEmpty(venueConfigPricesSimulations)) {
            return venueConfigPricesSimulations.stream().map(vc -> convertToDto(vc, currencies)).toList();
        }
        return null;
    }

    private static VenueConfigPricesSimulationDTO convertToDto(VenueConfigPricesSimulation venueConfigPricesSimulation, List<Currency> currencies) {
        VenueConfigPricesSimulationDTO venueConfigPricesSimulationDTO = new VenueConfigPricesSimulationDTO();
        venueConfigPricesSimulationDTO.setVenueConfig(convertToVenueConfigDto(venueConfigPricesSimulation.getVenueConfig()));
        venueConfigPricesSimulationDTO.setRates(convertToListRateDto(venueConfigPricesSimulation.getRates(), currencies));
        return venueConfigPricesSimulationDTO;
    }

    private static VenueConfigBaseDTO convertToVenueConfigDto(VenueConfigBase venueConfig) {
        if (nonNull(venueConfig)) {
            VenueConfigBaseDTO venueConfigBase = new VenueConfigBaseDTO();
            venueConfigBase.setId(venueConfig.getId());
            venueConfigBase.setName(venueConfig.getName());
            return venueConfigBase;
        }
        return null;
    }

    private static List<RateDTO> convertToListRateDto(List<Rate> rates, List<Currency> currencies) {
        if (isNotEmpty(rates)) {
            return rates.stream().map( c -> convertToRateDto(c, currencies)).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static RateDTO convertToRateDto(Rate rate, List<Currency> currencies) {
        if (nonNull(rate)) {
            RateDTO rateDto = new RateDTO();
            rateDto.setId(rate.getId());
            rateDto.setName(rate.getName());
            if(rate.getCurrencyId() != null) {
                rateDto.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, rate.getCurrencyId()));
            }
            rateDto.setPriceTypes(convertToListPriceTypeDto(rate.getPriceTypes()));
            return rateDto;
        }
        return null;
    }

    private static List<PriceTypeDTO> convertToListPriceTypeDto(List<PriceType> priceTypes) {
        if (isNotEmpty(priceTypes)) {
            return priceTypes.stream().map(PriceSimulationConverter::convertToPriceTypeDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static PriceTypeDTO convertToPriceTypeDto(PriceType priceType) {
        if (nonNull(priceType)) {
            PriceTypeDTO priceTypeDto = new PriceTypeDTO();
            priceTypeDto.setId(priceType.getId());
            priceTypeDto.setName(priceType.getName());
            priceTypeDto.setSimulations(convertToListSimulationsDto(priceType.getSimulations()));
            return priceTypeDto;
        }
        return null;
    }

    private static List<PriceSimulationDTO> convertToListSimulationsDto(List<PriceSimulation> simulations) {
        if (isNotEmpty(simulations)) {
            return simulations.stream().map(PriceSimulationConverter::convertToSimulationDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static PriceSimulationDTO convertToSimulationDto(PriceSimulation priceSimulation) {
        if (nonNull(priceSimulation)) {
            PriceSimulationDTO priceSimulationDto = new PriceSimulationDTO();
            priceSimulationDto.setPrice(getPriceDto(priceSimulation.getPrice()));
            priceSimulationDto.setPromotions(convertToListPromotionDto(priceSimulation.getPromotions()));
            return priceSimulationDto;
        }
        return null;
    }

    private static PriceDTO getPriceDto(Price price) {
        if (nonNull(price)) {
            PriceDTO priceDto = new PriceDTO();
            priceDto.setBase(price.getBase());
            priceDto.setTotal(price.getTotal());
            priceDto.setSurcharges(convertToListSurchargeDto(price.getSurcharges()));
            return priceDto;
        }
        return null;
    }

    private static List<SurchargeDTO> convertToListSurchargeDto(List<Surcharge> surcharges) {
        if (isNotEmpty(surcharges)) {
            return surcharges.stream().map(PriceSimulationConverter::convertToSurchargeDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static SurchargeDTO convertToSurchargeDto(Surcharge surcharge) {
        if (nonNull(surcharge)) {
            SurchargeDTO surchargeDto = new SurchargeDTO();
            surchargeDto.setValue(surcharge.getValue());
            surchargeDto.setType(SurchargeTypeDTO.valueOf(surcharge.getType().name()));
            return surchargeDto;
        }
        return null;
    }

    private static List<PromotionDTO> convertToListPromotionDto(List<Promotion> promotions) {
        if (isNotEmpty(promotions)) {
            return promotions.stream().map(PriceSimulationConverter::convertToPromotionDto).filter(Objects::nonNull).collect(toList());
        }
        return null;
    }

    private static PromotionDTO convertToPromotionDto(Promotion promotion) {
        if (nonNull(promotion)) {
            PromotionDTO promotionDto = new PromotionDTO();
            promotionDto.setId(promotion.getId());
            promotionDto.setName(promotion.getName());
            promotionDto.setType(PromotionTypeDTO.valueOf(promotion.getType().name()));
            return promotionDto;
        }
        return null;
    }

}

















































