package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.events.dto.PriceTypeDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceDTO;
import es.onebox.mgmt.events.enums.PriceType;
import es.onebox.mgmt.events.enums.TicketType;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPriceRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketPricesConverter {

    private SeasonTicketPricesConverter() {
    }

    public static VenueTemplatePriceDTO fromMsVenueTemplatePrice(VenueTemplatePrice venueTemplatePrice) {
        VenueTemplatePriceDTO venueTemplatePriceDTO = new VenueTemplatePriceDTO();

        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(venueTemplatePrice.getPriceTypeId());
        priceTypeDTO.setCode(venueTemplatePrice.getPriceTypeCode());
        priceTypeDTO.setDescription(venueTemplatePrice.getPriceTypeDescription());
        venueTemplatePriceDTO.setPriceType(priceTypeDTO);

        IdNameDTO rateDTO = new IdNameDTO();
        rateDTO.setId(venueTemplatePrice.getRateId().longValue());
        rateDTO.setName(venueTemplatePrice.getRateName());
        venueTemplatePriceDTO.setRate(rateDTO);

        venueTemplatePriceDTO.setValue(venueTemplatePrice.getPrice());

        venueTemplatePriceDTO.setTicketType(fromMsEventPriceType(venueTemplatePrice.getPriceType()));

        return venueTemplatePriceDTO;
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
                .map(SeasonTicketPricesConverter::fromMsVenueTemplatePrice)
                .collect(Collectors.toList());
    }

    public static List<VenueTemplatePrice> toMsVenue(List<UpdateSeasonTicketPriceRequestDTO> prices) {
        return prices.stream().map(SeasonTicketPricesConverter::toMsVenue).collect(Collectors.toList());
    }

    public static VenueTemplatePrice toMsVenue(UpdateSeasonTicketPriceRequestDTO price) {
        VenueTemplatePrice venueTemplatePrice = new VenueTemplatePrice();

        venueTemplatePrice.setRateId(price.getRateId().intValue());
        venueTemplatePrice.setPriceTypeId(price.getPriceTypeId());
        venueTemplatePrice.setPrice(price.getValue());

        return venueTemplatePrice;
    }
}
