package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreatePackMainItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("item_id")
    private Long itemId;

    private PackItemType type;

    @JsonProperty("sub_item_ids")
    @Size(max = 50, message = "The sub_item_ids list cannot contain more than 50 items")
    private List<Integer> subItemIds;

    @JsonProperty("venue_template_id")
    private Integer venueTemplateId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public PackItemType getType() {
        return type;
    }

    public void setType(PackItemType type) {
        this.type = type;
    }

    public List<Integer> getSubItemIds() {
        return subItemIds;
    }

    public void setSubItemIds(List<Integer> subItemIds) {
        this.subItemIds = subItemIds;
    }

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
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
