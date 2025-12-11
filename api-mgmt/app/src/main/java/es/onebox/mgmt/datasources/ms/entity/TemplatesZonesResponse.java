package es.onebox.mgmt.datasources.ms.entity;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplateZones;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;


public class TemplatesZonesResponse extends ListWithMetadata<TemplateZones> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
