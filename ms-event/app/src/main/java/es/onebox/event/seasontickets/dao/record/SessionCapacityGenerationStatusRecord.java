package es.onebox.event.seasontickets.dao.record;

public class SessionCapacityGenerationStatusRecord {

    public SessionCapacityGenerationStatusRecord() {

    }

    public SessionCapacityGenerationStatusRecord(int estadoGeneracionAforo) {
        this.estadoGeneracionAforo = estadoGeneracionAforo;
    }

    private int estadoGeneracionAforo;

    public int getEstadoGeneracionAforo() {
        return estadoGeneracionAforo;
    }

    public void setEstadoGeneracionAforo(int estadoGeneracionAforo) {
        this.estadoGeneracionAforo = estadoGeneracionAforo;
    }
}
