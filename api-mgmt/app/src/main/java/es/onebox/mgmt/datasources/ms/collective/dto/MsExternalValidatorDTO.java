package es.onebox.mgmt.datasources.ms.collective.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MsExternalValidatorDTO implements Serializable {

    private static final long serialVersionUID = 8358566358729951042L;

    private String name;
    private String executionClass;

    private CollectiveValidatorAuthentication authentication;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutionClass() {
        return executionClass;
    }

    public void setExecutionClass(String executionClass) {
        this.executionClass = executionClass;
    }

    public CollectiveValidatorAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(CollectiveValidatorAuthentication authentication) {
        this.authentication = authentication;
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
