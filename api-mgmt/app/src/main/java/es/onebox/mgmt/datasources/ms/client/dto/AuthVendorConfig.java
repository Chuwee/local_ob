package es.onebox.mgmt.datasources.ms.client.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jgomez.
 */

public class AuthVendorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Map<String, String> properties;
    private Map<String, String> ssoProperties;
    private Map<String, String> userDataProperties;
    private Map<String, String> ssoParameters;
    private Map<String, String> userDataParameters;
    private List<String> roles;
    private List<String> claims;
    private String serviceImpl;
    private String callbackImpl;
    private String ssoServiceImpl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCallbackImpl() {
        return callbackImpl;
    }

    public void setCallbackImpl(String callbackImpl) {
        this.callbackImpl = callbackImpl;
    }

    public String getSsoServiceImpl() {
        return ssoServiceImpl;
    }

    public void setSsoServiceImpl(String ssoServiceImpl) {
        this.ssoServiceImpl = ssoServiceImpl;
    }
}
