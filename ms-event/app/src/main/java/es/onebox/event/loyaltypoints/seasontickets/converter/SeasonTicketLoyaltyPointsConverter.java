package es.onebox.event.loyaltypoints.seasontickets.converter;

import es.onebox.event.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.seasontickets.dto.SessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.seasontickets.domain.SeasonTicketLoyaltyPointsConfig;
import es.onebox.event.loyaltypoints.seasontickets.domain.SessionLoyaltyPoints;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeasonTicketLoyaltyPointsConverter {

    private SeasonTicketLoyaltyPointsConverter() {
    }

    public static SeasonTicketLoyaltyPointsConfigDTO toDTO(SeasonTicketLoyaltyPointsConfig document) {
        SeasonTicketLoyaltyPointsConfigDTO dto = new SeasonTicketLoyaltyPointsConfigDTO();

        if (CollectionUtils.isEmpty(document.getSessions())) {
            return dto;
        }

        List<SessionLoyaltyPointsConfigDTO> sessionsDto =  document.getSessions().stream()
                .map(SeasonTicketLoyaltyPointsConverter::toDTO)
                .toList();

        dto.setSessions(sessionsDto);
        return dto;
    }

    public static void updateSeasonTicketLoyaltyPointsConfig(List<SessionResultDTO> sessions, SeasonTicketLoyaltyPointsConfig config,
                                                             SeasonTicketLoyaltyPointsConfigDTO seasonTicketLoyaltyPointsConfigDTO) {
        if (seasonTicketLoyaltyPointsConfigDTO == null || CollectionUtils.isEmpty(seasonTicketLoyaltyPointsConfigDTO.getSessions())
                || CollectionUtils.isEmpty(sessions)) {
            return;
        }

        Map<Long, SessionLoyaltyPointsConfigDTO> sessionsDto = seasonTicketLoyaltyPointsConfigDTO.getSessions().stream()
                .collect(Collectors.toMap(SessionLoyaltyPointsConfigDTO::getSessionId, Function.identity()));
        Set<Long> validSessionIds = sessions.stream()
                .map(s -> Long.valueOf(s.getSessionId()))
                .collect(Collectors.toSet());

        List<SessionLoyaltyPoints> existingSessions = config.getSessions();
        List<SessionLoyaltyPoints> updatedSessions;
        if (CollectionUtils.isEmpty(existingSessions)) {
            updatedSessions = seasonTicketLoyaltyPointsConfigDTO.getSessions().stream()
                    .filter(session -> validSessionIds.contains(session.getSessionId()))
                    .map(SeasonTicketLoyaltyPointsConverter::toDTO)
                    .toList();
        } else {
            updatedSessions = existingSessions.stream()
                    .map(session -> Optional.ofNullable(sessionsDto.get(session.getSessionId()))
                            .map(SeasonTicketLoyaltyPointsConverter::toDTO)
                            .orElse(session))
                    .toList();
        }

        config.setSessions(updatedSessions);
    }

    private static SessionLoyaltyPointsConfigDTO toDTO(SessionLoyaltyPoints in) {
        if (in == null) {
            return null;
        }
        SessionLoyaltyPointsConfigDTO out = new SessionLoyaltyPointsConfigDTO();
        out.setSessionId(in.getSessionId());
        out.setTransfer(in.getTransfer());
        out.setAttendance(in.getAttendance());
        return out;
    }

    private static SessionLoyaltyPoints toDTO(SessionLoyaltyPointsConfigDTO in) {
        if (in == null) {
            return null;
        }
        SessionLoyaltyPoints out = new SessionLoyaltyPoints();
        out.setSessionId(in.getSessionId());
        out.setTransfer(in.getTransfer());
        out.setAttendance(in.getAttendance());
        return out;
    }
}
