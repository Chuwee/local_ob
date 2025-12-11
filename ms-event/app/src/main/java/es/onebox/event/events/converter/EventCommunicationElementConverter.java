package es.onebox.event.events.converter;

import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;

import java.util.List;
import java.util.stream.Collectors;

public class EventCommunicationElementConverter {

    private EventCommunicationElementConverter() {
        throw new UnsupportedOperationException();
    }

    public static List<EventCommunicationElementDTO> fromRecords(List<CpanelElementosComEventoRecord> records,
                                                                 EventRecord event, StaticDataContainer staticDataContainer) {
        return records.stream()
                .filter(r -> r.getValor() != null)
                .map(r -> fromRecord(r, event, staticDataContainer))
                .collect(Collectors.toList());
    }

    public static List<EventCommunicationElementDTO> fromRecords(List<CpanelElementosComEventoRecord> records,
                                                                 SessionRecord session, StaticDataContainer staticDataContainer) {
        return records.stream()
                .filter(r -> r.getValor() != null)
                .map(r -> fromRecord(r, session, staticDataContainer))
                .collect(Collectors.toList());
    }

    public static List<EventCommunicationElementDTO> fromRecords(List<CpanelElementosComEventoRecord> records,
                                                                 TourRecord tour, StaticDataContainer staticDataContainer) {
        return records.stream()
                .filter(r -> r.getValor() != null)
                .map(r -> fromRecord(r, tour, staticDataContainer))
                .collect(Collectors.toList());
    }

    private static EventCommunicationElementDTO fromRecord(CpanelElementosComEventoRecord record, EventRecord event,
                                                           StaticDataContainer staticDataContainer) {
        EventCommunicationElementDTO dto = initComElement(record, staticDataContainer);

        EventTagType tagType = EventTagType.getTagTypeById(dto.getTagId());
        if (tagType.isImage() && record.getValor() != null) {
            if (EventTagType.IMG_BANNER_WEB.equals(tagType)) {
                dto.setPosition(record.getPosition());
            }
            dto.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                    .withOperatorId(event.getOperatorId())
                    .withEntityId(event.getIdentidad())
                    .withEventId(event.getIdevento())
                    .build()
                    .buildPath(record.getValor()));
            dto.setAltText(record.getAlttext());
        }
        return dto;
    }

    private static EventCommunicationElementDTO fromRecord(CpanelElementosComEventoRecord record, SessionRecord session,
                                                           StaticDataContainer staticDataContainer) {
        EventCommunicationElementDTO dto = initComElement(record, staticDataContainer);

        EventTagType tagType = EventTagType.getTagTypeById(dto.getTagId());
        if (tagType.isImage() && record.getValor() != null) {
            if (EventTagType.IMG_BANNER_WEB.equals(tagType)) {
                dto.setPosition(record.getPosition());
            }
            dto.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                    .withOperatorId(session.getOperatorId())
                    .withEntityId(session.getEntityId())
                    .withEventId(session.getIdevento())
                    .withSessionId(session.getIdsesion())
                    .build()
                    .buildPath(record.getValor()));
            dto.setAltText(record.getAlttext());
        }
        return dto;
    }

    private static EventCommunicationElementDTO fromRecord(CpanelElementosComEventoRecord record, TourRecord tour,
                                                           StaticDataContainer staticDataContainer) {
        EventCommunicationElementDTO dto = initComElement(record, staticDataContainer);

        EventTagType tagType = EventTagType.getTagTypeById(dto.getTagId());
        if (tagType.isImage() && record.getValor() != null) {
            dto.setValue(S3URLResolver.builder()
                    .withUrl(staticDataContainer.getS3Repository())
                    .withType(S3URLResolver.S3ImageType.TOUR_IMAGE)
                    .withOperatorId(tour.getOperatorId())
                    .withEntityId(tour.getIdentidad())
                    .withTourId(tour.getIdgira())
                    .build()
                    .buildPath(record.getValor()));
            dto.setAltText(record.getAlttext());
        }
        return dto;
    }

    private static EventCommunicationElementDTO initComElement(CpanelElementosComEventoRecord record, StaticDataContainer staticDataContainer) {
        EventCommunicationElementDTO dto = new EventCommunicationElementDTO();
        dto.setId(record.getIdelemento().longValue());
        dto.setTag(staticDataContainer.getTag(record.getIdtag()));
        dto.setTagId(staticDataContainer.getTagId(record.getIdtag()));
        dto.setLanguage(staticDataContainer.getLanguage(record.getIdioma()));
        dto.setValue(record.getValor());
        return dto;
    }
}
