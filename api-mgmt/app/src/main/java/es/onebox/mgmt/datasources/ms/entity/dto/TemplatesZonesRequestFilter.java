package es.onebox.mgmt.datasources.ms.entity.dto;


import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TemplatesZonesRequestFilter extends BaseRequestFilter {

    private String q;
    private String code;
    private TemplatesZonesStatus status;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
