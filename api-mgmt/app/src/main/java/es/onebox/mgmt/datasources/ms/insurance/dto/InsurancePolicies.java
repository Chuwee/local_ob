package es.onebox.mgmt.datasources.ms.insurance.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class InsurancePolicies extends ListWithMetadata<InsurancePolicyBasic> {
    @Serial
    private static final long serialVersionUID = 1614326690179526150L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
