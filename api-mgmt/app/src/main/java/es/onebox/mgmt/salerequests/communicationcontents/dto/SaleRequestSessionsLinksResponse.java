package es.onebox.mgmt.salerequests.communicationcontents.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SaleRequestSessionsLinksResponse extends ListWithMetadata<SaleRequestEventChannelContentSessionLinkDTO> {

    @Serial
    private static final long serialVersionUID = -2382133022872417768L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
