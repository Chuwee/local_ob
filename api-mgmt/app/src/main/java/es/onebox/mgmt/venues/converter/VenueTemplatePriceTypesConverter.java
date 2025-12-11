package es.onebox.mgmt.venues.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.PriceTypeChannelContentTextType;
import es.onebox.mgmt.datasources.common.enums.CommunicationElementType;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.venues.dto.CreateVenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.PriceTypeAdditionalConfigDTO;
import es.onebox.mgmt.venues.dto.PriceTypeChannelContentsListDTO;
import es.onebox.mgmt.venues.dto.PriceTypeDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRestrictionsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplatePriceTypesConverter {

    private VenueTemplatePriceTypesConverter() {
    }

    public static PriceTypeDTO fromMsEvent(PriceType priceType) {
        if (priceType == null) {
            return null;
        }

        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(priceType.getId());
        priceTypeDTO.setName(priceType.getName());
        priceTypeDTO.setCode(priceType.getCode());
        priceTypeDTO.setColor(priceType.getColor());
        priceTypeDTO.setDefault(priceType.getDefault());
        priceTypeDTO.setPriority(priceType.getPriority());
        if (priceType.getAdditionalConfig() != null) {
            PriceTypeAdditionalConfigDTO priceTypeAdditionalConfigDTO = new PriceTypeAdditionalConfigDTO();
            priceTypeAdditionalConfigDTO.setGateId(priceType.getAdditionalConfig().getGateId());
            priceTypeAdditionalConfigDTO.setRestrictiveAccess(priceType.getAdditionalConfig().getRestrictiveAccess());
            priceTypeDTO.setPriceTypeAdditionalConfigDTO(priceTypeAdditionalConfigDTO);
        }

        return priceTypeDTO;
    }


    private static ChannelContentTextDTO<PriceTypeChannelContentTextType> fromMsVenue(PriceTypeCommunicationElement source) {
        if (source == null) {
            return null;
        }
        ChannelContentTextDTO<PriceTypeChannelContentTextType> target = new ChannelContentTextDTO<>();
        target.setLanguage(source.getLang());
        target.setValue(source.getValue());
        target.setType(PriceTypeChannelContentTextType.valueOf(source.getWebCommunicationElementType().name()));
        return target;
    }

    public static PriceTypeChannelContentsListDTO fromMsVenue(List<PriceTypeCommunicationElement> source) {
        if (source == null || source.isEmpty()) {
            return new PriceTypeChannelContentsListDTO();
        }
        PriceTypeChannelContentsListDTO target = new PriceTypeChannelContentsListDTO();
        target.addAll(source.stream()
                .map(VenueTemplatePriceTypesConverter::fromMsVenue)
                .collect(Collectors.toList()));
        return target;
    }

    private static PriceTypeCommunicationElement toMsVenue(ChannelContentTextDTO<PriceTypeChannelContentTextType> source) {
        if (source == null) {
            return null;
        }
        PriceTypeCommunicationElement target = new PriceTypeCommunicationElement();
        target.setLang(source.getLanguage());
        target.setValue(source.getValue());
        target.setWebCommunicationElementType(CommunicationElementType.valueOf(source.getType().name()));
        return target;
    }

    public static List<PriceTypeCommunicationElement> toMsVenue(PriceTypeChannelContentsListDTO source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(VenueTemplatePriceTypesConverter::toMsVenue)
                .collect(Collectors.toList());
    }

    public static VenueTemplatePriceTypeRestrictionDTO fromMsEvent(VenueTemplatePriceTypeRestriction source) {
        if (source == null) {
            return new VenueTemplatePriceTypeRestrictionDTO();
        }
        VenueTemplatePriceTypeRestrictionDTO target = new VenueTemplatePriceTypeRestrictionDTO();
        target.setLockedTicketsNumber(source.getLockedTickets());
        target.setRequiredTicketsNumber(source.getRequiredTickets());
        target.setRequiredPriceTypeIds(source.getRequiredPriceTypes());
        return target;
    }

    public static CreateVenueTemplatePriceTypeRestriction toCreateMsEvent(CreateVenueTemplatePriceTypeRestrictionDTO source) {
        CreateVenueTemplatePriceTypeRestriction createVenueTemplatePriceTypeRestriction = new CreateVenueTemplatePriceTypeRestriction();
        createVenueTemplatePriceTypeRestriction.setRequiredPriceTypeIds(source.getRequiredPriceTypeIds());
        createVenueTemplatePriceTypeRestriction.setLockedTicketsNumber(source.getLockedTicketsNumber());
        createVenueTemplatePriceTypeRestriction.setRequiredTicketsNumber(source.getRequiredTicketsNumber());
        return createVenueTemplatePriceTypeRestriction;
    }

    public static VenueTemplateRestrictionsDTO fromMsEvent(List<IdNameDTO> venueTemplateRestrictions) {
        VenueTemplateRestrictionsDTO venueTemplateRestrictionsDTO = new VenueTemplateRestrictionsDTO();
        Metadata metadata = new Metadata();
        if(venueTemplateRestrictions != null) {
            venueTemplateRestrictionsDTO.setData(venueTemplateRestrictions);
            metadata.setTotal((long) venueTemplateRestrictions.size());
        }
        metadata.setOffset(0L);
        metadata.setLimit(1000L);
        venueTemplateRestrictionsDTO.setMetadata(metadata);
        return venueTemplateRestrictionsDTO;
    }
}
