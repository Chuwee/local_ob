package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderProductChannelPromotionCollectiveDTO extends OrderProductChannelPromotionBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7036330039671654549L;

    private Long id;
    private Long collectiveId;

    public OrderProductChannelPromotionCollectiveDTO() {
    }

    public Long getCollectiveId() {
        return this.collectiveId;
    }

    public void setCollectiveId(Long collectiveId) {
        this.collectiveId = collectiveId;
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
