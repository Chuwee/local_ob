package es.onebox.mgmt.channels.externaltools.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.onebox.mgmt.channels.externaltools.deserializer.ChannelExternalToolFieldDeserializer;
import es.onebox.mgmt.channels.externaltools.serializer.ChannelExternalToolFieldSerializer;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;


public class ChannelExternalToolFieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = ChannelExternalToolFieldDeserializer.class)
    @JsonSerialize(using = ChannelExternalToolFieldSerializer.class)
    @NotNull(message = "id is mandatory")
    private ChannelExternalToolIdentifierDTO id;
    @NotNull(message = "value is mandatory")
    private String value;

    public ChannelExternalToolIdentifierDTO getId() {
        return id;
    }

    public void setId(ChannelExternalToolIdentifierDTO id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
