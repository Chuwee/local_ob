package es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CustomResourceAssetsFilterMs extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private String q;

    public CustomResourceAssetsFilterMs() {
    }

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
