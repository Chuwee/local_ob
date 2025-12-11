package es.onebox.mgmt.entities.contents.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.HashSet;

@Valid
@Size(max = 100, message = "text contents collection must be less than 100 elements")
public class EntityLiteralsDTO extends HashSet<EntityLiteralDTO> {

    private static final long serialVersionUID = 1L;

    public EntityLiteralsDTO(Collection<EntityLiteralDTO> in) {
        super(in);
    }

    public EntityLiteralsDTO() {
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
