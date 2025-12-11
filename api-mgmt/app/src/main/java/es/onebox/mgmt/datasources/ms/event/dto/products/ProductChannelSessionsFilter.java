package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ProductChannelSessionsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 5510895994283738692L;

    private List<SessionStatus> status;

    public List<SessionStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SessionStatus> status) {
        this.status = status;
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
