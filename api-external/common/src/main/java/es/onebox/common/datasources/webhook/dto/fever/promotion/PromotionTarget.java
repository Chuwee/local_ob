package es.onebox.common.datasources.webhook.dto.fever.promotion;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.promotion.enums.PromotionTargetType;
import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public abstract class PromotionTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Type can't be null")
	private PromotionTargetType type;

	public PromotionTargetType getType() {
		return type;
	}

	public void setType(PromotionTargetType type) {
		this.type = type;
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
