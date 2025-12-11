package es.onebox.mgmt.packsalerequest.dto.response;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelSaleRequestDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9208198331536421916L;

    private IdNameDTO entity;

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static class PackSaleRequestBaseDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 7855964412379656530L;

    }
}
