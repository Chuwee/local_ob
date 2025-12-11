package es.onebox.mgmt.collectives.collectivecodes.dto.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Optional;

public class BulkUnifiedValidityPeriodDTO implements Serializable {

    private static final long serialVersionUID = 1670312007069589627L;

    private Optional<ZonedDateTime> from;
    private Optional<ZonedDateTime> to;

    public Optional<ZonedDateTime> getFrom() {
        return from;
    }

    public void setFrom(Optional<ZonedDateTime> from) {
        this.from = from;
    }

    public Optional<ZonedDateTime> getTo() {
        return to;
    }

    public void setTo(Optional<ZonedDateTime> to) {
        this.to = to;
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
