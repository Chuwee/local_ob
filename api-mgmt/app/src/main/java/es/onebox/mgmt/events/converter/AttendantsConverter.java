package es.onebox.mgmt.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.BaseEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionAttendantsConfigDTO;
import es.onebox.mgmt.events.dto.AttendantTicketsChannelsScopeDTO;
import es.onebox.mgmt.events.dto.EventAttendantTicketsDTO;
import es.onebox.mgmt.events.enums.AttendantTicketsChannelScopeTypeDTO;
import es.onebox.mgmt.events.enums.AttendantTicketsEventStatusDTO;
import es.onebox.mgmt.sessions.dto.AttendantTicketsSessionStatusDTO;
import es.onebox.mgmt.sessions.dto.SessionAttendantTicketsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendantsConverter {

    private AttendantsConverter(){}

    public static EventAttendantsConfigDTO toMsEvent(Long eventId, EventAttendantTicketsDTO attendantTickets) {

        EventAttendantsConfigDTO eventAttendantsConfig = new EventAttendantsConfigDTO();
        eventAttendantsConfig.setEventId(eventId);

        if(AttendantTicketsEventStatusDTO.ACTIVE.equals(attendantTickets.getStatus())) {
            eventAttendantsConfig.setActive(Boolean.TRUE);

        }else if(AttendantTicketsEventStatusDTO.DISABLED.equals(attendantTickets.getStatus())) {
            eventAttendantsConfig.setActive(Boolean.FALSE);
        }
        eventAttendantsConfig.setAutofill(attendantTickets.getAutofill());
        eventAttendantsConfig.setAllowAttendantsModification(attendantTickets.getEditAttendant());
        eventAttendantsConfig.setAllowEditAutofill(attendantTickets.getEditAutofill());
        eventAttendantsConfig.setEditAutofillDisallowedSectors(attendantTickets.getEditAutofillDisallowedSectors());
        if(attendantTickets.getChannelsScope() != null) {
            toMsEvent(attendantTickets.getChannelsScope(), eventAttendantsConfig);
        }
        return eventAttendantsConfig;
    }

    public static SessionAttendantsConfigDTO toMsEvent(Long sessionId, SessionAttendantTicketsDTO attendantTickets) {

        SessionAttendantsConfigDTO sessionAttendantsConfig = new SessionAttendantsConfigDTO();
        sessionAttendantsConfig.setSessionId(sessionId);

        if(AttendantTicketsSessionStatusDTO.ACTIVE.equals(attendantTickets.getStatus())) {
            sessionAttendantsConfig.setActive(Boolean.TRUE);
            sessionAttendantsConfig.setAutofill(attendantTickets.getAutofill());
            sessionAttendantsConfig.setAllowEditAutofill(attendantTickets.getEditAutofill());
        }else if(AttendantTicketsSessionStatusDTO.DISABLED.equals(attendantTickets.getStatus())) {
            sessionAttendantsConfig.setActive(Boolean.FALSE);
        }

        if(attendantTickets.getChannelsScope() != null) {
            toMsEvent(attendantTickets.getChannelsScope(), sessionAttendantsConfig);
        }

        return sessionAttendantsConfig;
    }

    private static void toMsEvent(AttendantTicketsChannelsScopeDTO channelsScope, AttendantsConfigDTO attendantsConfig) {
        if(AttendantTicketsChannelScopeTypeDTO.ALL.equals(channelsScope.getType())) {
            attendantsConfig.setAllChannelsActive(Boolean.TRUE);
        }else if(AttendantTicketsChannelScopeTypeDTO.LIST.equals(channelsScope.getType())) {
            attendantsConfig.setAllChannelsActive(Boolean.FALSE);
        }

        if(CollectionUtils.isNotEmpty(channelsScope.getChannels())) {
            attendantsConfig.setActiveChannels(new ArrayList<>());
            for(IdNameDTO channel: channelsScope.getChannels()) {
                attendantsConfig.getActiveChannels().add(channel.getId());
            }
        }

        attendantsConfig.setAutomaticChannelAssignment(channelsScope.getAddNewEventChannelRelationships());
    }

    public static EventAttendantTicketsDTO fromMsEvent(EventAttendantsConfigDTO attendantsConfig, EventChannels channels) {
        EventAttendantTicketsDTO attendantTickets = new EventAttendantTicketsDTO();

        if (attendantsConfig == null || BooleanUtils.isNotTrue(attendantsConfig.getActive())) {
            attendantTickets.setStatus(AttendantTicketsEventStatusDTO.DISABLED);
        } else {
            attendantTickets.setStatus(AttendantTicketsEventStatusDTO.ACTIVE);
        }
        if(attendantsConfig != null) {
            attendantTickets.setAutofill(attendantsConfig.getAutofill());
            attendantTickets.setEditAttendant(attendantsConfig.getAllowAttendantsModification());
            attendantTickets.setEditAutofill(attendantsConfig.getAllowEditAutofill());
            attendantTickets.setEditAutofillDisallowedSectors(attendantsConfig.getEditAutofillDisallowedSectors());

            if (BooleanUtils.isTrue(attendantsConfig.getActive())) {
                attendantTickets.setChannelsScope(fromMsEventChannels(attendantsConfig, channels));
            }
        }
        return attendantTickets;
    }

    public static SessionAttendantTicketsDTO sessionFromMsEvent(AttendantsConfigDTO attendantsConfig, EventChannels channels) {
        SessionAttendantTicketsDTO attendantTickets = new SessionAttendantTicketsDTO();

        if(attendantsConfig == null) {
            attendantTickets.setStatus(AttendantTicketsSessionStatusDTO.EVENT_CONFIG);
        }else if(BooleanUtils.isTrue(attendantsConfig.getActive())) {
            attendantTickets.setStatus(AttendantTicketsSessionStatusDTO.ACTIVE);
        }else if(BooleanUtils.isFalse(attendantsConfig.getActive())) {
            attendantTickets.setStatus(AttendantTicketsSessionStatusDTO.DISABLED);
        }
        if(attendantsConfig != null) {
            attendantTickets.setChannelsScope(fromMsEventChannels(attendantsConfig, channels));
            attendantTickets.setAutofill(attendantsConfig.getAutofill());
            attendantTickets.setEditAutofill(attendantsConfig.getAllowEditAutofill());
        }
        return attendantTickets;
    }

    private static AttendantTicketsChannelsScopeDTO fromMsEventChannels(AttendantsConfigDTO attendantsConfig, EventChannels channels) {
        AttendantTicketsChannelsScopeDTO channelsScope = new AttendantTicketsChannelsScopeDTO();

        if(BooleanUtils.isTrue(attendantsConfig.getAllChannelsActive())) {
            channelsScope.setType(AttendantTicketsChannelScopeTypeDTO.ALL);
        } else if(BooleanUtils.isFalse(attendantsConfig.getAllChannelsActive())) {
            channelsScope.setType(AttendantTicketsChannelScopeTypeDTO.LIST);
        }
        channelsScope.setAddNewEventChannelRelationships(attendantsConfig.getAutomaticChannelAssignment());

        if(CollectionUtils.isNotEmpty(attendantsConfig.getActiveChannels())) {
            channelsScope.setChannels(new ArrayList<>());

            Map<Long, String> channelIdName = channels.getData().stream().map(BaseEventChannel::getChannel)
                    .collect(Collectors.toMap(EventChannelInfo::getId, EventChannelInfo::getName));
            for(Long channelId : attendantsConfig.getActiveChannels()) {
                if(channelIdName.containsKey(channelId)) {
                    channelsScope.getChannels().add(new IdNameDTO(channelId, channelIdName.get(channelId)));
                }
            }
        }
        return channelsScope;
    }
}
