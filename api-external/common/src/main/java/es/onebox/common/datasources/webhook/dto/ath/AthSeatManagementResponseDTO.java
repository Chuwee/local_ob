package es.onebox.common.datasources.webhook.dto.ath;

import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AthSeatManagementResponseDTO extends OrderPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private String token;
    private String mensaje;
    private String codigoError;
    private String cesionLocalidadId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public String getCesionLocalidadId() {
        return cesionLocalidadId;
    }

    public void setCesionLocalidadId(String cesionLocalidadId) {
        this.cesionLocalidadId = cesionLocalidadId;
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
