package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AuthVendorConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String requestUrl;
    private Map<String, String> properties;
    private Map<String, String> ssoProperties;
    private Map<String, String> userDataProperties;
    private Map<String, String> userDataParameters;
    private Map<String, String> ssoParameters;
    private AuthVendorPostSalesConfig postSalesConfig;
    private List<String> roles;
    private List<String> claims;
    private String serviceImpl;
    private String ssoServiceImpl;
    private String callbackImpl;
    private AuthVendorMode mode;
    private AuthVendorCallbackValidation callbackValidation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getSsoProperties() {
        return ssoProperties;
    }

    public void setSsoProperties(Map<String, String> ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    public Map<String, String> getUserDataProperties() {
        return userDataProperties;
    }

    public void setUserDataProperties(Map<String, String> userDataProperties) {
        this.userDataProperties = userDataProperties;
    }

    public Map<String, String> getUserDataParameters() {
        return userDataParameters;
    }

    public void setUserDataParameters(Map<String, String> userDataParameters) {
        this.userDataParameters = userDataParameters;
    }

    public Map<String, String> getSsoParameters() {
        return ssoParameters;
    }

    public void setSsoParameters(Map<String, String> ssoParameters) {
        this.ssoParameters = ssoParameters;
    }

    public AuthVendorPostSalesConfig getPostSalesConfig() {
        return postSalesConfig;
    }

    public void setPostSalesConfig(AuthVendorPostSalesConfig postSalesConfig) {
        this.postSalesConfig = postSalesConfig;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getClaims() {
        return claims;
    }

    public void setClaims(List<String> claims) {
        this.claims = claims;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public String getSsoServiceImpl() {
        return ssoServiceImpl;
    }

    public void setSsoServiceImpl(String ssoServiceImpl) {
        this.ssoServiceImpl = ssoServiceImpl;
    }

    public String getCallbackImpl() {
        return callbackImpl;
    }

    public void setCallbackImpl(String callbackImpl) {
        this.callbackImpl = callbackImpl;
    }

    public AuthVendorMode getMode() {
        return mode;
    }

    public void setMode(AuthVendorMode mode) {
        this.mode = mode;
    }

    public AuthVendorCallbackValidation getCallbackValidation() {
        return callbackValidation;
    }

    public void setCallbackValidation(AuthVendorCallbackValidation callbackValidation) {
        this.callbackValidation = callbackValidation;
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
