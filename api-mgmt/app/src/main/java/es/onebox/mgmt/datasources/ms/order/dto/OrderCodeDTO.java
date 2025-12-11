package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.datasources.ms.order.enums.BarcodeOrderProvider;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class OrderCodeDTO extends CodeDTO implements Serializable {

    private BarcodeOrderProvider provider;

    public BarcodeOrderProvider getProvider() {
        return provider;
    }

    public void setProvider(BarcodeOrderProvider provider) {
        this.provider = provider;
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
