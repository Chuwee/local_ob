package es.onebox.mgmt.sessions.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionChannelContentImageListBulkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids must not be null")
    private List<Long> ids;
    @Valid
    @NotNull(message = "values must not be null")
    private SessionChannelContentImageListDTO values;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public SessionChannelContentImageListDTO getValues() {
        return values;
    }

    public void setValues(SessionChannelContentImageListDTO values) {
        this.values = values;
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
