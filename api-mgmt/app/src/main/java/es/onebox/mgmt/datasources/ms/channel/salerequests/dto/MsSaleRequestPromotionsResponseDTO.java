package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MsSaleRequestPromotionsResponseDTO extends BaseResponseCollection<MsSaleRequestPromotionsDTO, Metadata> implements Serializable{

    private static final long serialVersionUID = 1487662332418456925L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
