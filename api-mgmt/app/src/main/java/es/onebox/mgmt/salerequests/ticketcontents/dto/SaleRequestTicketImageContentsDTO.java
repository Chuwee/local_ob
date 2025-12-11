package es.onebox.mgmt.salerequests.ticketcontents.dto;

import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketContentImageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class SaleRequestTicketImageContentsDTO extends ArrayList<SaleRequestTicketImageContentDTO<SaleRequestTicketContentImageType>> implements Serializable {
    @Serial
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
