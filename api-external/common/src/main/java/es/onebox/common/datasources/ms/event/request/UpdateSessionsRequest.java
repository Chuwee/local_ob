package es.onebox.common.datasources.ms.event.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateSessionsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6661558805170801024L;

    private List<Long> ids;
    private UpdateSessionRequest value;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public UpdateSessionRequest getValue() {
        return value;
    }

    public void setValue(UpdateSessionRequest value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
