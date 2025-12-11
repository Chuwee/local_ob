package es.onebox.event.attendants;

import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.attendants.dto.ModifySessionAttendantsConfigDTO;

public class SessionAttendantConfigConverter {

    private SessionAttendantConfigConverter() {
    }

    public static SessionAttendantsConfigDTO toDTO(SessionAttendantsConfig entity) {
        if (entity == null) {
            return null;
        }
        SessionAttendantsConfigDTO dto = new SessionAttendantsConfigDTO();
        dto.setActive(entity.isActive());
        dto.setSessionId(entity.getSessionId());
        dto.setActiveChannels(entity.getActiveChannels());
        dto.setAllChannelsActive(entity.isAllChannelsActive());
        dto.setAutomaticChannelAssignment(entity.isAutomaticChannelAssignment());
        dto.setAutofill(entity.isAutofill());
        dto.setAllowEditAutofill(entity.isAllowEditAutofill());
        dto.setEditAutofillDisallowedSectors(entity.getEditAutofillDisallowedSectors());
        return dto;
    }

    public static SessionAttendantsConfig toEntity(Long sessionId, ModifySessionAttendantsConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        SessionAttendantsConfig entity = new SessionAttendantsConfig();
        entity.setActive(dto.getActive());
        entity.setSessionId(sessionId);
        entity.setActiveChannels(dto.getActiveChannels());
        entity.setAllChannelsActive(dto.getAllChannelsActive());
        entity.setAutomaticChannelAssignment(dto.getAutomaticChannelAssignment());
        entity.setAutofill(dto.getAutofill());
        entity.setAllowEditAutofill(dto.getAllowEditAutofill());
        entity.setEditAutofillDisallowedSectors(dto.getEditAutofillDisallowedSectors());
        return entity;
    }

}
