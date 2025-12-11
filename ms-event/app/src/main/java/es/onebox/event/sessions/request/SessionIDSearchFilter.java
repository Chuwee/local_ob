package es.onebox.event.sessions.request;

import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

@MaxLimit(10000)
public class SessionIDSearchFilter extends SessionSearchFilter {

    @Serial
    private static final long serialVersionUID = -6173455829218969717L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
