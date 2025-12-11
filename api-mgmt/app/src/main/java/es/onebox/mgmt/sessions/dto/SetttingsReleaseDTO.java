package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SetttingsReleaseDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("date")
    private ZonedDateTimeWithRelative date;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ZonedDateTimeWithRelative getDate() {
        return date;
    }

    public void setDate(ZonedDateTimeWithRelative date) {
        this.date = date;
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
