package es.onebox.mgmt.events.eventchannel;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionPackType;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.dto.ChannelEventContentImageFilter;
import es.onebox.mgmt.events.dto.ChannelEventContentImageUpdateRequest;
import es.onebox.mgmt.events.dto.channel.EventImageConfigDTO;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChannelEventContentsService {

    private final EventsRepository eventsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final EventChannelContentsRepository eventCommunicationElementRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final ChannelsHelper channelsHelper;

    @Autowired
    public ChannelEventContentsService(EventsRepository eventsRepository, EventChannelsRepository eventChannelsRepository,
                                       EventChannelContentsRepository eventCommunicationElementRepository,
                                       SecurityManager securityManager, MasterdataService masterdataService,
                                       ChannelsHelper channelsHelper) {
        this.eventsRepository = eventsRepository;
        this.eventChannelsRepository = eventChannelsRepository;
        this.eventCommunicationElementRepository = eventCommunicationElementRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.channelsHelper = channelsHelper;
    }

    public ChannelContentImageListDTO<ChannelEventContentImageType> getChannelEventImages(Long eventId, Long channelId, ChannelEventContentImageFilter filter) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessibleWithVisibility);

        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        CommunicationElementFilter<EventTagType> communicationElementFilter = ChannelContentConverter.fromEventFilter(filter, masterdataService);
        List<EventCommunicationElement> comElements = eventCommunicationElementRepository.getChannelEventCommunicationElements(eventId,
                channelId, communicationElementFilter, EventTagType::isImage);

        return ChannelContentConverter.fromMsChannelEventImage(comElements);
    }


    public List<EventImageConfigDTO> getChannelEventImagesConfiguration(Long eventId, Long channelId) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getCachedEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        Event event = eventsRepository.getCachedEvent(eventId);
        if(Boolean.FALSE.equals(event.getSupraEvent()) && SessionPackType.DISABLED.equals(event.getSessionPackType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CONFIG_NOT_SUPPORTED);
        }

        return ChannelContentConverter.fromMSChannelEventImageConfigList(eventCommunicationElementRepository.getChannelEventImageConfig(eventId, channelId));
    }

    public void updateChannelEventImages(Long eventId, Long channelId, ChannelEventContentImageUpdateRequest request) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);

        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }

        validateUpdateRequest(request, channelId);
        eventCommunicationElementRepository.updateChannelEventCommunicationElements(eventId, channelId, ChannelContentConverter.toMsEventImageList(request));
    }

    public void deleteChannelEventImage(Long eventId, Long channelId, String language, ChannelEventContentImageType type, Integer position) {
        EventChannel eventChannel = EventChannelValidations.GetEventChannelAndcheckPermissions(eventId, channelId,
                eventsRepository::getEvent, eventChannelsRepository::getEventChannel,
                securityManager::checkEntityAccessible);
        if (!WhitelabelType.EXTERNAL.equals(eventChannel.getChannel().getWhitelabelType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED);
        }
        validatePosition(type, position);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        eventCommunicationElementRepository.updateChannelEventCommunicationElements(eventId, channelId,
                Collections.singletonList(ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages)));
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

}
