package es.onebox.event.events.dao.record;

public class RateWithTranslationRecord {

    private Integer idTarifa;

    private Integer idEvento;

    private String nombre;

    private String descripcion;

    private Integer defecto;

    private Integer accesoRestrictivo;

    private Integer elementoComDescripcion;

    private String codigoIdioma;

    private String traduccion;

    private String idGrupoTarifa;

    private String nombreGrupoTarifa;
    private Integer position;

    private Integer externalRateTypeId;

    private String externalRateTypeCode;

    private String externalRateTypeName;

    public RateWithTranslationRecord() {
        //empty constructor
    }

    public Integer getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDefecto() {
        return defecto;
    }

    public void setDefecto(Integer defecto) {
        this.defecto = defecto;
    }

    public Integer getAccesoRestrictivo() {
        return accesoRestrictivo;
    }

    public void setAccesoRestrictivo(Integer accesoRestrictivo) {
        this.accesoRestrictivo = accesoRestrictivo;
    }

    public Integer getElementoComDescripcion() {
        return elementoComDescripcion;
    }

    public void setElementoComDescripcion(Integer elementoComDescripcion) {
        this.elementoComDescripcion = elementoComDescripcion;
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

    public String getIdGrupoTarifa() {
        return idGrupoTarifa;
    }

    public void setIdGrupoTarifa(String idGrupoTarifa) {
        this.idGrupoTarifa = idGrupoTarifa;
    }

    public String getNombreGrupoTarifa() {
        return nombreGrupoTarifa;
    }

    public void setNombreGrupoTarifa(String nombreGrupoTarifa) {
        this.nombreGrupoTarifa = nombreGrupoTarifa;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getExternalRateTypeId() { return externalRateTypeId; }

    public void setExternalRateTypeId(Integer externalRateTypeId) { this.externalRateTypeId = externalRateTypeId; }

    public String getExternalRateTypeCode() { return externalRateTypeCode; }

    public void setExternalRateTypeCode(String externalRateTypeCode) { this.externalRateTypeCode = externalRateTypeCode; }

    public String getExternalRateTypeName() { return externalRateTypeName; }

    public void setExternalRateTypeName(String externalRateTypeName) { this.externalRateTypeName = externalRateTypeName; }
}
