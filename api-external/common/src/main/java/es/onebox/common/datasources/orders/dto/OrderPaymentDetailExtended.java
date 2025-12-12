package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderPaymentDetailExtended implements Serializable {

    @Serial
    private static final long serialVersionUID = 5905208192959555698L;

    private String gateway;

    @JsonProperty("gateway_additional_info")
    private CustomInfo customInfo;

    @JsonProperty("reimbursements_info")
    private List<OrderPaymentRefund> reimbursements;

    public CustomInfo getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(CustomInfo customInfo) {
        this.customInfo = customInfo;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public List<OrderPaymentRefund> getReimbursements() {
        return reimbursements;
    }

    public void setReimbursements(List<OrderPaymentRefund> reimbursements) {
        this.reimbursements = reimbursements;
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
