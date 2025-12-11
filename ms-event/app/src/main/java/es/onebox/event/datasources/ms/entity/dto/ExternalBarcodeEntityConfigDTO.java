package es.onebox.event.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ExternalBarcodeEntityConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer entityId;
    private Boolean allowExternalBarcode;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getAllowExternalBarcode() {
        return allowExternalBarcode;
    }

    public void setAllowExternalBarcode(Boolean allowExternalBarcode) {
        this.allowExternalBarcode = allowExternalBarcode;
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
