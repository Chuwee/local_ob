package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class OrderPrintDetailDTO implements Serializable {

    private String code;
    @JsonProperty("ticket_url")
    private String ticketUrl;
    private PdfStatus status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public PdfStatus getStatus() {
        return status;
    }

    public void setStatus(PdfStatus status) {
        this.status = status;
    }
}
