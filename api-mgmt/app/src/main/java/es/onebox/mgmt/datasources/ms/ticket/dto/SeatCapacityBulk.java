package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagSeatDTO;

import java.util.List;

public class SeatCapacityBulk extends CapacityBulk<VenueTagSeatDTO> {
    public SeatCapacityBulk(List<Long> sessionIds, List<VenueTagSeatDTO> values) {
        super(sessionIds, values);
    }
}
