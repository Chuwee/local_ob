package es.onebox.mgmt.datasources.ms.ticket.repository;

import es.onebox.mgmt.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreview;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TicketPreviewRepository {

    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public TicketPreviewRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public TicketPreview getTicketPdfPreview(TicketPreviewRequest request) {
        return msTicketDatasource.getTicketPdfPreview(request);
    }
}
