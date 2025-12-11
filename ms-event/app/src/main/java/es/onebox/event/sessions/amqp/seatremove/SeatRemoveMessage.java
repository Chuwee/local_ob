package es.onebox.event.sessions.amqp.seatremove;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmolinero on 9/04/18.
 */
public class SeatRemoveMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private int idSesion;
    private int idEstado;
    private int idRazonBloqueo;
    private Byte tipoAbono;
    private Boolean esAbono;
    private List<Integer> idSesiones = new ArrayList<>();

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdRazonBloqueo() {
        return idRazonBloqueo;
    }

    public void setIdRazonBloqueo(int idRazonBloqueo) {
        this.idRazonBloqueo = idRazonBloqueo;
    }

    public Boolean getEsAbono() {
        return esAbono;
    }

    public void setEsAbono(Boolean esAbono) {
        this.esAbono = esAbono;
    }

    public List<Integer> getIdSesiones() {
        return idSesiones;
    }

    public void setIdSesiones(List<Integer> idSesiones) {
        this.idSesiones = idSesiones;
    }

    public Byte getTipoAbono() {
        return tipoAbono;
    }

    public void setTipoAbono(Byte tipoAbono) {
        this.tipoAbono = tipoAbono;
    }

}
