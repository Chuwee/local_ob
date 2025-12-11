package es.onebox.mgmt.channels.contents.dto;

import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

@Size(max = 100, message = "update limited to 100 elements")
public class UpdateChannelTextBlocksDTO extends ArrayList<UpdateChannelTextBlockDTO> {

    private static final long serialVersionUID = 1L;

    public UpdateChannelTextBlocksDTO() {
    }

    public UpdateChannelTextBlocksDTO(Collection<UpdateChannelTextBlockDTO> data) {
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
