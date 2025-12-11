package es.onebox.mgmt.channels.blacklists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.ChannelBlacklistValue;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelBlacklistDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ChannelBlacklistValue
    @NotBlank(message = "value must be not blank")
    private String value;

    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
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
