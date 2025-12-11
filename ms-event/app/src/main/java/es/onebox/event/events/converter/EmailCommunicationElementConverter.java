package es.onebox.event.events.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmailCommunicationElementConverter {

    private EmailCommunicationElementConverter() {
        throw new UnsupportedOperationException();
    }

    public static List<EmailCommunicationElementDTO> fromRecords(
            Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records, Long operatordId,
            StaticDataContainer staticDataContainer) {
        return records.entrySet().stream().flatMap(r -> fromRecord(r, operatordId, staticDataContainer))
                .collect(Collectors.toList());
    }
    
    public static EmailCommunicationElementFilter buildFilter(Set<EmailCommunicationElementDTO> elements) {
        EmailCommunicationElementFilter filter = new EmailCommunicationElementFilter();
        Set<EmailCommunicationElementTagType> tags = elements.stream().map(EmailCommunicationElementDTO::getTag)
                .collect(Collectors.toSet());
        filter.setTags(EnumSet.copyOf(tags));
        return filter;
    }
    
    public static EmailCommunicationElementFilter buildFilter(EmailCommunicationElementTagType tag , Integer languageId) {
        EmailCommunicationElementFilter filter = new EmailCommunicationElementFilter();
        filter.setTags(EnumSet.of(tag));
        filter.setLanguageId(languageId);
        return filter;
    }

    private static Stream<EmailCommunicationElementDTO> fromRecord(
            Map.Entry<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> record, final Long operatordId,
            StaticDataContainer staticDataContainer) {
        List<EmailCommunicationElementDTO> items = new ArrayList<>();
        record.getValue().forEach(desc -> items.add(convert(record.getKey(), desc, staticDataContainer, operatordId)));
        return items.stream();
    }

    private static EmailCommunicationElementDTO convert(final EmailCommunicationElementTagType tag,
            final CpanelDescPorIdiomaRecord record, final StaticDataContainer staticDataContainer,
            final Long operatorId) {
        EmailCommunicationElementDTO out = new EmailCommunicationElementDTO();
        out.setValue(S3URLResolver.builder()
                .withUrl(staticDataContainer.getS3Repository())
                .withType(S3URLResolver.S3ImageType.EVENT_TICKET_IMAGE)
                .withItemId(record.getIditem())
                .withOperatorId(operatorId)
                .withLanguageId(record.getIdidioma())
                .build()
                .buildPath(record.getDescripcion()));
        out.setLanguage(staticDataContainer.getLanguage(record.getIdidioma()));
        out.setId(record.getIditem().longValue());
        out.setTag(tag);
        out.setAltText(record.getAlttext());
        return out;
    }
}
