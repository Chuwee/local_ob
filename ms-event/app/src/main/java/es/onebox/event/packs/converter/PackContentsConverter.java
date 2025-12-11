package es.onebox.event.packs.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.ElementType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.packs.dto.PackCommunicationElementDTO;
import es.onebox.event.packs.dto.PackTicketContentDTO;
import es.onebox.event.packs.dto.PackTicketContentsDTO;
import es.onebox.event.packs.enums.PackTagType;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackContentsConverter {

    private PackContentsConverter() {
    }

    public static List<PackCommunicationElementDTO> fromComElementRecords(List<CpanelElementosComPackRecord> records,
                                                                          EntityDTO entity, Long packId,
                                                                          StaticDataContainer staticDataContainer) {
        List<PackCommunicationElementDTO> elementDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(records)) {
            return elementDTOS;
        }
        return records.stream()
                .filter(r -> r.getValor() != null)
                .map(element -> fromRecord(element, entity, packId, staticDataContainer)).collect(Collectors.toList());
    }

    public static PackCommunicationElementDTO fromRecord(CpanelElementosComPackRecord record, EntityDTO entity,
                                                         Long packId, StaticDataContainer staticDataContainer) {
        PackCommunicationElementDTO elementDTO = initComElement(record, staticDataContainer);
        PackTagType tagType = PackTagType.getTagTypeById(elementDTO.getTagId());
        if (tagType.isImage() && record.getValor() != null) {
            if (PackTagType.IMG_BANNER_WEB.equals(tagType)) {
                elementDTO.setPosition(record.getPosition());
            }
            elementDTO.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.PACK_IMAGE)
                    .withOperatorId(entity.getOperator().getId())
                    .withEntityId(entity.getId())
                    .withPackId(packId)
                    .build()
                    .buildPath(record.getValor()));
            elementDTO.setAltText(record.getAlttext());
        }
        return elementDTO;
    }

    public static PackTicketContentsDTO fromTicketRecords(Map<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> source,
                                                          Integer operatorId, StaticDataContainer staticDataContainer) {
        return source.entrySet()
                .stream()
                .flatMap(r -> fromTicketRecord(r, operatorId, staticDataContainer))
                .collect(Collectors.toCollection(PackTicketContentsDTO::new));
    }

    private static Stream<PackTicketContentDTO> fromTicketRecord(Map.Entry<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> record,
                                                                 Integer operatorId, StaticDataContainer staticDataContainer) {
        return record.getValue()
                .stream()
                .map(desc -> convert(record.getKey(), desc, operatorId, staticDataContainer));
    }

    private static PackTicketContentDTO convert(PackTicketContentTagType type, CpanelDescPorIdiomaRecord record,
                                                Integer operatorId, StaticDataContainer staticDataContainer) {
        PackTicketContentDTO target = new PackTicketContentDTO();
        target.setLanguage(staticDataContainer.getLanguage(record.getIdidioma()));
        target.setTag(type);
        if (ElementType.IMAGEN.equals(type.getElementType())) {
            target.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.ITEM_IMAGE)
                    .withItemId(record.getIditem())
                    .withOperatorId(operatorId)
                    .build()
                    .buildPath(record.getDescripcion()));
        } else {
            target.setValue(record.getDescripcion());
        }

        return target;
    }

    private static PackCommunicationElementDTO initComElement(CpanelElementosComPackRecord record, StaticDataContainer staticDataContainer) {
        PackCommunicationElementDTO dto = new PackCommunicationElementDTO();
        dto.setId(record.getIdelemento().longValue());
        dto.setTagId(record.getIdtag());
        dto.setLanguage(staticDataContainer.getLanguage(record.getIdioma()));
        dto.setValue(record.getValor());
        return dto;
    }

}
