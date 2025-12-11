package es.onebox.mgmt.datasources.ms.event.dto.packs;

import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreatePack implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long entityId;
    private String name;
    private PackType type;
    private CreatePackItem mainItem;
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

    public PackType getType() {
        return type;
    }

    public void setType(PackType type) {
        this.type = type;
    }

    public CreatePackItem getMainItem() {
        return mainItem;
    }

    public void setMainItem(CreatePackItem mainItem) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
