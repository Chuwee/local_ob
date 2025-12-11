package es.onebox.mgmt.datasources.ms.promotion.dto.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateProductPromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Name is mandatory")
    @Size(max = 40, min = 3, message = "Invalid name. Name should have between 3 and 40 characters")
    private String name;
    @NotNull(message = "Type is mandatory")
    private ProductPromotionType type;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductPromotionType getType() {
        return type;
    }

    public void setType(ProductPromotionType type) {
        this.type = type;
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
