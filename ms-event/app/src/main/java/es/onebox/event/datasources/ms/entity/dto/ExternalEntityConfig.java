package es.onebox.event.datasources.ms.entity.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ExternalEntityConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 8765505940682026603L;

    private Long id;
    private SmartBookingConfig smartBooking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmartBookingConfig getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(SmartBookingConfig smartBooking) {
        this.smartBooking = smartBooking;
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
