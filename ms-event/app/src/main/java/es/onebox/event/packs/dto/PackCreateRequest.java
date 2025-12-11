package es.onebox.event.packs.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class PackCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2761700061413057805L;

    @NotNull
    private Long entityId;
    private String name;
    private CreatePackItemDTO mainItem;
    private Long taxId;
    private Boolean unifiedPrice;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CreatePackItemDTO getMainItem() {
        return mainItem;
    }

    public void setMainItem(CreatePackItemDTO mainItem) {
        this.mainItem = mainItem;
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
}
