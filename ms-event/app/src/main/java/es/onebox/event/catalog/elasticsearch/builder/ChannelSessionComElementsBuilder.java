package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.dao.record.CommElementRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelSessionComElementsBuilder {
    private final Integer sessionId;
    private final Integer eventId;
    private final EventIndexationContext context;
    private final String s3Repository;

    private List<CommElementRecord> squareBanners;

    private ChannelSessionComElementsBuilder(Integer sessionId, Integer eventId, EventIndexationContext context, String s3Repository) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.context = context;
        this.s3Repository = s3Repository;
    }

    public static ChannelSessionComElementsBuilder builder(Integer sessionId, Integer eventId, EventIndexationContext context, String s3Repository) {
        return new ChannelSessionComElementsBuilder(sessionId, eventId, context, s3Repository);
    }

    public ChannelSessionComElementsBuilder squareBanners(List<CommElementRecord> squareBanners) {
        this.squareBanners = squareBanners;
        return this;
    }

    public List<SessionCommunicationElement> build() {
        List<SessionCommunicationElement> elements = new ArrayList<>();

        if (squareBanners != null && !squareBanners.isEmpty()) {
            var imageUrlBuilder = S3URLResolver.builder()
                    .withUrl(s3Repository)
                    .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                    .withSessionId(sessionId)
                    .withEventId(eventId)
                    .withEntityId(context.getEntity().getId())
                    .withOperatorId(context.getEntity().getOperator().getId().longValue())
                    .build();

            elements = squareBanners.stream()
                    .map(record -> {
                        SessionCommunicationElement element = new SessionCommunicationElement();
                        element.setId(record.getIdItem().longValue());
                        element.setPosition(record.getPosition());
                        element.setValue(record.getValue());
                        element.setTagId(12);
                        element.setTag("IMG_SQUARE_BANNER_WEB");
                        element.setLanguageCode(record.getLanguageCode());
                        element.setUrl(imageUrlBuilder.buildPath(record.getValue()));
                        element.setAltText(record.getAltText());
                        return element;
                    })
                    .collect(Collectors.toList());
        }

        return elements;
    }
}
