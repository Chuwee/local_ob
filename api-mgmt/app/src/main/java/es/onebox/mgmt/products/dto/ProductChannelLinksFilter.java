package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class ProductChannelLinksFilter extends BaseRequestFilter {

    @NotNull(message = "session_status can not be null")
    @JsonProperty("session_status")
    private List<SessionStatus> sessionStatus;

    @Serial
    private static final long serialVersionUID = 5510895994283738692L;

    public @NotNull(message = "session_status can not be null") List<SessionStatus> getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(@NotNull(message = "session_status can not be null") List<SessionStatus> sessionStatus) {
        this.sessionStatus = sessionStatus;
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
