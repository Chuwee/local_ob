package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

public class BarcodeSessionDataDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime start;

    public BarcodeSessionDataDTO() {}

    public BarcodeSessionDataDTO(Long id, String name, ZonedDateTime start) {
        super(id, name);
        this.start = start;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
