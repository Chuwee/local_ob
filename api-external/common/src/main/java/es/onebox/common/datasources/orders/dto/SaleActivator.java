package es.onebox.common.datasources.orders.dto;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SaleActivator extends CodeDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO collective;

    public IdNameDTO getCollective() {
        return collective;
    }

    public void setCollective(IdNameDTO collective) {
        this.collective = collective;
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
