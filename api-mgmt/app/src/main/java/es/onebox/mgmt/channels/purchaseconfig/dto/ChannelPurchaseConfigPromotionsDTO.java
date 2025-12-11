package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelPromotionCodePersistence;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPurchaseConfigPromotionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("code_persistence")
    private ChannelPromotionCodePersistence codePersistence;

    public ChannelPromotionCodePersistence getCodePersistence() {
        return codePersistence;
    }

    public void setCodePersistence(ChannelPromotionCodePersistence codePersistence) {
        this.codePersistence = codePersistence;
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
