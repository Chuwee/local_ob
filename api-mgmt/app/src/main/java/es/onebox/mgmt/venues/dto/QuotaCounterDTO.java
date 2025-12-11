package es.onebox.mgmt.venues.dto;

import java.io.Serial;
import java.io.Serializable;

public class QuotaCounterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long quota;
    private Long available;
    private Long count;

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
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