package es.onebox.mgmt.events.eventchannel.b2b.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.B2BSeatPublishingConfig;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.PublishedSeatPriceType;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.SeatPriceTypesRelations;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.events.dto.PriceTypeDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigRequestDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.PublishedSeatPriceTypeDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.SeatPriceTypesRelationsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class B2BPublishingConfigConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(B2BPublishingConfigConverter.class);

    public B2BPublishingConfigConverter() {
    }

    public static B2BSeatPublishingConfigDTO fromMsEvent(B2BSeatPublishingConfig config, List<PriceType> priceTypes, List<Quota> quotas) {
        B2BSeatPublishingConfigDTO dto = new B2BSeatPublishingConfigDTO();
        dto.setEnabled(config.isEnabled());
        dto.setPublishedSeatQuota(findQuotaDTOById(quotas, config.getPublishedSeatQuotaId()));

        if (config.getPublishedSeatPriceType() != null) {
            PublishedSeatPriceTypeDTO publishedSeatPriceTypeDTO = new PublishedSeatPriceTypeDTO();
            publishedSeatPriceTypeDTO.setEnabled(config.getPublishedSeatPriceType().isEnabled());

            List<SeatPriceTypesRelationsDTO> relationsDTOList = config.getPublishedSeatPriceType().getPriceTypesRelations()
                    .stream()
                    .map(relation -> {
                        SeatPriceTypesRelationsDTO relationDTO = new SeatPriceTypesRelationsDTO();
                        relationDTO.setSourcePriceTypeId(convertToPriceTypeDTOById(priceTypes, relation.getSourcePriceTypeId()));
                        relationDTO.setTargetPriceTypeIds(relation.getTargetPriceTypeIds()
                                .stream()
                                .map(targetId -> convertToPriceTypeDTOById(priceTypes, targetId))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                        return relationDTO;
                    })
                    .collect(Collectors.toList());

            publishedSeatPriceTypeDTO.setSeatPriceTypesRelations(relationsDTOList);
            dto.setPublishedSeatPriceType(publishedSeatPriceTypeDTO);
        }
        return dto;
    }

    public static B2BSeatPublishingConfig toMsEvent(B2BSeatPublishingConfigRequestDTO dto) {
        B2BSeatPublishingConfig config = new B2BSeatPublishingConfig();
        config.setEnabled(dto.isEnabled());
        config.setPublishedSeatQuotaId(dto.getPublishedSeatQuotaId());

        if (dto.getPublishedSeatPriceType() != null) {
            PublishedSeatPriceType publishedSeatPriceType = new PublishedSeatPriceType();
            publishedSeatPriceType.setEnabled(dto.getPublishedSeatPriceType().getEnabled());

            List<SeatPriceTypesRelations> relationsList = dto.getPublishedSeatPriceType().getSeatPriceTypes()
                    .stream()
                    .map(relationDTO -> {
                        SeatPriceTypesRelations relation = new SeatPriceTypesRelations();
                        relation.setSourcePriceTypeId(relationDTO.getSourcePriceTypeId());
                        relation.setTargetPriceTypeIds(new ArrayList<>(relationDTO.getTargetPriceTypeIds()));
                        return relation;
                    })
                    .collect(Collectors.toList());

            publishedSeatPriceType.setPriceTypesRelations(relationsList);
            config.setPublishedSeatPriceType(publishedSeatPriceType);
        }
        return config;
    }

    private static PriceTypeDTO convertToPriceTypeDTOById(List<PriceType> priceTypes, Long id) {
        return Optional.ofNullable(priceTypes)
                .flatMap(list -> list.stream().filter(priceType -> id != null && id.equals(priceType.getId())).findFirst())
                .map(B2BPublishingConfigConverter::convertToPriceTypeDTO)
                .orElseGet(() -> {
                    LOGGER.warn("No PriceType found for ID: " + id);
                    return null;
                });
    }

    private static PriceTypeDTO convertToPriceTypeDTO(PriceType priceType) {
        PriceTypeDTO dto = new PriceTypeDTO();
        if (priceType != null) {
            dto.setId(priceType.getId());
            dto.setCode(priceType.getCode());
            dto.setDescription(priceType.getName());
        }
        return dto;
    }

    private static IdNameCodeDTO findQuotaDTOById(List<Quota> quotas, Long id) {
        return Optional.ofNullable(quotas)
                .flatMap(list -> list.stream().filter(quota -> id != null && id.equals(quota.getId())).findFirst())
                .map(B2BPublishingConfigConverter::convertToQuotaDTO)
                .orElseGet(() -> {
                    LOGGER.warn("No Quota found for ID: " + id);
                    return null;
                });
    }

    private static IdNameCodeDTO convertToQuotaDTO(Quota quota) {
        IdNameCodeDTO dto = new IdNameCodeDTO();
        if (quota != null) {
            dto.setId(quota.getId());
            dto.setCode(quota.getCode());
            dto.setName(quota.getName());
        }
        return dto;
    }
}
