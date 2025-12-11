package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.MemberStructureType;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.members.MemberOrderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class MemberConfigurationStructureDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("operation_name")
    private String operationName;
    private String implementation;
    @JsonProperty("order_type")
    private MemberOrderType orderType;
    private MemberStructureType type;
    private List<ConfigurationStructureFieldDTO> fields;

    public MemberConfigurationStructureDTO() {
    }


    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public MemberOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(MemberOrderType orderType) {
        this.orderType = orderType;
    }

    public MemberStructureType getType() {
        return type;
    }

    public void setType(MemberStructureType type) {
        this.type = type;
    }

    public List<ConfigurationStructureFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<ConfigurationStructureFieldDTO> fields) {
        this.fields = fields;
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
