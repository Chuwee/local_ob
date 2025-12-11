package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.UpdateChannelEventAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannel;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventChannelsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventChannelsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public EventChannels getEventChannels(Long eventId, EventChannelSearchFilter filter) {
        return msEventDatasource.getEventChannels(eventId, filter);
    }

    public EventChannel getEventChannel(Long eventId, Long channelId) {
        return msEventDatasource.getEventChannel(eventId, channelId);
    }

    public void createEventChannel(Long eventId, Long channelId) {
        msEventDatasource.createEventChannel(eventId, channelId);
    }

    public void deleteEventChannel(Long eventId, Long channelId) {
        msEventDatasource.deleteEventChannel(eventId, channelId);
    }

    public void updateEventChannel(Long eventId, Long channelId, UpdateEventChannel updateEventChannel) {
        msEventDatasource.updateEventChannel(eventId, channelId, updateEventChannel);
    }

    public void requestChannelApproval(Long eventId, Long channelId, Long userId) {
        msEventDatasource.requestChannelApproval(eventId, channelId, userId);
    }

    public List<EventSurcharge> getEventChannelSurcharges(Long eventId, Long channelId, List<SurchargeType> types) {
        return msEventDatasource.getEventChannelSurcharges(eventId, channelId, types);
    }

    public void setEventChannelSurcharges(Long eventId, Long channelId, List<EventSurcharge> requests) {
        msEventDatasource.setEventChannelSurcharges(eventId, channelId, requests);
    }

    public EventChannelB2BAssignations getEventChannelAssignation(Long eventId, Long channelId) {
        return msEventDatasource.getEventChannelAssignation(eventId, channelId);
    }

    public void updateEventChannelAssignation(Long eventId, Long channelId, UpdateChannelEventAssignations body) {
        msEventDatasource.updateEventChannelAssignation(eventId, channelId, body);
    }

    public void deleteEventChannelAssignation(Long eventId, Long channelId) {
        msEventDatasource.deleteEventChannelAssignation(eventId, channelId);
    }
}
