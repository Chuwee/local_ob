package es.onebox.event.datasources.ms.ticket.repository;

import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.datasources.ms.ticket.dto.LinkSessionCapacityResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionCompatibilityValidationResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionUnlinkResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.SeasonTicketRenewalResponse;
import es.onebox.event.seasontickets.amqp.renewals.relatedseats.RenewalsUpdateRelatedSeatsRequestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeasonTicketRepository {

    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public SeasonTicketRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public SessionCompatibilityValidationResponse validateSessionCompatibility(Long seasonSessionId, Long targetSessionId) {
        return msTicketDatasource.validateSessionCompatibility(seasonSessionId, Boolean.TRUE, targetSessionId);
    }

    public LinkSessionCapacityResponse linkSessionCapacity(Long sessionId, Long targetSessionId, Boolean updateBarcodes) {
        return msTicketDatasource.linkSessionCapacity(sessionId, targetSessionId, updateBarcodes);
    }

    public SessionUnlinkResponse unLinkSessionCapacity(Long sessionId, Long targetSessionId, Boolean updateBarcodes) {
        return msTicketDatasource.unLinkSessionCapacity(sessionId, targetSessionId,updateBarcodes);
    }

    public SeasonTicketRenewalResponse renewalSeasonTicket(Long renewalSeasonTicketSessionId, Long originSeasonTicketSessionId,
                                                           List<RenewalSeasonTicketOriginSeat> originSeats) {
        return msTicketDatasource.renewalSeasonTicket(renewalSeasonTicketSessionId, originSeasonTicketSessionId, originSeats);
    }

    public UpdateRelatedSeatsResponse updateRelatedSeasonTicketSeatsStatus(Long renewalSeasonTicketSessionId, List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats, List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats) {
        return msTicketDatasource.updateRelatedSeasonTicketSeatsStatus(renewalSeasonTicketSessionId, blockSeats, unblockSeats);
    }

    public void updateBarcodes(Long seasonTicketSessionId) {
        msTicketDatasource.updateBarcodes(seasonTicketSessionId);
    }
}