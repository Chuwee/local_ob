package es.onebox.mgmt.channels.ticketcontents.dto;

import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPrinterImageContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelTicketPrinterImageContentDTO extends ChannelTicketImageContentDTO<ChannelTicketPrinterImageContentType> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
