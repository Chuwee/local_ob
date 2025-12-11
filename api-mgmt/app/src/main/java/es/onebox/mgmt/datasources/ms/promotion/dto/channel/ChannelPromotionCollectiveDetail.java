package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveStatus;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveType;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveValidationMethod;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotionCollectiveDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = -5670809878402240732L;

    private Long id;
    private String name;
    private CollectiveType type;
    private CollectiveStatus status;
    private CollectiveValidationMethod validationMethod;
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

    public CollectiveType getType() {
        return type;
    }

    public void setType(CollectiveType type) {
        this.type = type;
    }

    public CollectiveStatus getStatus() {
        return status;
    }

    public void setStatus(CollectiveStatus status) {
        this.status = status;
    }

    public CollectiveValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(CollectiveValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
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
