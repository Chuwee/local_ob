package es.onebox.common.datasources.orders.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class Sale extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private SaleActivator activator;

    public SaleActivator getActivator() {
        return activator;
    }

    public void setActivator(SaleActivator activator) {
        this.activator = activator;
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
