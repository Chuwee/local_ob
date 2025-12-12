package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class MsSaleRequestsResponseDTO extends ListWithMetadata<MsSaleRequestDTO> {

    @Serial
    private static final long serialVersionUID = -7109965630510605394L;

    public MsSaleRequestsResponseDTO() {
    }

    public MsSaleRequestsResponseDTO(List<MsSaleRequestDTO> data, Metadata metadata) {
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

