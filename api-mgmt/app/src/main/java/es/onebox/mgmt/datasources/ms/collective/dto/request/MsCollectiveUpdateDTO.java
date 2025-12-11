package es.onebox.mgmt.datasources.ms.collective.dto.request;


import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class MsCollectiveUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3414312606218217219L;

    private String name;
    private String description;
    private Long entityId;
    private Long userMaxLength;
    private String cipherPolicy;
    private Boolean showUsages;
    private Map<String, Object> externalValidatorProperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getUserMaxLength() {
        return userMaxLength;
    }

    public void setUserMaxLength(Long userMaxLength) {
        this.userMaxLength = userMaxLength;
    }

    public String getCipherPolicy() {
        return cipherPolicy;
    }

    public void setCipherPolicy(String cipherPolicy) {
        this.cipherPolicy = cipherPolicy;
    }

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
    }

    public Boolean getShowUsages() {
        return showUsages;
    }

    public void setShowUsages(Boolean showUsages) {
        this.showUsages = showUsages;
    }
}