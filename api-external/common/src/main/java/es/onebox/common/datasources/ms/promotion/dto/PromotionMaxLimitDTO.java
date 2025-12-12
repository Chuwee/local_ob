
package es.onebox.common.datasources.ms.promotion.dto;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionMaxLimitDTO extends PromotionLimitDTO implements Serializable {

    private static final long serialVersionUID = 1405088658405507312L;

    private Long current;

    public PromotionMaxLimitDTO() {

    }

    public PromotionMaxLimitDTO(Boolean enabled, Integer limit) {
        super(enabled, limit);
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
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
