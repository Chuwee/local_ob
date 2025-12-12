package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrderDateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7882366156746109732L;

    private ZonedDateTime purchased;
    private String timeZone;


    public ZonedDateTime getPurchased() {
        return purchased;
    }

    public void setPurchased(ZonedDateTime purchased) {
        this.purchased = purchased;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
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
