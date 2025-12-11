package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;
import java.util.List;

public class PromotionScopeFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean filtered;
    private List<Long> ids;

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
