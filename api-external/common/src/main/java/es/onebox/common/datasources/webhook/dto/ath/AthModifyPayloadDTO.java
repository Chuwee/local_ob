package es.onebox.common.datasources.webhook.dto.ath;

import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AthModifyPayloadDTO extends OrderPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private String token;
    private String cesionLocalidadId;
    private String estado;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCesionLocalidadId() {
        return cesionLocalidadId;
    }

    public void setCesionLocalidadId(String cesionLocalidadId) {
        this.cesionLocalidadId = cesionLocalidadId;
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
