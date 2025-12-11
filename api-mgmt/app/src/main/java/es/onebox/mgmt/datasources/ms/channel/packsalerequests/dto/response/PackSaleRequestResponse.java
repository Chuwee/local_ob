package es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class PackSaleRequestResponse extends ListWithMetadata<PackSalesRequestBase> {

    @Serial
    private static final long serialVersionUID = 3653852706783725156L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
