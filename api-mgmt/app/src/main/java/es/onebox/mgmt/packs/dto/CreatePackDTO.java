package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreatePackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "name is mandatory and can not be blank")
    private String name;
    @NotNull(message = "type is mandatory and can not be null")
    private PackTypeDTO type;
    @JsonProperty("main_item")
    @Valid
    private CreatePackMainItemDTO mainItem;
    @JsonProperty("entity_id")
    @NotNull(message = "entity_id is mandatory and can not be null")
    private Long entityId;
    @JsonProperty("tax_id")
    @NotNull(message = "tax_id is mandatory and can not be null")
    private Long taxId;
    @JsonProperty("unified_price")
    private Boolean unifiedPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PackTypeDTO getType() {
        return type;
    }

    public void setType(PackTypeDTO type) {
        this.type = type;
    }

    public CreatePackMainItemDTO getMainItem() {
        return mainItem;
    }

    public void setMainItem(CreatePackMainItemDTO mainItem) {
        this.mainItem = mainItem;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Boolean getUnifiedPrice() {
        return unifiedPrice;
    }

    public void setUnifiedPrice(Boolean unifiedPrice) {
        this.unifiedPrice = unifiedPrice;
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
