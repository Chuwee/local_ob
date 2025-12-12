package es.onebox.flc.sessions.converter;

import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.flc.sessions.dto.Session;

import java.util.List;
import java.util.stream.Collectors;

public class SessionConverter {

    private SessionConverter() {
    }

    public static List<Session> convert(SessionsDTO sessionsDTO) {
        return sessionsDTO.getData()
                .stream()
                .map(SessionConverter::convert)
                .collect(Collectors.toList());
    }

    public static Session convert(SessionDTO in) {
        Session out = new Session();

        out.setId(in.getId());
        out.setName(in.getName());
        if (in.getDate() != null && in.getDate().getStart() != null) {
            out.setStartDate(in.getDate().getStart());
            out.setTimeZone(in.getDate().getStart().getZone().getId());
        }
        if (in.getDate() != null && in.getDate().getEnd() != null) {
            out.setEndDate(in.getDate().getEnd());
        }
        out.setReference(in.getReference());
        out.setEventId(in.getEventId());
        out.setSpaceId(in.getVenueConfigSpaceId());
        out.setVenueId(in.getVenueId());
        if (in.getSpace() != null) {
            out.setAccessValidationSpaceId(in.getSpace().getId());
        } else {
            out.setAccessValidationSpaceId(in.getVenueConfigSpaceId());
        }

        return out;
    }
}
