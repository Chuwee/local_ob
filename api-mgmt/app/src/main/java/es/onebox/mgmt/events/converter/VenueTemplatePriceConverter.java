package es.onebox.mgmt.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.events.dto.PriceTypeDTO;
import es.onebox.mgmt.events.dto.RateExtendedDTO;
import es.onebox.mgmt.events.dto.RateGroupBaseDTO;
import es.onebox.mgmt.events.dto.UpdateEventTemplatePriceRequestDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceBaseDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceExtendedDTO;
import es.onebox.mgmt.events.enums.PriceType;
import es.onebox.mgmt.events.enums.TicketType;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplatePriceConverter {

    private VenueTemplatePriceConverter() {
    }

    public static VenueTemplatePriceDTO fromMsVenueTemplatePrice(VenueTemplatePrice venueTemplatePrice) {
        VenueTemplatePriceDTO venueTemplatePriceDTO = new VenueTemplatePriceDTO();
        fromMsVenueTemplatePriceBase(venueTemplatePrice, venueTemplatePriceDTO);
        fillRate(venueTemplatePrice, venueTemplatePriceDTO);
        return venueTemplatePriceDTO;
    }

    public static VenueTemplatePriceExtendedDTO fromMsVenueTemplatePriceToExtended(VenueTemplatePrice venueTemplatePrice) {
        VenueTemplatePriceExtendedDTO venueTemplatePriceDTO = new VenueTemplatePriceExtendedDTO();
        fromMsVenueTemplatePriceBase(venueTemplatePrice, venueTemplatePriceDTO);
        fillRate(venueTemplatePrice, venueTemplatePriceDTO);
        return venueTemplatePriceDTO;
    }

    public static void fromMsVenueTemplatePriceBase(VenueTemplatePrice venueTemplatePrice, VenueTemplatePriceBaseDTO venueTemplatePriceBaseDTO) {
        fillPriceTypeDTO(venueTemplatePrice, venueTemplatePriceBaseDTO);
        venueTemplatePriceBaseDTO.setValue(venueTemplatePrice.getPrice());
        venueTemplatePriceBaseDTO.setTicketType(fromMsEventPriceType(venueTemplatePrice.getPriceType()));
    }

    private static void fillRate(VenueTemplatePrice venueTemplatePrice, VenueTemplatePriceDTO venueTemplatePriceDTO) {
        IdNameDTO rateDTO = new IdNameDTO();
        rateDTO.setId(venueTemplatePrice.getRateId().longValue());
        rateDTO.setName(venueTemplatePrice.getRateName());
        venueTemplatePriceDTO.setRate(rateDTO);
    }

    private static void fillRate(VenueTemplatePrice venueTemplatePrice, VenueTemplatePriceExtendedDTO venueTemplatePriceExtendedDTO) {
        RateExtendedDTO rateDTO = new RateExtendedDTO();
        rateDTO.setId(venueTemplatePrice.getRateId().longValue());
        rateDTO.setName(venueTemplatePrice.getRateName());
        if (venueTemplatePrice.getRateGroupId() != null) {
            RateGroupBaseDTO baseRateGroup = new RateGroupBaseDTO();
            baseRateGroup.setName(venueTemplatePrice.getRateGroupName());
            baseRateGroup.setId(venueTemplatePrice.getRateGroupId().longValue());
            rateDTO.setRateGroup(baseRateGroup);
        }
        venueTemplatePriceExtendedDTO.setRate(rateDTO);
    }

    private static void fillPriceTypeDTO(VenueTemplatePrice venueTemplatePrice, VenueTemplatePriceBaseDTO venueTemplatePriceDTO) {
        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(venueTemplatePrice.getPriceTypeId());
        priceTypeDTO.setCode(venueTemplatePrice.getPriceTypeCode());
        priceTypeDTO.setDescription(venueTemplatePrice.getPriceTypeDescription());
        venueTemplatePriceDTO.setPriceType(priceTypeDTO);
    }

    private static TicketType fromMsEventPriceType(PriceType priceType) {
        switch (priceType) {
            case INDIVIDUAL:
                return TicketType.INDIVIDUAL;
            case GROUP:
                return TicketType.GROUP;
            default:
                return TicketType.INDIVIDUAL;
        }
    }

    public static List<VenueTemplatePriceDTO> fromMsVenueTemplatePrices(List<VenueTemplatePrice> venueTemplatePrices) {
        return venueTemplatePrices.stream()
                .map(VenueTemplatePriceConverter::fromMsVenueTemplatePrice)
                .collect(Collectors.toList());
    }
    public static List<VenueTemplatePriceExtendedDTO> fromMsVenueTemplatePricesToExtended(List<VenueTemplatePrice> venueTemplatePrices) {
        return venueTemplatePrices.stream()
                .map(VenueTemplatePriceConverter::fromMsVenueTemplatePriceToExtended)
                .collect(Collectors.toList());
    }

    public static List<VenueTemplatePrice> toMsVenue(List<UpdateEventTemplatePriceRequestDTO> prices) {
        return prices.stream().map(VenueTemplatePriceConverter::toMsVenue).collect(Collectors.toList());
    }

    public static VenueTemplatePrice toMsVenue(UpdateEventTemplatePriceRequestDTO price) {
        VenueTemplatePrice venueTemplatePrice = new VenueTemplatePrice();

        if (price.getTicketType() != null) {
            venueTemplatePrice.setPriceType(PriceType.valueOf(price.getTicketType().name()));
        }
        venueTemplatePrice.setRateId(price.getRateId().intValue());
        venueTemplatePrice.setPriceTypeId(price.getPriceTypeId());
        venueTemplatePrice.setPrice(price.getValue());

        return venueTemplatePrice;
    }
}
