package es.onebox.mgmt.events.dto.channel;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class SaleRequestChannelCandidatesResponseDTO extends ListWithMetadata<SaleRequestChannelCandidateDTO> {
    @Serial
    private static final long serialVersionUID = 1L;

    public SaleRequestChannelCandidatesResponseDTO() {}

    public SaleRequestChannelCandidatesResponseDTO(List<SaleRequestChannelCandidateDTO> data, Metadata metadata) {
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
