package es.onebox.mgmt.entities.contents.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class EntityTextBlocksDTO extends ArrayList<EntityTextBlockDTO> {

    @Serial
    private static final long serialVersionUID = -5834121973094886115L;

    public EntityTextBlocksDTO(Collection<EntityTextBlockDTO> data) {
        super(data);
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
