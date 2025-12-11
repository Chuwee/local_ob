package es.onebox.event.tags.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.converter.EventConverter;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.tags.utils.LanguageUtils;
import es.onebox.event.tags.converter.SessionTagConverter;
import es.onebox.event.tags.dao.SessionTagCouchDao;
import es.onebox.event.tags.domain.ChannelsSessionTags;
import es.onebox.event.tags.domain.SessionTagCB;
import es.onebox.event.tags.domain.SessionTagsCB;
import es.onebox.event.tags.dto.ChannelSessionTagDTO;
import es.onebox.event.tags.dto.SessionTagLanguageDTO;
import es.onebox.event.tags.dto.SessionTagRequestDTO;
import es.onebox.event.tags.dto.SessionTagResponseDTO;
import es.onebox.event.tags.dto.SessionTagsResponseDTO;
import es.onebox.jooq.annotation.MySQLRead;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionTagService {

    private SessionTagCouchDao sessionTagCouchDao;
    private SessionValidationHelper sessionValidationHelper;
    private EventLanguageDao eventLanguageDao;
    private ChannelDao channelDao;

    public SessionTagService(SessionTagCouchDao sessionTagCouchDao,
                             SessionValidationHelper sessionValidationHelper,
                             EventLanguageDao eventLanguageDao,
                             ChannelDao channelDao) {
        this.sessionTagCouchDao = sessionTagCouchDao;
        this.sessionValidationHelper = sessionValidationHelper;
        this.eventLanguageDao = eventLanguageDao;
        this.channelDao = channelDao;
    }
    @MySQLRead
    public SessionTagsResponseDTO getSessionTags(Long eventId, Long sessionId) {
        validateEventAndSession(eventId, sessionId);
        SessionTagsCB sessionTagsCB = sessionTagCouchDao.getSessionTags(sessionId);
        if (sessionTagsCB == null) {
            return new SessionTagsResponseDTO();
        }

        return SessionTagConverter.toDTO(sessionTagsCB, getChannelSessionTag(sessionTagsCB));
    }

    @MySQLRead
    public SessionTagResponseDTO createSessionTag(Long eventId, Long sessionId, SessionTagRequestDTO sessionTagRequestDTO) {
        validateEventAndSession(eventId, sessionId);
        validateEventTagLanguage(eventId, sessionTagRequestDTO.getLanguages());
        SessionTagResponseDTO response = new SessionTagResponseDTO();
        SessionTagsCB sessionTagsCB = sessionTagCouchDao.getSessionTags(sessionId);

        Integer position = calculateTagPosition(sessionTagsCB);

        sessionTagCouchDao.upsert(String.valueOf(sessionId), toSessionTagCB(eventId, sessionId,
                position, sessionTagRequestDTO, sessionTagsCB));
        response.setPosition(position);
        return response;
    }

    @MySQLRead
    public void updateSessionTag(Long eventId, Long sessionId, Long positionId, SessionTagRequestDTO sessionTagRequestDTO) {
        validateEventAndSession(eventId, sessionId);
        validateEventTagLanguage(eventId, sessionTagRequestDTO.getLanguages());
        SessionTagsCB sessionTagsCB = sessionTagCouchDao.getSessionTags(sessionId);
        validateSessionTags(sessionTagsCB, positionId.intValue());
        updateSessionTag(sessionId, positionId, sessionTagsCB, sessionTagRequestDTO);
    }

    @MySQLRead
    public void deleteSessionTag(Long eventId, Long sessionId, Long positionId) {
        validateEventAndSession(eventId, sessionId);
        SessionTagsCB sessionTagsCB = sessionTagCouchDao.getSessionTags(sessionId);
        validateSessionTags(sessionTagsCB, positionId.intValue());

        sessionTagsCB.getTags().removeIf(tag -> tag.getPosition().equals(positionId.intValue()));
        recalculateTagsPositions(sessionTagsCB);
        sessionTagCouchDao.upsert(String.valueOf(sessionId), sessionTagsCB);
    }

    private void validateEventAndSession(Long eventId, Long sessionId) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
    }

    private void validateSessionTags(SessionTagsCB sessionTagsCB, Integer positionId) {
        if (sessionTagsCB == null || CollectionUtils.isEmpty(sessionTagsCB.getTags())) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_TAG_NOT_FOUND);
        }

        if (sessionTagsCB.getTags().stream().noneMatch(tag -> tag.getPosition().equals(positionId))) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_TAG_NOT_FOUND);
        }
    }

    private void validateEventTagLanguage(Long eventId, List<SessionTagLanguageDTO> languages) {
        EventDTO eventDTO = new EventDTO();
        eventDTO = EventConverter.fromEntity(eventLanguageDao.findByEventId(eventId), eventDTO);
        Set<String> requestLanguages = languages.stream()
                .map(SessionTagLanguageDTO::getLanguage)
                .map(LanguageUtils::toCpanelIdiomaCode)
                .collect(Collectors.toSet());
        Set<String> eventLanguages = eventDTO.getLanguages().stream().map(EventLanguageDTO::getCode).collect(Collectors.toSet());

        if (!eventLanguages.containsAll(requestLanguages)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_TAG_LANGUAGE_NOT_FOUND);
        }
    }

    private Integer calculateTagPosition(SessionTagsCB sessionTagsCB) {
        if (sessionTagsCB == null || CollectionUtils.isEmpty(sessionTagsCB.getTags())) {
            return 0;
        } else {
            return sessionTagsCB.getTags().size();
        }
    }

    private void recalculateTagsPositions(SessionTagsCB sessionTagsCB) {
        if (!CollectionUtils.isEmpty(sessionTagsCB.getTags())) {
            int position = 0;
            for (SessionTagCB tag : sessionTagsCB.getTags()) {
                tag.setPosition(position++);
            }
        }
    }

    private List<ChannelSessionTagDTO> getChannelSessionTag(SessionTagsCB sessionTagsCB) {
        Set<Integer> channelIds = sessionTagsCB.getTags().stream()
                .filter(tag -> Boolean.FALSE.equals(tag.getChannels().getAllChannels()))
                .map(tag -> tag.getChannels().getSelectedChannels())
                .flatMap(Collection::stream)
                .map(Long::intValue)
                .collect(Collectors.toSet());

        var channels = channelDao.getByIds(channelIds.stream().toList());
        return channels.stream()
                .map(SessionTagConverter::mapChannelSessionTag)
                .collect(Collectors.toList());
    }

    private void updateSessionTag(Long sessionId, Long positionId, SessionTagsCB sessionTagsCB, SessionTagRequestDTO sessionTagRequestDTO) {
        for (SessionTagCB tag : sessionTagsCB.getTags()) {
            if (tag.getPosition().equals(positionId.intValue())) {
                tag.setEnabled(sessionTagRequestDTO.getEnabled());
                tag.setBackgroundColor(sessionTagRequestDTO.getBackgroundColor());
                tag.setTextColor(sessionTagRequestDTO.getTextColor());
                ChannelsSessionTags channelsSessionTags = new ChannelsSessionTags();
                channelsSessionTags.setAllChannels(sessionTagRequestDTO.getChannels().getAllChannels());
                if (!channelsSessionTags.getAllChannels()) {
                    channelsSessionTags.setSelectedChannels(sessionTagRequestDTO.getChannels().getSelectedChannels());
                }
                tag.setChannels(channelsSessionTags);
                tag.setLanguages(SessionTagConverter.mapToLanguageMs(sessionTagRequestDTO.getLanguages()));
            }
        }
        sessionTagCouchDao.upsert(String.valueOf(sessionId), sessionTagsCB);
    }

    private static SessionTagsCB toSessionTagCB(Long eventId, Long sessionId, Integer position, SessionTagRequestDTO in,
                                                SessionTagsCB sessionTagsCB) {
        SessionTagsCB out = new SessionTagsCB();
        SessionTagCB sessionTagCB = new SessionTagCB();
        List<SessionTagCB> list = new ArrayList<>();
        ChannelsSessionTags channelsSessionTags = new ChannelsSessionTags();

        if (sessionTagsCB != null) {
            list.addAll(sessionTagsCB.getTags());
        }

        out.setEventId(eventId);
        out.setSessionId(sessionId);
        sessionTagCB.setPosition(position);
        sessionTagCB.setEnabled(in.getEnabled());
        sessionTagCB.setBackgroundColor(in.getBackgroundColor());
        sessionTagCB.setTextColor(in.getTextColor());
        channelsSessionTags.setAllChannels(in.getChannels().getAllChannels());
        if (!channelsSessionTags.getAllChannels()) {
            channelsSessionTags.setSelectedChannels(in.getChannels().getSelectedChannels());
        }
        sessionTagCB.setChannels(channelsSessionTags);
        sessionTagCB.setLanguages(SessionTagConverter.mapToLanguageMs(in.getLanguages()));

        list.add(sessionTagCB);
        out.setTags(list);
        out.setSessionId(sessionId);
        return out;
    }
}
