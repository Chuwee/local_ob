package es.onebox.event.packs.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class PackItemSubsetsResponseDTO extends ListWithMetadata<PackItemSubsetDTO> {

    @Serial
    private static final long serialVersionUID = 1;

    public PackItemSubsetsResponseDTO() {
        super();
    }

    public PackItemSubsetsResponseDTO(List<PackItemSubsetDTO> data, Metadata metadata) {
        super(data, metadata);
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
