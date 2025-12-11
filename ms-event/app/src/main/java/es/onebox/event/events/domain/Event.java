package es.onebox.event.events.domain;

import es.onebox.event.events.enums.EventStatus;

public class Event {

    private long idEvento;
    private boolean archivado;
    private EventStatus status;

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    public boolean isArchivado() {
        return archivado;
    }

    public void setArchivado(boolean archivado) {
        this.archivado = archivado;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
