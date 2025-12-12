package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.promotion.enums.PromotionCollectiveTypeDTO;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class PromotionTemplateCollectiveFeverDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private PromotionCollectiveTypeDTO type;
    private Boolean restrictiveSale;
    private Boolean boxOfficeValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PromotionCollectiveTypeDTO getType() {
        return type;
    }

    public void setType(PromotionCollectiveTypeDTO type) {
        this.type = type;
    }

    public Boolean getRestrictiveSale() {
        return restrictiveSale;
    }

    public void setRestrictiveSale(Boolean restrictiveSale) {
        this.restrictiveSale = restrictiveSale;
    }

    public Boolean getBoxOfficeValidation() {
        return boxOfficeValidation;
    }

    public void setBoxOfficeValidation(Boolean boxOfficeValidation) {
        this.boxOfficeValidation = boxOfficeValidation;
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
