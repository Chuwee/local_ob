package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;

import java.util.List;
import java.util.stream.Collectors;

public class SessionDataComElementsBuilder {

    private final Session data;

    private StaticDataContainer staticDataContainer;
    private Long operatorId;
    private List<CpanelElementosComEventoRecord> communicationElements;

    private SessionDataComElementsBuilder(Session data) {
        super();
        this.data = data;
    }

    public static SessionDataComElementsBuilder builder(Session data) {
        return new SessionDataComElementsBuilder(data);
    }

    public SessionDataComElementsBuilder staticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
        return this;
    }

    public SessionDataComElementsBuilder operatorId(Long operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    public SessionDataComElementsBuilder communicationElements(List<CpanelElementosComEventoRecord> communicationElements) {
        this.communicationElements = communicationElements;
        return this;
    }

    public void buildComElements() {
        var imageUrlBuilder = S3URLResolver.builder()
                .withUrl(staticDataContainer.getS3Repository())
                .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                .withSessionId(data.getSessionId().intValue()).withEventId(data.getEventId().intValue())
                .withEntityId(data.getEntityId().intValue())
                .withOperatorId(operatorId.intValue())
                .build();
        data.setCommunicationElements(communicationElements.stream().map(elem ->
                convert(elem, imageUrlBuilder)).collect(Collectors.toList()));

    }

    private SessionCommunicationElement convert(CpanelElementosComEventoRecord comEventoRecord, S3URLResolver imageUrlBuilder) {
        return getSessionCommunicationElement(comEventoRecord, imageUrlBuilder, staticDataContainer);
    }

    public static SessionCommunicationElement getSessionCommunicationElement(CpanelElementosComEventoRecord commElem,
                                                                             S3URLResolver imageUrlBuilder,
                                                                             StaticDataContainer staticDataContainer) {
        SessionCommunicationElement sessionComElement = new SessionCommunicationElement();
        sessionComElement.setId(commElem.getIdelemento().longValue());
        sessionComElement.setPosition(commElem.getPosition());
        sessionComElement.setValue(commElem.getValor());
        sessionComElement.setTagId(commElem.getIdtag());
        sessionComElement.setTag(staticDataContainer.getTag(commElem.getIdtag()));
        sessionComElement.setLanguageCode(staticDataContainer.getLanguage(commElem.getIdioma()));
        EventTagType tagTypeById = EventTagType.getTagTypeById(sessionComElement.getTagId());
        if (tagTypeById != null && tagTypeById.isImage() && commElem.getValor() != null) {
            sessionComElement.setUrl(imageUrlBuilder.buildPath(commElem.getValor()));
            sessionComElement.setAltText(commElem.getAlttext());
        }
        return sessionComElement;
    }

}
