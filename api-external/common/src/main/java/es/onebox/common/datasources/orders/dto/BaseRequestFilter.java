package es.onebox.common.datasources.orders.dto;

import jakarta.validation.constraints.Min;
import java.io.Serializable;

public class BaseRequestFilter extends LimitedFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private @Min(0L) Long offset;

    public @Min(0L) Long getOffset() {
        return offset;
    }

    public void setOffset(@Min(0L) Long offset) {
        this.offset = offset;
    }
}
