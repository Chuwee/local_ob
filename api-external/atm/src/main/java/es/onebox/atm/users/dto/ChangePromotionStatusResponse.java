package es.onebox.atm.users.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChangePromotionStatusResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    private String success;
    private ChangePromotionStatusDetailResponse promocion;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ChangePromotionStatusDetailResponse getPromocion() {
        return promocion;
    }

    public void setPromocion(ChangePromotionStatusDetailResponse promocion) {
        this.promocion = promocion;
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
