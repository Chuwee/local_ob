package es.onebox.event.events.dao.record;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RateGroupSessionRecord {

    private Integer idSesion;
    private Integer idTarifa;
    private String nombre;
    private String descripcion;
    private Integer idGrupoTarifa;

    public RateGroupSessionRecord() {
        //empty constructor
    }

    public Integer getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(Integer idSesion) {
        this.idSesion = idSesion;
    }

    public Integer getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdGrupoTarifa() {
        return idGrupoTarifa;
    }

    public void setIdGrupoTarifa(Integer idGrupoTarifa) {
        this.idGrupoTarifa = idGrupoTarifa;
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
