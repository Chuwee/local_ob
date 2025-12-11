package es.onebox.mgmt.export.converter;

import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityExportFilter;
import es.onebox.mgmt.sessions.dto.CapacityExportRequest;
import es.onebox.mgmt.users.dto.UserSelfDTO;

import java.util.List;

public class CapacityExportConverter {
    
    private CapacityExportConverter() {
    }
    
    public static CapacityExportFilter convert(CapacityExportRequest request, UserSelfDTO user, Long sessionId,
                                               Long venueTemplateId, List<Long> viewIds, List<Long> sectorIds) {
        CapacityExportFilter out = new CapacityExportFilter();
        ExportConverter.fillFilter(out, request, user, null, null);
        out.setSessionId(sessionId);
        out.setVenueTemplateId(venueTemplateId);
        out.setViewIds(viewIds);
        out.setSectorIds(sectorIds);
        return out;
    }
    
    
}
