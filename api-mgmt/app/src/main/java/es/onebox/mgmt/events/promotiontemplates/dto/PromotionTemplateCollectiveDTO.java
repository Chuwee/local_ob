package es.onebox.mgmt.events.promotiontemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.promotions.dto.EventPromotionCollectiveTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionTemplateCollectiveDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private Long id;
    private EventPromotionCollectiveTypeDTO type;
    @JsonProperty("restrictive_sale")
    private Boolean restrictiveSale;
    @JsonProperty("box_office_validation")
    private Boolean boxOfficeValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventPromotionCollectiveTypeDTO getType() {
        return type;
    }

    public void setType(EventPromotionCollectiveTypeDTO type) {
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
