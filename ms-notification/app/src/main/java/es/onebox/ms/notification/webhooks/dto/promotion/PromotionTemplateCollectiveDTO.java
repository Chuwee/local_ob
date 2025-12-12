package es.onebox.ms.notification.webhooks.dto.promotion;

import es.onebox.ms.notification.webhooks.enums.promotion.PromotionCollectiveTypeDTO;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionTemplateCollectiveDTO implements Serializable {

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
