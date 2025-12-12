package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import java.io.Serial;
import java.io.Serializable;

public class TicketAttachment implements Serializable {

    @Serial
    private static final long serialVersionUID = -4381268186257438921L;

    private Integer id;
    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
