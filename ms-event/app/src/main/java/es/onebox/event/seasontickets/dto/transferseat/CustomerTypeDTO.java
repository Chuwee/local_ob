package es.onebox.event.seasontickets.dto.transferseat;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;


public class CustomerTypeDTO extends IdNameCodeDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    public CustomerTypeDTO(Long id, String name, String code) {
        super(id, name, code);
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
