package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class MsSaleRequestsResponseDTO extends ListWithMetadata<MsSaleRequestDTO> {

    private static final long serialVersionUID = 1L;

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

