package es.onebox.internal.automaticsales.processsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.internal.automaticsales.processsales.enums.AutomaticSalesExecutionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProcessSalesRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8663305015588006467L;

    @JsonProperty("status")
    private AutomaticSalesExecutionStatus automaticSalesExecutionStatus;

    public UpdateProcessSalesRequest() {
    }

    public UpdateProcessSalesRequest(AutomaticSalesExecutionStatus automaticSalesExecutionStatus) {
        this.automaticSalesExecutionStatus = automaticSalesExecutionStatus;
    }

    public AutomaticSalesExecutionStatus getAutomaticSalesExecutionStatus() {
        return automaticSalesExecutionStatus;
    }

    public void setAutomaticSalesExecutionStatus(AutomaticSalesExecutionStatus automaticSalesExecutionStatus) {
        this.automaticSalesExecutionStatus = automaticSalesExecutionStatus;
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
        return ToStringBuilder.reflectionToString(this);
    }
}
