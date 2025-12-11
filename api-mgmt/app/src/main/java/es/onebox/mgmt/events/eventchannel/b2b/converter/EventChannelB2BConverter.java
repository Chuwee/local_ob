package es.onebox.mgmt.events.eventchannel.b2b.converter;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.b2b.clients.enums.ClientType;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignation;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.UpdateChannelEventAssignation;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.UpdateChannelEventAssignations;
import es.onebox.mgmt.events.eventchannel.b2b.dto.EventChannelB2BAssignationsDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.QuotaClientAssignationDTO;
import es.onebox.mgmt.events.eventchannel.b2b.enums.ChannelEventQuotaAssignationType;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventChannelB2BConverter {

    private EventChannelB2BConverter() {}

    public static EventChannelB2BAssignationsDTO toDTO(EventChannelB2BAssignations source, Clients clients) {
        EventChannelB2BAssignationsDTO target = new EventChannelB2BAssignationsDTO();
        Map<Integer, String> clientsMap = buildClientIdNameMap(clients);
        target.setType(ChannelEventQuotaAssignationType.ALL_QUOTAS);
        if (!source.isEmpty()) {
            target.setType(ChannelEventQuotaAssignationType.SPECIFIC);
            target.setAssignations(source.stream().map(assignation -> toDTO(assignation, clientsMap))
                    .sorted(Comparator.comparingLong(assignation -> assignation.getQuota().getId()))
                    .toList());
        }
        return target;
    }

    public static UpdateChannelEventAssignations toMs(EventChannelB2BAssignationsDTO source) {
        return source.getAssignations().stream()
                .map(EventChannelB2BConverter::toMs)
                .collect(Collectors.toCollection(UpdateChannelEventAssignations::new));
    }

    public static UpdateChannelEventAssignation toMs(QuotaClientAssignationDTO source) {
        UpdateChannelEventAssignation target = new UpdateChannelEventAssignation();
        target.setAllClients(source.getAllClients());
        target.setQuotaId(source.getQuota().getId());
        if (source.getClients() != null) {
            target.setClients(source.getClients().stream().map(IdDTO::getId).toList());
        }
        return target;
    }

    private static QuotaClientAssignationDTO toDTO(EventChannelB2BAssignation source, Map<Integer, String> clientsMap) {
        QuotaClientAssignationDTO target = new QuotaClientAssignationDTO();
        target.setAllClients(source.getAllClients());
        target.setQuota(source.getQuota());
        if (!source.getClients().isEmpty()) {
            target.setClients(source.getClients().stream()
                    .filter(client -> clientsMap.get(client.intValue()) != null)
                    .map(client -> new IdNameDTO(client, clientsMap.get(client.intValue())))
                    .sorted(Comparator.comparing(IdNameDTO::getName))
                    .toList());
        }
        return target;
    }
    private static Map<Integer,String> buildClientIdNameMap(Clients clients) {
        return clients != null ?clients.getClientList().stream()
                .collect(Collectors.toMap(client -> client.getClientB2B().getClientEntity().getId(), Client::getName))
                : null;
    }

    public static SearchClientsFilter buildClientsFilter(Long entityId) {
        SearchClientsFilter filter = new SearchClientsFilter();
        filter.setEntityId(entityId.intValue());
        filter.setActive(Boolean.TRUE);
        filter.setClientTypeId(ClientType.CLIENT_B2B.getId());
        return filter;
    }

    public static SearchClientsFilter buildClientsFilter(EventChannelB2BAssignations assignations, Long entityId) {
        SearchClientsFilter filter = new SearchClientsFilter();
        filter.setEntityId(entityId.intValue());
        filter.setActive(Boolean.TRUE);
        filter.setClientsId(getClientIds(assignations));
        filter.setClientTypeId(ClientType.CLIENT_B2B.getId());
        return filter;
    }

    private static List<Long> getClientIds(EventChannelB2BAssignations source) {
        return source.stream()
                .map(EventChannelB2BAssignation::getClients)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }
}
