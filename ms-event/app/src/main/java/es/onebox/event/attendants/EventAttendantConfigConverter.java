package es.onebox.event.attendants;

import es.onebox.event.attendants.domain.EventAttendantsConfig;
import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.ModifyEventAttendantsConfigDTO;

public class EventAttendantConfigConverter {

    private EventAttendantConfigConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventAttendantsConfigDTO toDTO(EventAttendantsConfig attendantsConfig) {
        if (attendantsConfig == null) {
            return null;
        }
        EventAttendantsConfigDTO dto = new EventAttendantsConfigDTO();
        dto.setActive(attendantsConfig.isActive());
        dto.setEventId(attendantsConfig.getEventId());
        dto.setActiveChannels(attendantsConfig.getActiveChannels());
        dto.setAllChannelsActive(attendantsConfig.isAllChannelsActive());
        dto.setAutomaticChannelAssignment(attendantsConfig.isAutomaticChannelAssignment());
        dto.setAutofill(attendantsConfig.isAutofill());
        dto.setAllowEditAutofill(attendantsConfig.isAllowEditAutofill());
        dto.setAllowAttendantsModification(attendantsConfig.getAllowAttendantsModification());
        dto.setEditAutofillDisallowedSectors(attendantsConfig.getEditAutofillDisallowedSectors());
        return dto;
    }

    public static EventAttendantsConfig toEntity(Long eventId, ModifyEventAttendantsConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        EventAttendantsConfig entity = new EventAttendantsConfig();
        entity.setActive(dto.getActive());
        entity.setEventId(eventId);
        entity.setActiveChannels(dto.getActiveChannels());
        entity.setAllChannelsActive(dto.getAllChannelsActive());
        entity.setAutomaticChannelAssignment(dto.getAutomaticChannelAssignment());
        entity.setAutofill(dto.getAutofill());
        entity.setAllowAttendantsModification(dto.getAllowAttendantsModification());
        entity.setAllowEditAutofill(dto.getAllowEditAutofill());
        entity.setEditAutofillDisallowedSectors(dto.getEditAutofillDisallowedSectors());
        return entity;
    }

    public static AttendantsConfig toAttendantConfig(EventAttendantsConfigDTO in) {
        if (in == null) {
            return null;
        }
        AttendantsConfig attendantsConfig = new AttendantsConfig();
        attendantsConfig.setAllowEditAutofill(in.getAllowEditAutofill());
        attendantsConfig.setAutofill(in.getAutofill());
        attendantsConfig.setEditAutofillDisallowedSectors(in.getEditAutofillDisallowedSectors());
        return attendantsConfig;
    }

}
