package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AddPromotionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -891496682082205439L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("type")
    private PromotionRequestType type;
    @JsonProperty("items")
    List<Long> itemIds;

    @JsonProperty("collective")
    private PromotionCollectiveRequest collective;
    @JsonProperty("dynamic_discount_type")
    private String dynamicDiscountType;
    @JsonProperty("dynamic_discount_value")
    private Double dynamicDiscountValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PromotionRequestType getType() {
        return type;
    }

    public void setType(PromotionRequestType type) {
        this.type = type;
    }
    
    public PromotionCollectiveRequest getCollective() {
        return collective;
    }

    public void setCollective(PromotionCollectiveRequest collective) {
        this.collective = collective;
    }

    public String getDynamicDiscountType() {
        return dynamicDiscountType;
    }

    public void setDynamicDiscountType(String dynamicDiscountType) {
        this.dynamicDiscountType = dynamicDiscountType;
    }

    public Double getDynamicDiscountValue() {
        return dynamicDiscountValue;
    }

    public void setDynamicDiscountValue(Double dynamicDiscountValue) {
        this.dynamicDiscountValue = dynamicDiscountValue;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
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
