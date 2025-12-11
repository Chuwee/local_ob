package es.onebox.mgmt.seasontickets;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPresaleDTO;
import org.apache.commons.lang3.BooleanUtils;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SeasonTicketUtils {
    private SeasonTicketUtils() {
    }

    public static void validateSeasonTicketPresaleConfig(Session session, EntityDTO entity, UpdateSeasonTicketPresaleDTO request) {
        if (isNull(request)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }

        if (session.getEventType().equals(EventType.AVET)) {
            if (nonNull(request.getMemberTicketsLimit())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_MEMBER_TICKETS_LIMIT_NOT_UPDATABLE,
                        "member_tickets_limit cannot be updated on an AVET event.", null);
            }
            if (nonNull(request.getGeneralTicketsLimit())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_GENERAL_TICKETS_LIMIT_NOT_UPDATABLE,
                        "general_tickets_limit cannot be updated on an AVET event.", null);
            }
        }

        if (nonNull(request.getLoyaltyProgram())) {
            if (nonNull(entity.getSettings()) && BooleanUtils.isNotTrue(entity.getSettings().getAllowLoyaltyPoints())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_LOYALTY_PROGRAM_NOT_ALLOWED);
            }

            if (BooleanUtils.isTrue(request.getLoyaltyProgram().getEnabled()) && request.getLoyaltyProgram().getPoints() == null) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "points can not be null", null);
            }
        }

    }
}
