package es.onebox.common.datasources.accesscontrol.repository;

import es.onebox.common.datasources.accesscontrol.ApiAccessControlDatasource;
import es.onebox.common.datasources.accesscontrol.dto.ACTicketResponse;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListDTO;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListFilter;
import es.onebox.common.datasources.accesscontrol.dto.TicketFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccessControlRepository {

    @Autowired
    private ApiAccessControlDatasource apiAccessControlDatasource;

    public ACTicketResponse getTickets(TicketFilter filter, String token) {
        return apiAccessControlDatasource.getTickets(filter, token);
    }

    public BarcodeListDTO getWhitelist(String token, Long eventId, Long sessionId, BarcodeListFilter filter) {
        return apiAccessControlDatasource.getWhitelist(token, eventId, sessionId, filter);
    }
}
