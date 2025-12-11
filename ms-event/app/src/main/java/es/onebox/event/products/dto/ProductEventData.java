package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductEventData extends IdNameDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4574528968762622154L;

    private ZonedDateTime startDate;

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
