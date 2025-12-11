package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TicketPrintResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("download_url")
    private String downloadUrl;

    public TicketPrintResultDTO() {}

    public TicketPrintResultDTO(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
