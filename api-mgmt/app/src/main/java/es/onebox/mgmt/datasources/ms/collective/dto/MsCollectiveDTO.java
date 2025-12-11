package es.onebox.mgmt.datasources.ms.collective.dto;

import es.onebox.mgmt.collectives.dto.CipherPolicy;

import java.io.Serial;
import java.io.Serializable;

public class MsCollectiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2842508700293960092L;

    private Long id;
    private String name;
    private String description;
    private CollectiveStatus status;
    private CollectiveType type;
    private CollectiveValidationMethod validationMethod;
    private Long ownerOperatorId;
    private String ownerOperatorName;
    private Long ownerEntityId;
    private String ownerEntityName;
    private String externalValidator;
    private CollectiveValidatorAuthentication externalValidatorAuthentication;
    private Long userMaxLength;
    private CipherPolicy cipherPolicy;
    private Boolean showUsages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public CollectiveStatus getStatus() {
        return status;
    }

    public void setStatus(CollectiveStatus status) {
        this.status = status;
    }

    public CollectiveType getType() {
        return type;
    }

    public void setType(CollectiveType type) {
        this.type = type;
    }

    public CollectiveValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(CollectiveValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public Long getOwnerOperatorId() {
        return ownerOperatorId;
    }

    public void setOwnerOperatorId(Long ownerOperatorId) {
        this.ownerOperatorId = ownerOperatorId;
    }

    public String getOwnerOperatorName() {
        return ownerOperatorName;
    }

    public void setOwnerOperatorName(String ownerOperatorName) {
        this.ownerOperatorName = ownerOperatorName;
    }

    public Long getOwnerEntityId() {
        return ownerEntityId;
    }

    public void setOwnerEntityId(Long ownerEntityId) {
        this.ownerEntityId = ownerEntityId;
    }

    public String getOwnerEntityName() {
        return ownerEntityName;
    }

    public void setOwnerEntityName(String ownerEntityName) {
        this.ownerEntityName = ownerEntityName;
    }

    public String getExternalValidator() {
        return externalValidator;
    }

    public void setExternalValidator(String externalValidator) {
        this.externalValidator = externalValidator;
    }

    public Long getUserMaxLength() {
        return userMaxLength;
    }

    public void setUserMaxLength(Long userMaxLength) {
        this.userMaxLength = userMaxLength;
    }

    public CipherPolicy getCipherPolicy() {
        return cipherPolicy;
    }

    public void setCipherPolicy(CipherPolicy cipherPolicy) {
        this.cipherPolicy = cipherPolicy;
    }

    public CollectiveValidatorAuthentication getExternalValidatorAuthentication() {
        return externalValidatorAuthentication;
    }

    public void setExternalValidatorAuthentication(CollectiveValidatorAuthentication externalValidatorAuthentication) {
        this.externalValidatorAuthentication = externalValidatorAuthentication;
    }

    public Boolean getShowUsages() {
        return showUsages;
    }

    public void setShowUsages(Boolean showUsages) {
        this.showUsages = showUsages;
    }
}
