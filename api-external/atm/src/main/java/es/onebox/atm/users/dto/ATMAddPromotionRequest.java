package es.onebox.atm.users.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class ATMAddPromotionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    @NotNull
    private String cartToken;
    @NotNull
    private Long collectiveId;
    @NotNull
    private Long promotionId;

    public String getCartToken() {
        return cartToken;
    }

    public void setCartToken(String cartToken) {
        this.cartToken = cartToken;
    }

    public Long getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Long collectiveId) {
        this.collectiveId = collectiveId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
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
