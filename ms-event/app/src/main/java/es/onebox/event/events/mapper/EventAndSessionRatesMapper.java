package es.onebox.event.events.mapper;

import java.io.Serializable;

public class EventAndSessionRatesMapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTarifa;
    private Long idEvento;
    private String nombre;
    private String descripcion;
    private boolean defecto;
    private boolean accesoRestrictivo;

    public Long getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Long idTarifa) {
        this.idTarifa = idTarifa;
    }

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
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

    public boolean getDefecto() {
        return defecto;
    }

    public void setDefecto(boolean defecto) {
        this.defecto = defecto;
    }

    public boolean getAccesoRestrictivo() {
        return accesoRestrictivo;
    }

    public void setAccesoRestrictivo(boolean accesoRestrictivo) {
        this.accesoRestrictivo = accesoRestrictivo;
    }

}
