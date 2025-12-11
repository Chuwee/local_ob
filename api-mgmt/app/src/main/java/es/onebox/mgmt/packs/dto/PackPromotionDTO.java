package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackPromotionDTO {

    private Boolean enabled;
    @JsonProperty("promotion_id")
    private Long promotionId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }
}
