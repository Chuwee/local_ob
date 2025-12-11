package es.onebox.mgmt.salerequests.dto;

import es.onebox.core.serializer.dto.request.LimitedFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@MaxLimit(500)
@DefaultLimit(500)
public class FiltersSalesRequest extends LimitedFilter implements Serializable {

    private static final long serialVersionUID = 5202934880785000171L;

    private String q;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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
