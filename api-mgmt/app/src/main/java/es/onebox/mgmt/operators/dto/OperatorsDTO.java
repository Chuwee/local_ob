package es.onebox.mgmt.operators.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class OperatorsDTO extends BaseResponseCollection<OperatorDTO, Metadata> {

    @Serial
    private static final long serialVersionUID = 1L;

    public OperatorsDTO() {
    }

    public OperatorsDTO(List<OperatorDTO> data, Metadata metadata) {
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
