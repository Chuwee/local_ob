package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class OneboxInvoiceEntity implements Serializable {

    private static final long serialVersionUID = 2L;

    private Long entityId;
    private String entityName;
    private Double fixed;
    private Double variable;
    private Double min;
    private Double max;
    private Double invitation;
    private Double refund;
    private OneboxInvoiceType type;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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