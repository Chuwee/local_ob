package es.onebox.common.datasources.orders.dto;

import jakarta.validation.constraints.Min;
import java.io.Serializable;

public class LimitedFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private @Min(0L) Long limit;

    public @Min(0L) Long getLimit() {
        return limit;
    }

    public void setLimit(@Min(0L) Long limit) {
        this.limit = limit;
    }
}