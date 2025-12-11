package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagNotNumberedZoneDTO;

import java.util.List;

public class NotNumberedZoneCapacityBulk extends CapacityBulk<VenueTagNotNumberedZoneDTO> {
    public NotNumberedZoneCapacityBulk(List<Long> sessionIds, List<VenueTagNotNumberedZoneDTO> values) {
        super(sessionIds, values);
    }
}
