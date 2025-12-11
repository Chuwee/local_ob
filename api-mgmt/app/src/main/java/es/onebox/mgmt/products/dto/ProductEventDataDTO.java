package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductEventDataDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "start_date")
    private ZonedDateTime startDate;

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
