package es.onebox.mgmt.channels.tickettemplates.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelTicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private ChannelTemplateTicketType type;
    private ChannelTicketTemplateFormat format;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChannelTemplateTicketType getType() {
        return type;
    }

    public void setType(ChannelTemplateTicketType type) {
        this.type = type;
    }

    public ChannelTicketTemplateFormat getFormat() {
        return format;
    }

    public void setFormat(ChannelTicketTemplateFormat format) {
        this.format = format;
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
