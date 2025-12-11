package es.onebox.mgmt.datasources.ms.collective.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class MsCollectivesDTO extends BaseResponseCollection<MsCollectiveDTO, Metadata> {

    private static final long serialVersionUID = -6480465201762436083L;

    public MsCollectivesDTO() {}

    public MsCollectivesDTO(List<MsCollectiveDTO> data, Metadata metadata) {
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
