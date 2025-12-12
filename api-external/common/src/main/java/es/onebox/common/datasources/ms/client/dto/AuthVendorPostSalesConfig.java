package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class AuthVendorPostSalesConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private AuthVendorPostSalesScopeConfig scope;
    private Map<String, Boolean> actions;

    public AuthVendorPostSalesScopeConfig getScope() {
        return scope;
    }

    public void setScope(AuthVendorPostSalesScopeConfig scope) {
        this.scope = scope;
    }

    public Map<String, Boolean> getActions() {
        return actions;
    }

    public void setActions(Map<String, Boolean> actions) {
        this.actions = actions;
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
