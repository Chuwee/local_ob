package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TemplateInfoListResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -3206994365902641472L;

    private Metadata metadata;
    private List<TemplateElementInfo> data;

    public TemplateInfoListResponse() {
        data = new ArrayList<>();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<TemplateElementInfo> getData() {
        return data;
    }

    public void setData(List<TemplateElementInfo> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
