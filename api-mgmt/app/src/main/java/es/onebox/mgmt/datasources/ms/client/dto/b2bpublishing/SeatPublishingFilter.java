package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.time.ZonedDateTime;

public class SeatPublishingFilter extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime date;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
