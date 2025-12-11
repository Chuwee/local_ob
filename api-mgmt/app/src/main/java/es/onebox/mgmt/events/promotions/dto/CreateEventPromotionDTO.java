package es.onebox.mgmt.events.promotions.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.CreatePromotionDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreateEventPromotionDTO extends CreatePromotionDTO {

    private static final long serialVersionUID = 2L;

    @JsonProperty("from_entity_template_id")
    private Long fromEntityTemplateId;

    public void setFromEntityTemplateId(Long fromEntityTemplateId) {
        this.fromEntityTemplateId = fromEntityTemplateId;
    }

    public Long getFromEntityTemplateId() {
        return fromEntityTemplateId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
