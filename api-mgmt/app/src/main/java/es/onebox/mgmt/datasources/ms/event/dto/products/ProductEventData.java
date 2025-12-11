package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductEventData extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ZonedDateTime startDate;

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
