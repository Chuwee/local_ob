package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class VenueTemplatesFilterOptionsRequest extends BaseRequestFilter implements VenueTemplateFilterScoped {

    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    private Long eventId;
    private List<VenueTemplateScopeDTO> scope;

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public List<VenueTemplateScopeDTO> getScope() {
        return scope;
    }

    @Override
    public void setScope(List<VenueTemplateScopeDTO> scope) {
        this.scope = scope;
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
