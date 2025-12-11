package es.onebox.mgmt.datasources.ms.promotion.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ClonePromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long eventPromotionTemplateId;
    private Long entityPromotionTemplateId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEventPromotionTemplateId() {
        return eventPromotionTemplateId;
    }

    public void setEventPromotionTemplateId(Long eventPromotionTemplateId) {
        this.eventPromotionTemplateId = eventPromotionTemplateId;
    }

    public Long getEntityPromotionTemplateId() {
        return entityPromotionTemplateId;
    }

    public void setEntityPromotionTemplateId(Long entityPromotionTemplateId) {
        this.entityPromotionTemplateId = entityPromotionTemplateId;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
