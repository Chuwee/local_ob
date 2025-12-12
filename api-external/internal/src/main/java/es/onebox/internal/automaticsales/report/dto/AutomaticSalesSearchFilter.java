package es.onebox.internal.automaticsales.report.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class AutomaticSalesSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -3689957530015154644L;
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
