package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;
import java.io.Serializable;

public class QuotaCounter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long quotaId;
    private Long available;
    private Long count;

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}