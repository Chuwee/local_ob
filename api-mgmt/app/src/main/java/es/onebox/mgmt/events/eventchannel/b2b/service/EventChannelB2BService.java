package es.onebox.mgmt.events.eventchannel.b2b.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.b2b.utils.B2BUtilsService;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignation;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.eventchannel.EventChannelsService;
import es.onebox.mgmt.events.eventchannel.b2b.converter.EventChannelB2BConverter;
import es.onebox.mgmt.events.eventchannel.b2b.dto.EventChannelB2BAssignationsDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.QuotaClientAssignationDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class EventChannelB2BService {

    private final EventChannelsRepository eventChannelsRepository;
    private final EventsRepository eventsRepository;
    private final ClientsRepository clientsRepository;
    private final B2BUtilsService b2BUtilsService;
    private final EventChannelsService eventChannelsService;

    @Autowired
    public EventChannelB2BService(EventChannelsRepository eventChannelsRepository, EventsRepository eventsRepository,
                                  ClientsRepository clientsRepository, B2BUtilsService b2BUtilsService,
                                  EventChannelsService eventChannelsService) {
        this.eventChannelsRepository = eventChannelsRepository;
        this.eventsRepository = eventsRepository;
        this.clientsRepository =  clientsRepository;
        this.b2BUtilsService = b2BUtilsService;
        this.eventChannelsService = eventChannelsService;
    }

    public EventChannelB2BAssignationsDTO getAssignations(Long eventId, Long channelId) {
        Long entityId = validate(eventId, channelId);
        EventChannelB2BAssignations response = eventChannelsRepository.getEventChannelAssignation(eventId, channelId);
        Clients clients = getClients(response, entityId);
        return EventChannelB2BConverter.toDTO(response, clients);
    }

    public void updateAssignation(Long eventId, Long channelId, EventChannelB2BAssignationsDTO request) {
        Long entityId = validate(eventId, channelId);
        switch (request.getType()) {
            case ALL_QUOTAS -> eventChannelsRepository.deleteEventChannelAssignation(eventId, channelId);
            case SPECIFIC -> {
                validateUpdate(request, entityId);
                eventChannelsRepository.updateEventChannelAssignation(eventId, channelId,
                        EventChannelB2BConverter.toMs(request));
            }
        }
    }

    private Long validate(Long eventId, Long channelId) {
        eventChannelsService.getEventChannel(eventId, channelId);
        Event event = eventsRepository.getEvent(eventId);
        b2BUtilsService.validateEntity(event.getEntityId());
        return event.getEntityId();
    }

    public void validateUpdate(EventChannelB2BAssignationsDTO request, Long entityId) {
        if (request.getAssignations() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "assignations can not be null", null);
        }

        if (request.getAssignations().stream().anyMatch(assignation -> assignation.getQuota().getId() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "quota id can not be null", null);
        }

        if (request.getAssignations().stream().map(QuotaClientAssignationDTO::getClients)
                .filter(Objects::nonNull).flatMap(Collection::stream).anyMatch(client -> client.getId() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "client id can not be null", null);
        }
        validateClients(request, entityId);
    }

    private void validateClients(EventChannelB2BAssignationsDTO request, Long entityId) {
        List<Integer> requestIds = request.getAssignations().stream()
                .map(QuotaClientAssignationDTO::getClients)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(elem -> elem.getId().intValue())
                .toList();


        if (!requestIds.isEmpty()) {
            List<Integer> clients = clientsRepository.getClients(EventChannelB2BConverter.buildClientsFilter(entityId))
                    .getClientList().stream()
                    .map(client -> client.getClientB2B().getClientEntity().getId())
                    .toList();
            if (!CollectionUtils.removeAll(requestIds, clients).isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.CLIENT_NOT_FOUND);
            }
        }
    }

    public Clients getClients(EventChannelB2BAssignations response, Long entityId) {
        if (!response.stream().map(EventChannelB2BAssignation::getClients).flatMap(Collection::stream).toList().isEmpty()) {
            return clientsRepository.getClients(EventChannelB2BConverter.buildClientsFilter(response, entityId));
        }
        return null;
    }
}
