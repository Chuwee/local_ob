package es.onebox.event.attendants;

import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;

public class SessionAttendantConverter {

    private SessionAttendantConverter() {
    }

    public static SessionAttendantsConfigDTO toDTO(SessionAttendantsConfig attendants) {
        SessionAttendantsConfigDTO sessionAttendantsDTO = new SessionAttendantsConfigDTO();
        sessionAttendantsDTO.setActive(attendants.isActive());
        sessionAttendantsDTO.setSessionId(attendants.getSessionId());
        sessionAttendantsDTO.setActiveChannels(attendants.getActiveChannels());
        sessionAttendantsDTO.setAllChannelsActive(attendants.isAllChannelsActive());
        sessionAttendantsDTO.setAutomaticChannelAssignment(attendants.isAutomaticChannelAssignment());
        sessionAttendantsDTO.setAutofill(attendants.isAutofill());
        sessionAttendantsDTO.setEditAutofillDisallowedSectors(attendants.getEditAutofillDisallowedSectors());
        return sessionAttendantsDTO;
    }

    public static SessionAttendantsConfig toEntity(SessionAttendantsConfigDTO sessionAttendantsDTO) {
        SessionAttendantsConfig attendants = new SessionAttendantsConfig();
        attendants.setActive(sessionAttendantsDTO.getActive());
        attendants.setSessionId(sessionAttendantsDTO.getSessionId());
        attendants.setActiveChannels(sessionAttendantsDTO.getActiveChannels());
        attendants.setAllChannelsActive(sessionAttendantsDTO.getAllChannelsActive());
        attendants.setAutomaticChannelAssignment(sessionAttendantsDTO.getAutomaticChannelAssignment());
        attendants.setAutofill(sessionAttendantsDTO.getAutofill());
        attendants.setEditAutofillDisallowedSectors(sessionAttendantsDTO.getEditAutofillDisallowedSectors());
        return attendants;
    }

}
