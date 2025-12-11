package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventCommunicationElement;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventDataComElementsBuilder {

    private final Event data;

    private StaticDataContainer staticDataContainer;
    private List<CpanelIdiomaComEventoRecord> languages;
    private List<CpanelElementosComEventoRecord> communicationElementRecords;
    private Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> emailCommElements;

    private EventDataComElementsBuilder(Event data) {
        super();
        this.data = data;
    }

    public static EventDataComElementsBuilder builder(Event data) {
        return new EventDataComElementsBuilder(data);
    }

    public EventDataComElementsBuilder staticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
        return this;
    }

    public EventDataComElementsBuilder languages(List<CpanelIdiomaComEventoRecord> languages) {
        this.languages = languages;
        return this;
    }

    public EventDataComElementsBuilder communicationElementRecords(List<CpanelElementosComEventoRecord> communicationElementRecords) {
        this.communicationElementRecords = communicationElementRecords;
        return this;
    }

    public EventDataComElementsBuilder emailCommunicationElements(Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> emailCommElements) {
        this.emailCommElements = emailCommElements;
        return this;
    }

    public void buildComElements() {
        var imageUrlBuilder = S3URLResolver.builder()
                .withEventId(data.getEventId())
                .withEntityId(data.getEntity().getId())
                .withOperatorId(data.getOperatorId())
                .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                .withUrl(staticDataContainer.getS3Repository())
                .build();
        data.setCommunicationElements(getCommunicationElements(communicationElementRecords, languages, imageUrlBuilder));
        data.setEmailCommunicationElements(getEmailCommunicationElements(emailCommElements, data.getOperatorId(), staticDataContainer));
    }

    private List<EventCommunicationElement> getEmailCommunicationElements(Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> emailCommElements, Integer operatorId, StaticDataContainer staticDataContainer) {
        List<EventCommunicationElement> communicationElements = new ArrayList<>();
        if (MapUtils.isNotEmpty(emailCommElements)) {
            emailCommElements.forEach((key, value) -> value.forEach(desc -> {
                EventCommunicationElement eventCommunicationElement = new EventCommunicationElement();
                eventCommunicationElement.setId(desc.getIditem().longValue());
                eventCommunicationElement.setTag(key.name());
                eventCommunicationElement.setLanguageCode(this.staticDataContainer.getLanguage(desc.getIdidioma()));
                eventCommunicationElement.setValue(desc.getDescripcion());
                eventCommunicationElement.setPosition(1);
                var url = S3URLResolver.builder()
                        .withUrl(staticDataContainer.getS3Repository())
                        .withOperatorId(operatorId)
                        .withType(S3URLResolver.S3ImageType.ITEM_IMAGE)
                        .withItemId(desc.getIditem()).build().buildPath(desc.getDescripcion());
                eventCommunicationElement.setUrl(url);
                eventCommunicationElement.setAltText(desc.getAlttext());
                communicationElements.add(eventCommunicationElement);
            }));

        }

        return communicationElements;
    }

    private List<EventCommunicationElement> getCommunicationElements(List<CpanelElementosComEventoRecord> communicationElementRecords, List<CpanelIdiomaComEventoRecord> languages, S3URLResolver imageUrlBuilder) {
        List<EventCommunicationElement> communicationElements = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(communicationElementRecords)) {
            for (CpanelElementosComEventoRecord communicationElementRecord : communicationElementRecords) {
                for (CpanelIdiomaComEventoRecord language : languages) {
                    if (communicationElementRecord.getIdioma().equals(language.getIdidioma())) {
                        if (communicationElementRecord.getValor() != null) {
                            EventCommunicationElement eventCommunicationElement = new EventCommunicationElement();
                            eventCommunicationElement.setId(communicationElementRecord.getIdelemento().longValue());
                            eventCommunicationElement.setTag(staticDataContainer.getTag(communicationElementRecord.getIdtag()));
                            eventCommunicationElement.setTagId(staticDataContainer.getTagId(communicationElementRecord.getIdtag()));
                            eventCommunicationElement.setLanguageCode(staticDataContainer.getLanguage(communicationElementRecord.getIdioma()));
                            eventCommunicationElement.setPosition(communicationElementRecord.getPosition());
                            eventCommunicationElement.setValue(communicationElementRecord.getValor());
                            EventTagType tagTypeById = EventTagType.getTagTypeById(eventCommunicationElement.getTagId());
                            if (tagTypeById != null && tagTypeById.isImage() && communicationElementRecord.getValor() != null) {
                                eventCommunicationElement.setUrl(imageUrlBuilder.buildPath(communicationElementRecord.getValor()));
                                eventCommunicationElement.setAltText(communicationElementRecord.getAlttext());
                            }
                            communicationElements.add(eventCommunicationElement);
                        }
                        break;
                    }
                }
            }
        }
        return communicationElements;
    }

}
