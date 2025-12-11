package es.onebox.mgmt.channels.catalog;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.catalog.converter.ChannelEventConverter;
import es.onebox.mgmt.channels.catalog.converter.ChannelSessionConverter;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventFilter;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventUpdateDetailDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsUpdateDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsFilter;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvent;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvents;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventsUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessions;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelEventsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelsRepository channelsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelEventsService(ChannelsHelper channelsHelper, ChannelsRepository channelsRepository, MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelEventsDTO search(Long channelId, ChannelEventFilter request) {
        this.channelsHelper.getAndCheckChannel(channelId);
        ChannelEvents response = this.channelsRepository.getChannelEvents(channelId, ChannelEventConverter.toFilter(request));
        return ChannelEventConverter.toDTO(response, masterdataService.getCurrencies());
    }

    public void update(Long channelId, ChannelEventsUpdateDTO body) {
        this.channelsHelper.getAndCheckChannel(channelId);
        validateUpdate(body);
        ChannelEventsUpdate request = ChannelEventConverter.fromDTO(body);
        this.channelsRepository.updateChannelEvents(channelId, request);
    }

    public ChannelEventDTO getChannelEvent(Long channelId, Long eventId) {
        this.channelsHelper.getAndCheckChannel(channelId);
        ChannelEvent response = this.channelsRepository.getChannelEvent(channelId, eventId);
        return ChannelEventConverter.toDTO(response, masterdataService.getCurrencies());
    }

    public void putChannelEvent(Long channelId, Long eventId, ChannelEventUpdateDetailDTO channelEventUpdateDetailDTO) {
        ChannelEventUpdate request = ChannelEventConverter.fromDTO(channelEventUpdateDetailDTO);
        request.setEventId(eventId);
        this.channelsRepository.putChannelEvent(channelId, eventId, request);
    }

    public ChannelSessionsDTO searchSessions(Long channelId, Long eventId, ChannelSessionsFilter request) {
        this.channelsHelper.getAndCheckChannel(channelId);
        ChannelSessions response = this.channelsRepository.getChannelEventSessions(channelId, eventId, ChannelSessionConverter.toFilter(request));
        return ChannelSessionConverter.toDTO(response);
    }

    private void validateUpdate(ChannelEventsUpdateDTO body) {

        if (body.stream().filter(event -> event.getCatalog() != null).anyMatch(event ->
                event.getCatalog().getVisible() != null && !event.getCatalog().getVisible() && event.getCatalog().getPosition() != null)) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.UPDATED_EVENT_MISMATCH);
        }
    }
}
