package es.onebox.mgmt.common.promotions.dto;

import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import es.onebox.mgmt.validation.annotation.PromotionScope;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

@PromotionScope
public abstract class PromotionTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Type can't be null")
	private PromotionTargetType type;
    protected Set<Long> data;

	public PromotionTargetType getType() {
		return type;
	}

	public void setType(PromotionTargetType type) {
		this.type = type;
	}

    public Set<Long> getData() {
        return data;
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
