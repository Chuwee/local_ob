package es.onebox.event.datasources.ms.ticket.repository;

import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.datasources.ms.ticket.dto.TicketsSearchResponse;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchFilter;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchResponse;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketsRepository {
    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public TicketsRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public TicketsSearchResponse getTickets(Long sessionId, List<Long> seatIds, List<TicketStatus> ticketStatus) {
        return msTicketDatasource.getTickets(sessionId, seatIds, ticketStatus);
    }

    public List<SecondaryMarketSearch> getSecondaryMarketLocations(SecondaryMarketSearchFilter secondaryMarketSearchRequest) {
        Boolean filledTickets = false;
        List<SecondaryMarketSearch> partialResults = new ArrayList<>();
        secondaryMarketSearchRequest.setOffset(0L);
        secondaryMarketSearchRequest.setLimit(1000L);

        SecondaryMarketSearchResponse secondaryMarketSearchResponse = msTicketDatasource.getSecondaryMarketLocations(secondaryMarketSearchRequest);
        do {
            if(secondaryMarketSearchResponse != null && secondaryMarketSearchResponse.getData() != null) {
                partialResults.addAll(secondaryMarketSearchResponse.getData());
                if(secondaryMarketSearchResponse.getMetadata() != null && secondaryMarketSearchResponse.getMetadata().getTotal() != null
                        && secondaryMarketSearchResponse.getMetadata().getOffset() != null && secondaryMarketSearchResponse.getMetadata().getTotal() > (secondaryMarketSearchRequest.getOffset() + secondaryMarketSearchRequest.getLimit())) {
                    secondaryMarketSearchRequest.setOffset(secondaryMarketSearchRequest.getOffset() + secondaryMarketSearchRequest.getLimit());
                    secondaryMarketSearchResponse = msTicketDatasource.getSecondaryMarketLocations(secondaryMarketSearchRequest);
                } else {
                    filledTickets = true;
                }
            }
        } while(secondaryMarketSearchResponse != null && secondaryMarketSearchResponse.getData() != null && !secondaryMarketSearchResponse.getData().isEmpty() && !filledTickets);

        return partialResults;
    }

    public Long getSecondaryMarketLocationsCount(SecondaryMarketSearchFilter secondaryMarketSearchRequest) {
        secondaryMarketSearchRequest.setOffset(0L);
        secondaryMarketSearchRequest.setLimit(1L);
        SecondaryMarketSearchResponse secondaryMarketSearchResponse = msTicketDatasource.getSecondaryMarketLocations(secondaryMarketSearchRequest);
        return secondaryMarketSearchResponse != null && secondaryMarketSearchResponse.getMetadata() != null ? secondaryMarketSearchResponse.getMetadata().getTotal() : 0L;
    }
}
