package es.onebox.event.events.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoCanalRecord;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelEventCommunicationElementConverter {
    private ChannelEventCommunicationElementConverter() {
        throw new UnsupportedOperationException();
    }


    public static List<EventCommunicationElementDTO> fromRecords(List<CpanelElementosComEventoCanalRecord> records,
                                                                 EventChannelRecord channelEvent, StaticDataContainer staticDataContainer) {
        return records.stream().filter(r -> r.getValor() != null)
                .map(r -> fromRecord(r, channelEvent, staticDataContainer))
                .collect(Collectors.toList());
    }

    private static EventCommunicationElementDTO fromRecord(CpanelElementosComEventoCanalRecord record, EventChannelRecord channelEvent,
                                                           StaticDataContainer staticDataContainer) {
        EventCommunicationElementDTO element = new EventCommunicationElementDTO();
        element.setId(record.getIdelemento().longValue());
        element.setTag(staticDataContainer.getTag(record.getIdtag()));
        element.setTagId(staticDataContainer.getTagId(record.getIdtag()));
        element.setLanguage(staticDataContainer.getLanguage(record.getIdioma()));
        element.setValue(record.getValor());

        EventTagType tagType = EventTagType.getTagTypeById(element.getTagId());
        if (tagType.isImage() && record.getValor() != null) {
            if (EventTagType.IMG_SQUARE_BANNER_WEB.equals(tagType)) {
                element.setPosition(record.getPosition());
            }
            element.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.ITEM_IMAGE)
                    .withOperatorId(channelEvent.getOperatorId())
                    .withItemId(element.getId())
                    .build()
                    .buildPath(record.getValor()));
        }
        return element;
    }
}
