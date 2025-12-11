package es.onebox.mgmt.channels.promotions.dto;


import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Valid
public class CreateChannelPromotionDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @Size(min = 1, max = 40, message = "name must have between 1 and 40 characters")
    @NotEmpty(message = "name must not be empty")
    @NotNull(message = "name must not be null")
    private String name;
    @NotNull(message = "type must not be empty")
    private ChannelPromotionType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelPromotionType getType() {
        return type;
    }

    public void setType(ChannelPromotionType type) {
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
