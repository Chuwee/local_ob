package es.onebox.mgmt.channels.dto;

import io.micrometer.core.lang.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class UpdateMemberConfigChargesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NonNull
    private Map<String, Double> charges;

    @NonNull
    public Map<String, Double> getCharges() {
        return charges;
    }

    public void setCharges(@NonNull Map<String, Double> charges) {
        this.charges = charges;
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
