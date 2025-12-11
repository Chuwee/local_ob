package es.onebox.event.events.converter;

import es.onebox.event.events.domain.B2BSeatPublishingConfig;
import es.onebox.event.events.domain.PublishedSeatPriceType;
import es.onebox.event.events.domain.SeatPriceTypesRelations;
import es.onebox.event.events.dto.B2BSeatPublishingConfigDTO;
import es.onebox.event.events.dto.PublishedSeatPriceTypeDTO;
import es.onebox.event.events.dto.SeatPriceTypesRelationsDTO;

import java.util.List;
import java.util.stream.Collectors;

public class B2BPublishingConfigConverter {

    public static B2BSeatPublishingConfigDTO fromMsEvent(B2BSeatPublishingConfig config) {
        B2BSeatPublishingConfigDTO dto = new B2BSeatPublishingConfigDTO();
        dto.setEnabled(config.isEnabled());
        dto.setPublishedSeatQuotaId(config.getPublishedSeatQuotaId());

        if (config.getPublishedSeatPriceType() != null) {
            PublishedSeatPriceTypeDTO publishedSeatPriceTypeDTO = new PublishedSeatPriceTypeDTO();
            publishedSeatPriceTypeDTO.setEnabled(config.getPublishedSeatPriceType().getEnabled());

            List<SeatPriceTypesRelationsDTO> seatPriceTypesRelationsDTOs = config.getPublishedSeatPriceType()
                    .getPriceTypesRelations().stream()
                    .map(B2BPublishingConfigConverter::convertToDtoSeatPriceTypeRelation)
                    .collect(Collectors.toList());

            publishedSeatPriceTypeDTO.setPriceTypesRelations(seatPriceTypesRelationsDTOs);
            dto.setPublishedSeatPriceType(publishedSeatPriceTypeDTO);
        }

        return dto;
    }

    public static B2BSeatPublishingConfig toMsEvent(Long eventId, Long channelId, Long venueTemplateId, B2BSeatPublishingConfigDTO dto) {
        B2BSeatPublishingConfig config = new B2BSeatPublishingConfig();
        config.setEventId(eventId);
        config.setChannelId(channelId);
        config.setVenueTemplateId(venueTemplateId);
        config.setEnabled(dto.isEnabled());
        config.setPublishedSeatQuotaId(dto.getPublishedSeatQuotaId());

        if (dto.getPublishedSeatPriceType() != null) {
            PublishedSeatPriceType publishedSeatPriceType = new PublishedSeatPriceType();
            publishedSeatPriceType.setEnabled(dto.getPublishedSeatPriceType().isEnabled());

            List<SeatPriceTypesRelations> seatPriceTypesRelations = dto.getPublishedSeatPriceType()
                    .getPriceTypesRelations().stream()
                    .map(B2BPublishingConfigConverter::convertToModelSeatPriceTypeRelation)
                    .collect(Collectors.toList());

            publishedSeatPriceType.setPriceTypesRelations(seatPriceTypesRelations);
            config.setPublishedSeatPriceType(publishedSeatPriceType);
        }

        return config;
    }

    private static SeatPriceTypesRelationsDTO convertToDtoSeatPriceTypeRelation(SeatPriceTypesRelations modelRelation) {
        SeatPriceTypesRelationsDTO dtoRelation = new SeatPriceTypesRelationsDTO();
        dtoRelation.setSourcePriceTypeId(modelRelation.getSourcePriceTypeId());
        dtoRelation.setTargetPriceTypeIds(modelRelation.getTargetPriceTypeIds());
        return dtoRelation;
    }

    private static SeatPriceTypesRelations convertToModelSeatPriceTypeRelation(SeatPriceTypesRelationsDTO dtoRelation) {
        SeatPriceTypesRelations modelRelation = new SeatPriceTypesRelations();
        modelRelation.setSourcePriceTypeId(dtoRelation.getSourcePriceTypeId());
        modelRelation.setTargetPriceTypeIds(dtoRelation.getTargetPriceTypeIds());
        return modelRelation;
    }
}