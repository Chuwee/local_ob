package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;

public class TicketPrintResult implements Serializable {

    private String downloadUrl;

    public TicketPrintResult() {
    }

    public TicketPrintResult(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
