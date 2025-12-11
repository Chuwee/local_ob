package es.onebox.event.events.dao.record;

public class RateGroupWithTranslationRecord {

    private Integer idGrupoTarifa;

    private Integer idEvento;

    private String nombre;

    private Integer defecto;

    private Integer elementoComDescripcion;

    private String descripcionExterna;

    private String codigoIdioma;

    private String traduccion;

    private Integer position;

    private Integer idTarifa;

    private Integer tipo;

    public RateGroupWithTranslationRecord() {
    }

    public Integer getIdGrupoTarifa() {
        return idGrupoTarifa;
    }

    public void setIdGrupoTarifa(Integer idGrupoTarifa) {
        this.idGrupoTarifa = idGrupoTarifa;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getDefecto() {
        return defecto;
    }

    public void setDefecto(Integer defecto) {
        this.defecto = defecto;
    }

    public Integer getElementoComDescripcion() {
        return elementoComDescripcion;
    }

    public void setElementoComDescripcion(Integer elementoComDescripcion) {
        this.elementoComDescripcion = elementoComDescripcion;
    }

    public String getDescripcionExterna() {
        return descripcionExterna;
    }

    public void setDescripcionExterna(String descripcionExterna) {
        this.descripcionExterna = descripcionExterna;
    }

    public String getCodigoIdioma() {
        return codigoIdioma;
    }

    public void setCodigoIdioma(String codigoIdioma) {
        this.codigoIdioma = codigoIdioma;
    }

    public String getTraduccion() {
        return traduccion;
    }

    public void setTraduccion(String traduccion) {
        this.traduccion = traduccion;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }
}
