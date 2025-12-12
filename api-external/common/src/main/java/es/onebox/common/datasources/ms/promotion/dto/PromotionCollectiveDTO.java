package es.onebox.common.datasources.ms.promotion.dto;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionCollectiveDTO extends PromotionTemplateCollectiveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean selfManaged;

    public Boolean getSelfManaged() {
        return selfManaged;
    }

    public void setSelfManaged(Boolean selfManaged) {
        this.selfManaged = selfManaged;
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
