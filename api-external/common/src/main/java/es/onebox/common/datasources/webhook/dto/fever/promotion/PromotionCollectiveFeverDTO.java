package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class PromotionCollectiveFeverDTO extends PromotionTemplateCollectiveFeverDTO implements Serializable {

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
