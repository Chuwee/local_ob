package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.SessionChannelContentImageType;
import es.onebox.mgmt.common.channelcontents.SessionChannelContentTextType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.events.dto.EventChannelContentImageFilter;
import es.onebox.mgmt.sessions.dto.SessionChannelContentImageListDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelContentsTextFilter;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static es.onebox.mgmt.exception.ApiMgmtSessionErrorCode.SESSION_NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtSessionErrorCode.SESSION_NOT_MATCH_EVENT;

@Service
public class SessionChannelContentsService {

    private final MasterdataService masterdataService;
    private final EventsRepository eventsRepository;
    private final SessionsRepository sessionsRepository;
    private final ValidationService validationService;


    @Autowired
    public SessionChannelContentsService( MasterdataService masterdataService,
                                         EventsRepository eventsRepository, SessionsRepository sessionssRepository,
                                         ValidationService validationService) {
        this.masterdataService = masterdataService;
        this.eventsRepository = eventsRepository;
        this.sessionsRepository = sessionssRepository;
        this.validationService = validationService;
    }

    public ChannelContentTextListDTO<SessionChannelContentTextType> getChannelContentTexts(Long eventId, Long sessionId, SessionChannelContentsTextFilter filter) {
        Event event = eventsRepository.getEvent(eventId);
        getAndCheckSession(event, sessionId);

        CommunicationElementFilter<EventTagType> communicationElementFilter = ChannelContentConverter.fromEventFilter(filter, masterdataService);
        ChannelContentsUtils.addEventTagsToFilter(communicationElementFilter, EventTagType::isText);
        List<EventCommunicationElement> comElements = sessionsRepository.getSessionCommunicationElements(eventId, sessionId, communicationElementFilter);

        return ChannelContentConverter.fromMsEventSessionText(comElements);
    }

    public ChannelContentImageListDTO<SessionChannelContentImageType> getChannelContentImages(Long eventId, Long sessionId,
                                                                                              EventChannelContentImageFilter filter) {
        Event event = eventsRepository.getEvent(eventId);
        getAndCheckSession(event, sessionId);

        CommunicationElementFilter<EventTagType> communicationElementFilter = ChannelContentConverter.fromEventFilter(filter, masterdataService);
        ChannelContentsUtils.addEventTagsToFilter(communicationElementFilter, EventTagType::isImage);
        List<EventCommunicationElement> comElements = sessionsRepository.getSessionCommunicationElements(eventId, sessionId, communicationElementFilter);

        return ChannelContentConverter.fromMsEventSessionImage(comElements);
    }

    public void updateChannelContentTexts(Long eventId, Long sessionId, List<ChannelContentTextDTO<SessionChannelContentTextType>> texts) {
        Event event = eventsRepository.getEvent(eventId);
        getAndCheckSession(event, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentTextDTO<SessionChannelContentTextType> element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage()));
        }
        sessionsRepository.updateSessionCommunicationElements(eventId, sessionId, ChannelContentConverter.toMsEventText(texts));
    }

    public void updateChannelContentTextsBulk(Long eventId, List<Long> sessionIds, List<ChannelContentTextDTO<SessionChannelContentTextType>> texts) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentTextDTO<SessionChannelContentTextType> element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage()));
        }
        sessionsRepository.updateSessionCommunicationElementsBulk(eventId, ChannelContentConverter.toMsEventText(sessionIds, texts));
    }

    public void updateChannelContentImages(Long eventId, Long sessionId, SessionChannelContentImageListDTO images) {
        Event event = eventsRepository.getEvent(eventId);
        getAndCheckSession(event, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        images.forEach(element -> element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage())));

        sessionsRepository.updateSessionCommunicationElements(eventId, sessionId, ChannelContentConverter.toMsEventImageList(images));
    }

    public void updateChannelContentImagesBulk(Long eventId, List<Long> sessionIds, SessionChannelContentImageListDTO images) {
        Event event = validationService.getAndCheckEvent(eventId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        images.forEach(element -> element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage())));
        sessionsRepository.updateSessionCommunicationElementsBulk(eventId, ChannelContentConverter.toMsEventImageList(sessionIds, images));
    }

    public void deleteChannelContentImages(Long eventId, Long sessionId, String language, SessionChannelContentImageType type, Integer position) {
        Event event = eventsRepository.getEvent(eventId);
        getAndCheckSession(event, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        EventCommunicationElement dto = ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages);
        sessionsRepository.updateSessionCommunicationElements(eventId, sessionId, Collections.singletonList(dto));
    }

    public void deleteChannelContentImageBulk(Long eventId, List<Long> sessionIds, String language, SessionChannelContentImageType type, Integer position) {
        Event event = eventsRepository.getEvent(eventId);
        sessionIds.forEach(sessionId -> getAndCheckSession(event, sessionId));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        sessionsRepository.updateSessionCommunicationElementsBulk(eventId,
                ChannelContentConverter.buildEventCommunicationElementBulkToDelete(language, type, position, languages, sessionIds));
    }

    public void deleteChannelContentImagesBulk(Long eventId, List<Long> sessionIds, String language) {
        Event event = eventsRepository.getEvent(eventId);
        sessionIds.forEach(sessionId -> getAndCheckSession(event, sessionId));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageOut = ChannelContentsUtils.checkElementLanguageForEvent(event, languages, language);
        sessionsRepository.deleteSessionCommunicationElementsBulk(eventId, languageOut, sessionIds);
    }

    private Session getAndCheckSession(Event event, Long sessionId) {
        validationService.getAndCheckEvent(event.getId());
        Session session = sessionsRepository.getSession(event.getId(), sessionId);
        if (session == null || SessionStatus.DELETED.equals(session.getStatus())) {
            throw new OneboxRestException(SESSION_NOT_FOUND, "No session found with id: " + event, null);
        } else if (!session.getEventId().equals(event.getId())) {
            throw new OneboxRestException(SESSION_NOT_MATCH_EVENT);
        }
        return session;
    }
}
