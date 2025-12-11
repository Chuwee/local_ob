package es.onebox.mgmt.entities.contents.dto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.Size;

@Size(max = 100, message = "update limited to 100 elements")
public class UpdateEntityTextBlocksDTO extends ArrayList<UpdateEntityTextBlockDTO> {

    @Serial
    private static final long serialVersionUID = -623061979410906168L;

    public UpdateEntityTextBlocksDTO() {
        super();
    }

    public UpdateEntityTextBlocksDTO(Collection<UpdateEntityTextBlockDTO> data) {
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
