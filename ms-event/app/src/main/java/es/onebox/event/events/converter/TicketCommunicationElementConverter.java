package es.onebox.event.events.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TicketCommunicationElementConverter {

    private TicketCommunicationElementConverter() {
        throw new UnsupportedOperationException();
    }

    public static List<TicketCommunicationElementDTO> fromRecords(
            Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records, Long operatorId,
            StaticDataContainer staticDataContainer) {
        return records.entrySet().stream().flatMap(r -> fromRecord(r, operatorId, staticDataContainer))
                .collect(Collectors.toList());
    }

    public static TicketCommunicationElementFilter buildFilter(Set<TicketCommunicationElementDTO> elements) {
        TicketCommunicationElementFilter filter = new TicketCommunicationElementFilter();
        Set<TicketCommunicationElementTagType> tags = elements.stream().map(TicketCommunicationElementDTO::getTag)
                .collect(Collectors.toSet());
        filter.setTags(EnumSet.copyOf(tags));
        return filter;
    }

    public static TicketCommunicationElementFilter buildFilter(TicketCommunicationElementTagType tag, Integer languageId) {
        TicketCommunicationElementFilter filter = new TicketCommunicationElementFilter();
        filter.setTags(EnumSet.of(tag));
        filter.setLanguageId(languageId);
        return filter;
    }

    public static TicketCommunicationElementFilter buildFilter(Integer languageId) {
        TicketCommunicationElementFilter filter = new TicketCommunicationElementFilter();
        filter.setLanguageId(languageId);
        return filter;
    }

    public static void setTagRecord(CpanelElementosComTicketRecord record, Integer itemId, TicketCommunicationElementTagType tag) {
        switch (tag) {
            case HEADER:
                record.setPathimagencabecera(itemId);
                break;
            case BODY:
                record.setPathimagencuerpo(itemId);
                break;
            case EVENT_LOGO:
                record.setPathimagenlogo(itemId);
                break;
            case BANNER_MAIN:
                record.setPathimagenbanner1(itemId);
                break;
            case BANNER_SECONDARY:
                record.setPathimagenbanner2(itemId);
                break;
            case BANNER_CHANNEL_LOGO:
                record.setPathimagenbanner3(itemId);
                break;
            case ADDITIONAL_DATA:
                record.setOtrosdatos(itemId);
                break;
            case TITLE:
                record.setSubtitulo1(itemId);
                break;
            case SUBTITLE:
                record.setSubtitulo2(itemId);
                break;
            case TERMS:
                record.setTerminos(itemId);
                break;
            default:
                break;
        }
    }

    public static Integer getTagItemId(CpanelElementosComTicketRecord record, TicketCommunicationElementTagType tag) {
        switch (tag) {
            case HEADER:
                return record.getPathimagencabecera();
            case BODY:
                return record.getPathimagencuerpo();
            case EVENT_LOGO:
                return record.getPathimagenlogo();
            case BANNER_MAIN:
                return record.getPathimagenbanner1();
            case BANNER_SECONDARY:
                return record.getPathimagenbanner2();
            case BANNER_CHANNEL_LOGO:
                return record.getPathimagenbanner3();
            case ADDITIONAL_DATA:
                return record.getOtrosdatos();
            case TITLE:
                return record.getSubtitulo1();
            case SUBTITLE:
                return record.getSubtitulo2();
            case TERMS:
                return record.getTerminos();
            default:
                return null;
        }
    }

    private static Stream<TicketCommunicationElementDTO> fromRecord(
            Map.Entry<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> record, final Long operatordId,
            StaticDataContainer staticDataContainer) {
        List<TicketCommunicationElementDTO> items = new ArrayList<>();
        record.getValue().forEach(desc -> items.add(convert(record.getKey(), desc, staticDataContainer, operatordId)));
        return items.stream();
    }

    private static TicketCommunicationElementDTO convert(final TicketCommunicationElementTagType tag, final CpanelDescPorIdiomaRecord record, final StaticDataContainer staticDataContainer,
                                                         final Long operatorId) {
        TicketCommunicationElementDTO out = new TicketCommunicationElementDTO();
        if (tag.isText()) {
            out.setValue(record.getDescripcion());
        } else {
            out.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.EVENT_TICKET_IMAGE)
                    .withItemId(record.getIditem())
                    .withOperatorId(operatorId)
                    .withLanguageId(record.getIdidioma())
                    .build()
                    .buildPath(record.getDescripcion()));
        }
        out.setLanguage(staticDataContainer.getLanguage(record.getIdidioma()));
        out.setId(record.getIditem().longValue());
        out.setTag(tag);
        out.setAltText(record.getAlttext());
        return out;
    }
}
