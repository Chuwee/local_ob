package es.onebox.mgmt.channels.notifications.dto;

import es.onebox.mgmt.channels.notifications.enums.ChannelEmailTemplateType;
import es.onebox.mgmt.validation.annotation.EmailList;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ChannelEmailTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Email(message = "from must be a well-formed email address")
    @NotNull(message = "from must be not null")
    private String from;

    @EmailList
    private String cco;

    private String alias;

    @NotNull(message = "type must be not null")
    private ChannelEmailTemplateType type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCco() {
        return cco;
    }

    public void setCco(String cco) {
        this.cco = cco;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ChannelEmailTemplateType getType() {
        return type;
    }

    public void setType(ChannelEmailTemplateType type) {
        this.type = type;
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
