package es.onebox.event.seasontickets.converter;

import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionCommunicationElement;
import es.onebox.event.seasontickets.dto.SeasonTicketSessionDTO;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketSessionsConverter {

    private SeasonTicketSessionsConverter() {

    }

    public static List<SeasonTicketSessionDTO> convert(List<SessionResultDTO> sessionResults, TimeZoneDTO beginSessionDateTZ,
                                                       Integer seasonTicketSessionId) {
        return sessionResults.stream()
                .map(r-> convert(r, beginSessionDateTZ, seasonTicketSessionId))
                .collect(Collectors.toList());
    }

    public static SeasonTicketSessionDTO convert(SessionResultDTO sessionResult, TimeZoneDTO beginSessionDateTZ,
                                                 Integer seasonTicketSessionId) {
        SeasonTicketSessionDTO result = new SeasonTicketSessionDTO();
        result.setEventId(sessionResult.getEventId());
        result.setEventName(sessionResult.getEventName());
        result.setSessionId(sessionResult.getSessionId());
        result.setSessionName(sessionResult.getSessionName());
        result.setBeginSessionDate(DateUtils.getZonedDateTime(sessionResult.getBeginSessionDate()));
        result.setRealEndSessionDate(DateUtils.getZonedDateTime(sessionResult.getRealEndSessionDate()));
        result.setBeginSessionDateTZ(beginSessionDateTZ);
        result.setSessionAssignable(SessionAssignableConverter.convert(sessionResult));
        result.setStatus(setStatus(sessionResult, seasonTicketSessionId));
        result.setCommunicationElements(convert(sessionResult.getCommunicationElements()));
        return result;
    }

    public static SessionAssignationStatusDTO setStatus(SessionResultDTO sessionResult, Integer seasonTicketSessionId) {
        return sessionResult.getRelatedSeasonSessionIds() != null &&
                sessionResult.getRelatedSeasonSessionIds().contains(seasonTicketSessionId) ?
                SessionAssignationStatusDTO.ASSIGNED : SessionAssignationStatusDTO.NOT_ASSIGNED;
    }

    private static List<SeasonTicketSessionCommunicationElement> convert(List<SessionCommunicationElement> communicationElements) {
        if(communicationElements == null) {
            return null;
        }
        return communicationElements.stream().map(SeasonTicketSessionsConverter::convert).collect(Collectors.toList());
    }

    private static SeasonTicketSessionCommunicationElement convert(SessionCommunicationElement communicationElement) {
        if(communicationElement == null) {
            return null;
        }
        SeasonTicketSessionCommunicationElement seasonTicketSessionCommunicationElement = new SeasonTicketSessionCommunicationElement();
        seasonTicketSessionCommunicationElement.setTag(communicationElement.getTag());
        seasonTicketSessionCommunicationElement.setLanguage(communicationElement.getLanguageCode());
        seasonTicketSessionCommunicationElement.setValue(communicationElement.getValue());
        return seasonTicketSessionCommunicationElement;
    }
}

