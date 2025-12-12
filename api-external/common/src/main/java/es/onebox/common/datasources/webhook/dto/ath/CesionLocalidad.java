package es.onebox.common.datasources.webhook.dto.ath;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CesionLocalidad implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private PartidoCesion partidoCesion;
    private String apellidos;
    private String destinatario;
    private String email;
    private String nombre;
    private String cesionLocalidadId;
    private String estado;
    private String canal;

    public PartidoCesion getPartidoCesion() {
        return partidoCesion;
    }

    public void setPartidoCesion(PartidoCesion partidoCesion) {
        this.partidoCesion = partidoCesion;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
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
