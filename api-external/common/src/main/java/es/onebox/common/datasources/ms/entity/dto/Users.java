package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class Users extends BaseResponseCollection<User, Metadata> {

    @Serial
    private static final long serialVersionUID = -5095690561085919032L;

    public Users() {
    }

    public Users(List<User> response, Metadata metadata) {
        super(response, metadata);
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
