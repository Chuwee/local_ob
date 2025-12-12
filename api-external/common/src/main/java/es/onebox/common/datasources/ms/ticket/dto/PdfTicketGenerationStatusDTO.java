package es.onebox.common.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class PdfTicketGenerationStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4729345488971142209L;

    private PdfTicketGenerationStatus status;

    public PdfTicketGenerationStatus getStatus() {
        return status;
    }

    public void setStatus(PdfTicketGenerationStatus status) {
        this.status = status;
    }
}
