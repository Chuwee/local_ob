package es.onebox.event.tickettemplates.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.ElementType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.tickettemplates.dto.CommunicationElementDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TicketTemplateCommunicationElementConverter {

    private TicketTemplateCommunicationElementConverter() {
    }

    public static List<CommunicationElementDTO> fromRecords(Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records,
                                                            TicketTemplateRecord ticketRecord, CpanelIdiomaPlantillaTicketRecord defaultLanguage,
                                                            StaticDataContainer staticDataContainer) {
        return records.entrySet().stream()
                .flatMap(r -> fromRecord(r, ticketRecord, defaultLanguage, staticDataContainer))
                .collect(Collectors.toList());
    }

    private static Stream<CommunicationElementDTO> fromRecord(Map.Entry<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> record,
                                                              TicketTemplateRecord ticketRecord, CpanelIdiomaPlantillaTicketRecord defaultLanguage,
                                                              StaticDataContainer staticDataContainer) {
        List<CommunicationElementDTO> items = new ArrayList<>();
        for (CpanelDescPorIdiomaRecord itemDesc : record.getValue()) {
            CommunicationElementDTO dto = initComElement(record.getKey(), itemDesc, staticDataContainer);
            if (ElementType.IMAGEN.equals(dto.getTagType().getElementType())) {
                dto.setValue(S3URLResolver.builder().withUrl(staticDataContainer.getS3Repository())
                        .withType(S3URLResolver.S3ImageType.ITEM_IMAGE).withOperatorId(ticketRecord.getOperatorId())
                        .withEntityId(ticketRecord.getIdentidad()).withLanguageId(defaultLanguage.getIdidioma())
                        .withItemId(itemDesc.getIditem()).build().buildPath(itemDesc.getDescripcion()));
            } else {
                dto.setValue(itemDesc.getDescripcion());
            }
            items.add(dto);
        }
        return items.stream();
    }

    private static CommunicationElementDTO initComElement(TicketTemplateTagType key, CpanelDescPorIdiomaRecord record, StaticDataContainer staticDataContainer) {
        CommunicationElementDTO dto = new CommunicationElementDTO();
        dto.setId(record.getIditem().longValue());
        dto.setTagType(key);
        dto.setLanguage(staticDataContainer.getLanguage(record.getIdidioma()));
        return dto;
    }

}


