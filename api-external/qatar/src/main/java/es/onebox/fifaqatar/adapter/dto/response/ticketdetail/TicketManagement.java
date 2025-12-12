package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import java.io.Serial;
import java.io.Serializable;

public class TicketManagement implements Serializable {

    @Serial
    private static final long serialVersionUID = 4883875271631445679L;

    private String url;
    private String title;
    private String subtitle;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
