package es.onebox.mgmt.channels.forms.dto;

import es.onebox.mgmt.common.agreements.UpdateAgreementDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateChannelAgreementDTO extends UpdateAgreementDTO {

    private static final long serialVersionUID = 1L;

    @Min(value = 0L, message = "position must be greater than 0")
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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
