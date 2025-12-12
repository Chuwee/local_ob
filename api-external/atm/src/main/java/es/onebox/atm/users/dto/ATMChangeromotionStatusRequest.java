package es.onebox.atm.users.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class ATMChangeromotionStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    public ATMChangeromotionStatusRequest(String idPromocion, String estado) {
        this.idPromocion = idPromocion;
        this.estado = estado;
    }

    @NotNull
    private String idPromocion;
    @NotNull
    private String estado;

    public String getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(String idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
