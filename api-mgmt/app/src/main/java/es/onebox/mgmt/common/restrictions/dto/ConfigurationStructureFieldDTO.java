package es.onebox.mgmt.common.restrictions.dto;

import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.common.restrictions.StructureContainer;
import es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ConfigurationStructureFieldDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private DynamicBusinessRuleFieldType type;
    private StructureContainer container;
    private StructureContainerDataOrigin source;
    private StructureContainerDataOrigin target;
    private Object value;

    public ConfigurationStructureFieldDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DynamicBusinessRuleFieldType getType() {
        return type;
    }

    public void setType(DynamicBusinessRuleFieldType type) {
        this.type = type;
    }

    public StructureContainer getContainer() {
        return container;
    }

    public void setContainer(StructureContainer container) {
        this.container = container;
    }

    public StructureContainerDataOrigin getSource() {
        return source;
    }

    public void setSource(StructureContainerDataOrigin source) {
        this.source = source;
    }

    public StructureContainerDataOrigin getTarget() {
        return target;
    }

    public void setTarget(StructureContainerDataOrigin target) {
        this.target = target;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
