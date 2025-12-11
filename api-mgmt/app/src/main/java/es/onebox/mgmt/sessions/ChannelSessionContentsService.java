package es.onebox.mgmt.sessions;


import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.events.dto.ChannelEventContentImageFilter;
import es.onebox.mgmt.events.dto.ChannelEventContentImageUpdateRequest;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import es.onebox.mgmt.events.eventchannel.EventChannelValidations;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChannelSessionContentsService {

    private final EventsRepository eventsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final ValidationService validationService;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final SessionsRepository sessionsRepository;
    private final ChannelsHelper channelsHelper;


    @Autowired
    public ChannelSessionContentsService(EventsRepository eventsRepository,
                                         EventChannelsRepository eventChannelsRepository,
                                         ValidationService validationService,
                                         SecurityManager securityManager,
                                         MasterdataService masterdataService,
                                         SessionsRepository sessionsRepository,
                                         ChannelsHelper channelsHelper) {
        this.eventsRepository = eventsRepository;
        this.eventChannelsRepository = eventChannelsRepository;
        this.validationService = validationService;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.sessionsRepository = sessionsRepository;
        this.channelsHelper = channelsHelper;
    }

    public ChannelContentImageListDTO<ChannelEventContentImageType> getChannelSessionImages(Long eventId, Long sessionId, Long channelId, ChannelEventContentImageFilter filter) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);
        validationService.getAndCheckSession(eventId, sessionId);
        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        CommunicationElementFilter<EventTagType> communicationElementFilter = ChannelContentConverter.fromEventFilter(filter, masterdataService);
        List<EventCommunicationElement> comElements = sessionsRepository.getChannelSessionCommunicationElements(eventId,
                sessionId, channelId, communicationElementFilter, EventTagType::isImage);

        return ChannelContentConverter.fromMsChannelEventImage(comElements);
    }

    public void updateChannelSessionImages(Long eventId, Long sessionId, Long channelId, ChannelEventContentImageUpdateRequest request) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);
        validationService.getAndCheckSession(eventId, sessionId);
        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }

        validateUpdateRequest(request, channelId);
        sessionsRepository.updateChannelSessionCommunicationElements(eventId, sessionId, channelId, ChannelContentConverter.toMsEventImageList(request));
    }

    public void deleteChannelSessionImage(Long eventId, Long sessionId, Long channelId, String language, ChannelEventContentImageType type, Integer position) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);
        validationService.getAndCheckSession(eventId, sessionId);
        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        validatePosition(type, position);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        sessionsRepository.updateChannelSessionCommunicationElements(eventId, sessionId, channelId,
                Collections.singletonList(ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages)));
    }

    public void deleteChannelSessionImages(Long eventId, Long sessionId, Long channelId) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);
        validationService.getAndCheckSession(eventId, sessionId);
        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        List<EventCommunicationElement> imageElements = sessionsRepository.getChannelSessionCommunicationElements(
                eventId, sessionId, channelId, null, EventTagType::isImage
        );
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        List<EventCommunicationElement> elementsToDelete = imageElements.stream()
                .map(e -> {
                    ChannelEventContentImageType type = ChannelEventContentImageType.getById(e.getTagId());
                    String languageTag = ConverterUtils.toLanguageTag(e.getLanguage());
                    return ChannelContentConverter.buildEventCommunicationElementToDelete(languageTag, type, e.getPosition(), languages);
                })
                .collect(Collectors.toList());
        sessionsRepository.updateChannelSessionCommunicationElements(eventId, sessionId, channelId, elementsToDelete);
    }


    private void validatePosition(ChannelEventContentImageType type, Integer position) {
        if (ChannelEventContentImageType.SQUARE_LANDSCAPE.equals(type)) {
            if (position == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.IMAGE_POSITION_REQUIRED);
            }
            if (position < 1 || position > 5) {
                throw new OneboxRestException(ApiMgmtErrorCode.IMAGE_POSITION_INVALID);
            }
        }
    }

    private void validateUpdateRequest(ChannelEventContentImageUpdateRequest request, Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        request.forEach(elem -> {
            elem.setLanguage(ChannelContentsUtils.checkElementLanguageForChannel(channelResponse, languagesByIds, elem.getLanguage()));
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
            validatePosition(elem.getType(), elem.getPosition());
        });
    }
}
