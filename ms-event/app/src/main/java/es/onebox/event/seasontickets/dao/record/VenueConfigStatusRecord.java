package es.onebox.event.seasontickets.dao.record;

public class VenueConfigStatusRecord {

    public VenueConfigStatusRecord() {

    }

    public VenueConfigStatusRecord(int estado) {
        this.estado = estado;
    }

    private int estado;

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
