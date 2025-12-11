package es.onebox.mgmt.templateszones.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MaxLimit(1000)
@DefaultLimit(50)
public class TemplatesZonesRequestFilterDTO extends BaseRequestFilter {

    private String q;
    private TemplatesZonesStatus status;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public TemplatesZonesStatus getStatus() {
        return status;
    }

    public void setStatus(TemplatesZonesStatus status) {
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
