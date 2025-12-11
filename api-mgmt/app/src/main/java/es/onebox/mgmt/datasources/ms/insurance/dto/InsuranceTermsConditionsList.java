package es.onebox.mgmt.datasources.ms.insurance.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class InsuranceTermsConditionsList extends ListWithMetadata<InsuranceTermsConditions> {
    @Serial
    private static final long serialVersionUID = 4927907566743282388L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
