package es.onebox.fever.service;

import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementFilter;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.enums.EventTagType;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.webhook.dto.fever.EventUpdate;
import es.onebox.common.datasources.webhook.dto.fever.session.SessionDetail;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.session.SessionStatus;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.fever.converter.EventConverter;
import es.onebox.fever.converter.SessionConverter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionWebhookService {

  private final MsEventRepository sessionRepository;
  private  final EntityValidationService entityValidationService;

  private static final String SESSION_IMAGE_TAGS = "SESSION_COMMUNICATION_IMAGES";
  private static final String DELETED_IMAGE_TAGS = "SESSION_COMMUNICATION_DELETED";

  @Autowired
  public SessionWebhookService(MsEventRepository sessionRepository, EntityValidationService entityValidationService) {
    this.sessionRepository = sessionRepository;
    this.entityValidationService = entityValidationService;
  }

  public WebhookFeverDTO sendSessionGeneralData(
      WebhookFeverDTO webhookFeverDTO) {

    SessionDTO session = sessionRepository.getSession(Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()));

    SessionDetail sessionDetail = new SessionDetail();

    if (session != null && session.getDate() != null) {
      sessionDetail.setDate(SessionConverter.mapSessionDate(session.getDate()));
    }
    sessionDetail.setId(session.getId());
    sessionDetail.setName(session.getName());
    sessionDetail.setStatus(SessionStatus.valueOf(session.getStatus().name()));
    sessionDetail.setVenueId(session.getVenueId());
    sessionDetail.setVenueName(session.getVenueName());

    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setSession(sessionDetail);

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventId(session.getEventId());
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendSessionCommunicationElements(
      WebhookFeverDTO webhookFeverDTO) {

    SessionDTO session = sessionRepository.getSessionCached(Long.valueOf(webhookFeverDTO.getNotificationMessage()
        .getId()));

    String obSubtype = webhookFeverDTO.getHeaders().getHeader("ob-subtype");

    EventCommunicationElementFilter  filter = generateEventCommunicationFilter(obSubtype);
    List<EventCommunicationElementDTO> updatedSessionCommunication =
        sessionRepository.getSessionCommunicationElements(
            session.getEventId(), session.getId(), filter
        );

    Long channelId = webhookFeverDTO.getNotificationMessage().getChannelId();
    if (channelId != null) {
      entityValidationService.validateAllowedEntities(webhookFeverDTO);
      Long sessionId = session.getId();
      Long eventId = session.getEventId();
      updatedSessionCommunication.addAll(sessionRepository.getSessionChannelCommunicationElements(eventId, sessionId, channelId, filter));
    }
    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setEventCommunicationElements(EventConverter.toEventCommunicationElementDTO(
        updatedSessionCommunication
    ));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventId(session.getEventId());
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }
  
  private EventCommunicationElementFilter generateEventCommunicationFilter(String obSubtype) {
    EventCommunicationElementFilter filter = new EventCommunicationElementFilter();
    if (SESSION_IMAGE_TAGS.equals(obSubtype) || DELETED_IMAGE_TAGS.equals(obSubtype)) {
      filter.setTags(new HashSet<>(Arrays.asList(EventTagType.LOGO_WEB, EventTagType.IMG_BODY_WEB,
          EventTagType.IMG_BANNER_WEB,EventTagType.IMG_CARD_WEB, EventTagType.IMG_SQUARE_BANNER_WEB)));
    } else {
      filter.setTags(new HashSet<>(Arrays.asList(EventTagType.TEXT_TITLE_WEB, EventTagType.TEXT_SUBTITLE_WEB,
          EventTagType.TEXT_LENGTH_WEB,EventTagType.TEXT_SUMMARY_WEB, EventTagType.TEXT_BODY_WEB, EventTagType.TEXT_LOCATION_WEB)));
    }
    return filter;
  }

}
