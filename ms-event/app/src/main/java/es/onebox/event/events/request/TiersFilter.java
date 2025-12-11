package es.onebox.event.events.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@MaxLimit(1000)
@DefaultLimit(50)
public class TiersFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long venueTemplateId;
    private Boolean active;

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
