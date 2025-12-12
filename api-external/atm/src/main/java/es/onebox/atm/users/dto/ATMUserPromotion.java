package es.onebox.atm.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ATMUserPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    private String promotionId;
    private Double value;
    private String discountType;
    private String promotionType;
    private String status;
    private String name;
    private ZonedDateTime inicioPromocion;
    private ZonedDateTime finPromocion;

    @JsonProperty("promotionId")
    public String getPromotionId() {
        return promotionId;
    }

    @JsonProperty("ID")
    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }


    @JsonProperty("value")
    public Double getValue() {
        return value;
    }

    @JsonProperty("valor")
    public void setValue(Double value) {
        this.value = value;
    }


    @JsonProperty("discountType")
    public String getDiscountType() {
        return discountType;
    }

    @JsonProperty("tipoDescuento")
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @JsonProperty("promotionType")
    public String getPromotionType() {
        return promotionType;
    }
    
    @JsonProperty("tipoPromocion")
    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("estado")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("promocion")
    public void setName(String name) {
        this.name = name;
    }


    public ZonedDateTime getInicioPromocion() {
        return inicioPromocion;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public void setInicioPromocion(ZonedDateTime inicioPromocion) {
        this.inicioPromocion = inicioPromocion;
    }

    public ZonedDateTime getFinPromocion() {
        return finPromocion;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public void setFinPromocion(ZonedDateTime finPromocion) {
        this.finPromocion = finPromocion;
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
