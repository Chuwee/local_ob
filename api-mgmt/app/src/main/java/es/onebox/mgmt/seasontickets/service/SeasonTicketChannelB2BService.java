package es.onebox.mgmt.seasontickets.service;

import es.onebox.mgmt.b2b.utils.B2BUtilsService;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.events.eventchannel.b2b.converter.EventChannelB2BConverter;
import es.onebox.mgmt.events.eventchannel.b2b.dto.EventChannelB2BAssignationsDTO;
import es.onebox.mgmt.events.eventchannel.b2b.service.EventChannelB2BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketChannelB2BService {

    private final EventChannelsRepository eventChannelsRepository;
    private final B2BUtilsService b2BUtilsService;
    private final SeasonTicketChannelService seasonTicketChannelService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final EventChannelB2BService eventChannelB2BService;

    @Autowired
    public SeasonTicketChannelB2BService(EventChannelsRepository eventChannelsRepository, B2BUtilsService b2BUtilsService,
                                         SeasonTicketChannelService seasonTicketChannelService, SeasonTicketRepository seasonTicketRepository,
                                         EventChannelB2BService eventChannelB2BService) {
        this.eventChannelsRepository = eventChannelsRepository;
        this.b2BUtilsService = b2BUtilsService;
        this.seasonTicketChannelService = seasonTicketChannelService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.eventChannelB2BService = eventChannelB2BService;
    }

    public EventChannelB2BAssignationsDTO getAssignations(Long seasonTicketId, Long channelId) {
        Long entityId = validate(seasonTicketId, channelId);
        EventChannelB2BAssignations response = eventChannelsRepository.getEventChannelAssignation(seasonTicketId, channelId);
        Clients clients = eventChannelB2BService.getClients(response, entityId);
        return EventChannelB2BConverter.toDTO(response, clients);
    }

    public void updateAssignation(Long seasonTicketId, Long channelId, EventChannelB2BAssignationsDTO request) {
        Long entityId = validate(seasonTicketId, channelId);
        switch (request.getType()) {
            case ALL_QUOTAS -> eventChannelsRepository.deleteEventChannelAssignation(seasonTicketId, channelId);
            case SPECIFIC -> {
                eventChannelB2BService.validateUpdate(request, entityId);
                eventChannelsRepository.updateEventChannelAssignation(seasonTicketId, channelId,
                        EventChannelB2BConverter.toMs(request));
            }
        }
    }

    private Long validate(Long seasonTicketId, Long channelId) {
        seasonTicketChannelService.getSeasonTicketChannel(seasonTicketId, channelId);
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        b2BUtilsService.validateEntity(seasonTicket.getEntityId());
        return seasonTicket.getEntityId();
    }
}
