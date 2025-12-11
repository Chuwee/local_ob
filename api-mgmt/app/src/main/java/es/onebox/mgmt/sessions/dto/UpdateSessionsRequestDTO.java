package es.onebox.mgmt.sessions.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateSessionsRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3286660763148840964L;

	@NotEmpty(message = "ids are mandatories")
    private List<Long> ids;

    @NotNull(message = "value is mandatory")
    @Valid
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
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
