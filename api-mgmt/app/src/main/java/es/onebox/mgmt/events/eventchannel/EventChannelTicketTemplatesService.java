package es.onebox.mgmt.events.eventchannel;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannel;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.PassbookRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.events.EventPassbookTemplateHelper;
import es.onebox.mgmt.events.converter.EventTicketTemplatesConverter;
import es.onebox.mgmt.events.dto.EventTicketTemplateDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.events.eventchannel.converter.EventChannelTicketTemplatesConverter;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import es.onebox.mgmt.validation.ValidationService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventChannelTicketTemplatesService {

    private final EventChannelsRepository eventChannelsRepository;
    private final ValidationService validationService;

    @Autowired
    public EventChannelTicketTemplatesService(EventChannelsRepository eventChannelsRepository,
                                              ChannelsRepository channelsRepository,
                                              ValidationService validationService) {
        this.eventChannelsRepository = eventChannelsRepository;
        this.validationService = validationService;
    }


    public void saveEventChannelTicketTemplate(Long eventId, Long channelId, EventTicketTemplateType type, TicketTemplateFormatPath templateFormat,
                                               IdDTO templateId) {
        Event event = validationService.getAndCheckEvent(eventId);
        EventChannel eventChannel = eventChannelsRepository.getEventChannel(eventId, channelId);
        checkEntites(event, eventChannel);
        if (templateId != null && templateId.getId() != null) {
            UpdateEventChannel updateEventChannel = EventChannelTicketTemplatesConverter.toEventChannel(eventId, type, templateFormat, templateId);
            eventChannelsRepository.updateEventChannel(eventId, channelId, updateEventChannel);
        }
    }

    public List<EventTicketTemplateDTO> getEventChannelTicketTemplates(@Min(value = 1, message = "eventId must be above 0") Long eventId, @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        EventChannel eventChannel = eventChannelsRepository.getEventChannel(eventId, channelId);
        return EventTicketTemplatesConverter.convert(eventChannel.getChannel().getTicketTemplates());
    }

    private void checkEntites(Event event, EventChannel eventChannel) {
        if(!event.getEntityId().equals(eventChannel.getChannel().getEntityId())) {
            throw ExceptionBuilder.build(ApiMgmtEntitiesErrorCode.EVENT_CHANNEL_DIFFERENT_ENTITIES);
        }
    }
}
