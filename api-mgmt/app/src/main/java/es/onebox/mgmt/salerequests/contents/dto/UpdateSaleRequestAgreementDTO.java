package es.onebox.mgmt.salerequests.contents.dto;

import es.onebox.mgmt.common.agreements.UpdateAgreementDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateSaleRequestAgreementDTO extends UpdateAgreementDTO {

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
