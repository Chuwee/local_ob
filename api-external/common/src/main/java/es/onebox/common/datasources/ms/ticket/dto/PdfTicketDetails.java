package es.onebox.common.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class PdfTicketDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 5978000848644558752L;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
