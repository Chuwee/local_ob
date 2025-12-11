package es.onebox.mgmt.events.tours.dto;

import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.events.enums.TourStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MaxLimit(1000)
@DefaultLimit(50)
public class TourSearchFilter extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    private TourStatus status;

    public TourStatus getStatus() {
        return status;
    }

    public void setStatus(TourStatus status) {
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
