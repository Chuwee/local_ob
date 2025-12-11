package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.CommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CatalogCommunicationElementConverter {


    private CatalogCommunicationElementConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<CatalogCommunicationElementDTO> convert(Event event, String s3Repository) {
        return convert(event.getCommunicationElements(), getEventImageResolver(event, s3Repository));
    }

    private static S3URLResolver getEventImageResolver(Event event, String s3Repository) {
        return S3URLResolver.builder()
                .withUrl(s3Repository)
                .withEntityId(event.getEntityId())
                .withOperatorId(event.getOperatorId())
                .withEventId(event.getEventId())
                .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                .build();
    }

    public static List<CatalogCommunicationElementDTO> convert(List<? extends CommunicationElement> communicationElements, S3URLResolver resolver) {
        if (CollectionUtils.isEmpty(communicationElements)) {
            return Collections.emptyList();
        }
        return communicationElements.stream().map(s -> CatalogCommunicationElementConverter.convert(s, resolver))
                .collect(Collectors.toList());
    }

    public static List<CatalogCommunicationElementDTO> convert(List<ChannelEventCommunicationElement> communicationElements, Integer operatorId, String s3Repository) {
        if (CollectionUtils.isEmpty(communicationElements)) {
            return Collections.emptyList();
        }

        return communicationElements.stream()
                .filter(comm -> comm.getItemId() != null)
                .map(comm -> convert(comm, S3URLResolver.builder()
                        .withUrl(s3Repository)
                        .withType(S3URLResolver.S3ImageType.ITEM_IMAGE)
                        .withItemId(comm.getItemId())
                        .withOperatorId(operatorId)
                        .build()))
                .collect(Collectors.toList());
    }

    private static CatalogCommunicationElementDTO convert(ChannelEventCommunicationElement comm, S3URLResolver resolver) {
        if (Objects.isNull(comm)) {
            return null;
        }
        CatalogCommunicationElementDTO dto = new CatalogCommunicationElementDTO();
        dto.setTag(comm.getType() != null ? comm.getType().name() : null);
        dto.setLanguage(comm.getLanguageCode());
        if (comm.getType().isImage()) {
            dto.setValue(StringUtils.isNotBlank(comm.getValue()) ? resolver.buildPath(comm.getValue()) : null);
            dto.setAltText(comm.getAltText());
        } else {
            dto.setValue(comm.getValue());
        }
        dto.setLinkUrl(comm.getLinkUrl());
        dto.setPosition(comm.getPosition());
        return dto;
    }

    private static CatalogCommunicationElementDTO convert(CommunicationElement communicationElement, S3URLResolver resolver) {
        if (Objects.isNull(communicationElement)) {
            return null;
        }
        CatalogCommunicationElementDTO dto = new CatalogCommunicationElementDTO();
        dto.setTagId(communicationElement.getTagId());
        dto.setLanguage(communicationElement.getLanguageCode());
        if (StringUtils.isNotBlank(communicationElement.getUrl())) {
            dto.setValue(communicationElement.getUrl());
            dto.setAltText(communicationElement.getAltText());
        } else {
            dto.setValue(checkImageAndResolve(communicationElement.getTagId(), communicationElement.getValue(), resolver));
            dto.setAltText(communicationElement.getAltText());
        }
        dto.setPosition(communicationElement.getPosition());
        dto.setTag(communicationElement.getTag());
        return dto;
    }

    private static String checkImageAndResolve(final Integer tagId, String value, S3URLResolver resolver) {
        EventTagType tag = EventTagType.getTagTypeById(tagId);
        if (Objects.nonNull(tag) && tag.isImage() && StringUtils.isNotBlank(value)) {
            return resolver.buildPath(value);
        }
        return value;
    }
}
