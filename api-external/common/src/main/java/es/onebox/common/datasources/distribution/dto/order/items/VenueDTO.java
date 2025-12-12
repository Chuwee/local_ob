package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VenueDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("city")
    private String city;
    @JsonProperty("time_zone")
    private String timeZone;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
