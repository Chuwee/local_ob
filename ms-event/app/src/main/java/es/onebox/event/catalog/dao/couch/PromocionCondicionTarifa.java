package es.onebox.event.catalog.dao.couch;

import java.io.Serializable;

public final class PromocionCondicionTarifa implements Serializable {
    private static final long serialVersionUID = 6821415559638579206L;
    private Integer idPromoEvento;
    private Integer idTarifa;
    private Integer cantidad;

    public Integer getIdPromoEvento() {
        return this.idPromoEvento;
    }

    public void setIdPromoEvento(Integer idPromoEvento) {
        this.idPromoEvento = idPromoEvento;
    }

    public Integer getIdTarifa() {
        return this.idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
