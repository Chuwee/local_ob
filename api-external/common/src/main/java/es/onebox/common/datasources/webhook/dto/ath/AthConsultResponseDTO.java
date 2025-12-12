package es.onebox.common.datasources.webhook.dto.ath;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AthConsultResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private String codigoError;
    private String mensaje;
    private String token;
    private List<CesionLocalidad> cesionesLocalidad;

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<CesionLocalidad> getCesionesLocalidad() {
        return cesionesLocalidad;
    }

    public void setCesionesLocalidad(List<CesionLocalidad> cesionesLocalidad) {
        this.cesionesLocalidad = cesionesLocalidad;
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
