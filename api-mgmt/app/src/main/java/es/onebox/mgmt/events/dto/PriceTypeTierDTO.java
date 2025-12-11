package es.onebox.mgmt.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PriceTypeTierDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public PriceTypeTierDTO() {
    }

    public PriceTypeTierDTO(Long id, String name) {
        super(id, name);
    }


    public PriceTypeTierDTO(Long id, String name, Long capacity) {
        super(id, name);
        this.capacity = capacity;
    }

    private Long capacity;

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
