package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class CreateChannelPromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ChannelPromotionType type;

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

    public ChannelPromotionType getType() {
        return type;
    }

    public void setType(ChannelPromotionType type) {
        this.type = type;
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
