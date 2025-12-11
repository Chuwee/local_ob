package es.onebox.mgmt.loyaltypoints.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.*;
import es.onebox.mgmt.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.seasontickets.dto.SessionLoyaltyPointsDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SeasonTicketLoyaltyPointsConverter {
    private SeasonTicketLoyaltyPointsConverter() {}

    public static SeasonTicketLoyaltyPointsConfigDTO toDTO(SeasonTicketLoyaltyPointsConfig seasonTicketLoyaltyPointsConfig) {
        SeasonTicketLoyaltyPointsConfigDTO dto = new SeasonTicketLoyaltyPointsConfigDTO();
        if (seasonTicketLoyaltyPointsConfig == null || CollectionUtils.isEmpty(seasonTicketLoyaltyPointsConfig.getSessions())) {
            return dto;
        }
        List<SessionLoyaltyPointsDTO> sessionDTOs = seasonTicketLoyaltyPointsConfig.getSessions().stream()
                .map(SeasonTicketLoyaltyPointsConverter::toDTO)
                .toList();
        dto.setSessions(sessionDTOs);
        return dto;
    }

    public static SeasonTicketLoyaltyPointsConfig toMs(SeasonTicketLoyaltyPointsConfigDTO sessionsUpdate) {
        SeasonTicketLoyaltyPointsConfig entity = new SeasonTicketLoyaltyPointsConfig();
        if (sessionsUpdate == null || CollectionUtils.isEmpty(sessionsUpdate.getSessions())) {
            return entity;
        }
        List<SessionLoyaltyPoints> sessions = sessionsUpdate.getSessions().stream()
                .map(SeasonTicketLoyaltyPointsConverter::toDTO).toList();
        entity.setSessions(sessions);
        return entity;
    }

    private static SessionLoyaltyPointsDTO toDTO(SessionLoyaltyPoints in) {
        SessionLoyaltyPointsDTO out = new SessionLoyaltyPointsDTO();
        out.setSessionId(in.getSessionId());
        out.setTransfer(in.getTransfer());
        out.setAttendance(in.getAttendance());
        return out;
    }

    private static SessionLoyaltyPoints toDTO(SessionLoyaltyPointsDTO in) {
        SessionLoyaltyPoints out = new SessionLoyaltyPoints();
        out.setSessionId(in.getSessionId());
        out.setTransfer(in.getTransfer());
        out.setAttendance(in.getAttendance());
        return out;
    }
}