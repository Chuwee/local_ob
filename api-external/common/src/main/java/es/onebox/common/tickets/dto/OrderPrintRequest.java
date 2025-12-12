package es.onebox.common.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class OrderPrintRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 8469661785154029294L;

    @JsonProperty("channel_id")
    @NotNull
    private Integer channelId;
    @JsonProperty("external_attributes")
    private transient Map<String, Object> externalAttributes;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Map<String, Object> getExternalAttributes() {
        return externalAttributes;
    }

    public void setExternalAttributes(Map<String, Object> externalAttributes) {
        this.externalAttributes = externalAttributes;
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
