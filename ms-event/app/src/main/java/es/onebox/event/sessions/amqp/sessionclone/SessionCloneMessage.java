package es.onebox.event.sessions.amqp.sessionclone;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class SessionCloneMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    protected int idSesionOrigen;
    protected int idSesionDestino;
    protected int idEstado;
    protected int idRazonBloqueo;

    public int getIdSesionOrigen() {
        return this.idSesionOrigen;
    }

    public void setIdSesionOrigen(int value) {
        this.idSesionOrigen = value;
    }

    public int getIdSesionDestino() {
        return this.idSesionDestino;
    }

    public void setIdSesionDestino(int value) {
        this.idSesionDestino = value;
    }

    public int getIdEstado() {
        return this.idEstado;
    }

    public void setIdEstado(int value) {
        this.idEstado = value;
    }

    public int getIdRazonBloqueo() {
        return this.idRazonBloqueo;
    }

    public void setIdRazonBloqueo(int value) {
        this.idRazonBloqueo = value;
    }

}

