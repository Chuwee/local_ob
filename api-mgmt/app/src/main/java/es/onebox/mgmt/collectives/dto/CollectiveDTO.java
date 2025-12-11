package es.onebox.mgmt.collectives.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CollectiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7459667351359623214L;

    private Long id;
    private String name;
    private String description;
    private Status status;
    private Scope scope;
    private Type type;
    @JsonProperty("validation_method")
    private ValidationMethod validationMethod;
    private IdNameDTO entity;

    @JsonProperty("max_user_length")
    private Long maxUserLength;

    @JsonProperty("cipher_policy")
    private CipherPolicy cipherPolicy;

    @JsonProperty("show_usages")
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(ValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Boolean getShowUsages() {
        return showUsages;
    }

    public void setShowUsages(Boolean showUsages) {
        this.showUsages = showUsages;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Long getMaxUserLength() {
        return maxUserLength;
    }

    public void setMaxUserLength(Long maxUserLength) {
        this.maxUserLength = maxUserLength;
    }

    public CipherPolicy getCipherPolicy() {
        return cipherPolicy;
    }

    public void setCipherPolicy(CipherPolicy cipherPolicy) {
        this.cipherPolicy = cipherPolicy;
    }
}
