package es.onebox.common.datasources.orders.dto;

import es.onebox.common.datasources.orderitems.dto.AccessControlValidation;
import es.onebox.common.datasources.orderitems.dto.BaseTicket;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class BaseTicketDetail extends BaseTicket {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<AccessControlValidation> validations;

    public List<AccessControlValidation> getValidations() {
        return validations;
    }

    public void setValidations(List<AccessControlValidation> validations) {
        this.validations = validations;
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
