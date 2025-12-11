package es.onebox.mgmt.oneboxinvoicing.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;

import java.io.Serializable;

public class OneboxInvoiceEntityDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private IdNameDTO entity;
    private Double fixed;
    private Double variable;
    private Double min;
    private Double max;
    private Double invitation;
    private Double refund;
    private OneboxInvoiceType type;

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Double getFixed() {
        return fixed;
    }

    public void setFixed(Double fixed) {
        this.fixed = fixed;
    }

    public Double getVariable() {
        return variable;
    }

    public void setVariable(Double variable) {
        this.variable = variable;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getInvitation() {
        return invitation;
    }

    public void setInvitation(Double invitation) {
        this.invitation = invitation;
    }

    public Double getRefund() {
        return refund;
    }

    public void setRefund(Double refund) {
        this.refund = refund;
    }

    public OneboxInvoiceType getType() {
        return type;
    }

    public void setType(OneboxInvoiceType type) {
        this.type = type;
    }
}