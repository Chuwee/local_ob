package es.onebox.event.sessions.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateSessionsRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Field ids is mandatory")
    private List<Long> ids;
    @NotNull(message = "Field value is mandatory")
    private UpdateSessionRequestDTO value;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public UpdateSessionRequestDTO getValue() {
        return value;
    }

    public void setValue(UpdateSessionRequestDTO value) {
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
