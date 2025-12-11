package es.onebox.mgmt.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ExpirationUnit;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RedemptionExpiration;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketRedemption;
import es.onebox.mgmt.seasontickets.dto.redemption.ExpirationUnitDTO;
import es.onebox.mgmt.seasontickets.dto.redemption.RedemptionExpirationDTO;
import es.onebox.mgmt.seasontickets.dto.redemption.UpdateSeasonTicketRedemptionDTO;
import es.onebox.mgmt.seasontickets.dto.redemption.SeasonTicketRedemptionConfigDTO;

public class SeasonTicketRedemptionConverter {

    private SeasonTicketRedemptionConverter() {
    }

    public static SeasonTicketRedemptionConfigDTO fromMs(SeasonTicketRedemption ms) {
        if (ms == null) {
            return null;
        }

        SeasonTicketRedemptionConfigDTO dto = new SeasonTicketRedemptionConfigDTO();
        dto.setEnabled(ms.getEnabled());
        dto.setExcludedSessions(ms.getExcludedSessions());
        dto.setExpiration(convertExpirationFromMs(ms.getExpiration()));
        return dto;
    }

    public static UpdateSeasonTicketRedemption toMs(UpdateSeasonTicketRedemptionDTO request) {
        if (request == null) {
            return null;
        }

        UpdateSeasonTicketRedemption ms = new UpdateSeasonTicketRedemption();
        ms.setEnabled(request.getEnabled());
        ms.setExcludedSessions(request.getExcludedSessions());
        ms.setExpiration(convertExpirationToMs(request.getExpiration()));
        return ms;
    }

    private static RedemptionExpirationDTO convertExpirationFromMs(RedemptionExpiration msExpiration) {
        if (msExpiration == null) {
            return null;
        }

        RedemptionExpirationDTO dto = new RedemptionExpirationDTO();
        if (msExpiration.getUnit() != null) {
            dto.setUnit(ExpirationUnitDTO.valueOf(msExpiration.getUnit().name()));
        }
        dto.setExpires(msExpiration.getExpires());
        return dto;
    }

    private static RedemptionExpiration convertExpirationToMs(RedemptionExpirationDTO dtoExpiration) {
        if (dtoExpiration == null) {
            return null;
        }

        RedemptionExpiration ms = new RedemptionExpiration();
        if (dtoExpiration.getUnit() != null) {
            ms.setUnit(ExpirationUnit.valueOf(dtoExpiration.getUnit().name()));
        }
        ms.setExpires(dtoExpiration.getExpires());
        return ms;
    }
}
