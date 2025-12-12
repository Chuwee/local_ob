package es.onebox.ms.notification.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private IdDTO operator;
    private Boolean allowExternalManagement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdDTO getOperator() {
        return operator;
    }

    public void setOperator(IdDTO operator) {
        this.operator = operator;
    }

    public Boolean getAllowExternalManagement() {
        return allowExternalManagement;
    }

    public void setAllowExternalManagement(Boolean allowExternalManagement) {
        this.allowExternalManagement = allowExternalManagement;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
