package es.onebox.common.datasources.ms.ticket.repository;

import es.onebox.common.datasources.common.dto.SeatStatus;
import es.onebox.common.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.common.datasources.ms.ticket.dto.ExternalMode;
import es.onebox.common.datasources.ms.ticket.dto.OrderItemPrint;
import es.onebox.common.datasources.ms.ticket.dto.PdfTicketDetails;
import es.onebox.venue.venuetemplates.VenueMapProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MsTicketRepository {
    @Autowired
    private MsTicketDatasource msTicketDatasource;

    public VenueMapProto.VenueMap getCapacityMap(Long eventId, Long sessionId, List<Long> sectorIds) {
        return msTicketDatasource.getCapacityMap(eventId, sessionId, sectorIds);
    }

    public void updateTicket(Long sessionId, Long id, SeatStatus status, Long blockingReasonId) {
        msTicketDatasource.updateTicket(sessionId, id, status, blockingReasonId);
    }

    public PdfTicketDetails getOrderMergedTickets(String orderCode) {
        return msTicketDatasource.getOrderMergedTickets(orderCode);
    }

    public OrderItemPrint getItemPassbook(String orderCode, Long itemId, ExternalMode externalMode) {
        return msTicketDatasource.getItemPassbook(orderCode, itemId, externalMode);
    }
}
