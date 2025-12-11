package es.onebox.event.events.dao.record;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.Set;

public class RateGroupRecord {

    private Integer idGrupoTarifa;

    private Integer idEvento;

    private String nombre;

    private Integer defecto;

    private Integer elementoComDescripcion;

    private String descripcionExterna;

    private Map<String, String> translations;

    private Integer position;

    Set<Integer> tarifas;

    private Integer tipo;

    public RateGroupRecord() {
        //empty constructor
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

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Set<Integer> getTarifas() {
        return tarifas;
    }

    public void setTarifas(Set<Integer> tarifas) {
        this.tarifas = tarifas;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
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
