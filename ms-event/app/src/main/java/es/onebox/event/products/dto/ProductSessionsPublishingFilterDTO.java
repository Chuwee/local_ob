package es.onebox.event.products.dto;

import java.io.Serializable;
import java.util.List;

public class ProductSessionsPublishingFilterDTO implements Serializable {

    private List<Long> sessionIds;

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }
}
