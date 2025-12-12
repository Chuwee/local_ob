package es.onebox.common.datasources.webhook.dto.ath;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PartidoCesion implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private String numeroJornada;
    private String fechaPartido;
    private String temporada;
    private String partido;
    private String idCompeticion;
    private String rival;
    private String jornada;

    public String getNumeroJornada() {
        return numeroJornada;
    }

    public void setNumeroJornada(String numeroJornada) {
        this.numeroJornada = numeroJornada;
    }

    public String getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(String fechaPartido) {
        this.fechaPartido = fechaPartido;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getIdCompeticion() {
        return idCompeticion;
    }

    public void setIdCompeticion(String idCompeticion) {
        this.idCompeticion = idCompeticion;
    }

    public String getRival() {
        return rival;
    }

    public void setRival(String rival) {
        this.rival = rival;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
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
