package es.onebox.mgmt.export.dto;

import es.onebox.mgmt.export.enums.ReportDeliveryType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Delivery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "delivery type can not be null")
    private ReportDeliveryType type;
    private transient Map<String, Object> properties;

    public ReportDeliveryType getType() {
        return type;
    }

    public void setType(ReportDeliveryType type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
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
