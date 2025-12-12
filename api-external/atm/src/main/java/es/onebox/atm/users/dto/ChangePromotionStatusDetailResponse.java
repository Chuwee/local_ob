package es.onebox.atm.users.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChangePromotionStatusDetailResponse implements Serializable  {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    private String id;
    private String status;
    private Double importeRestante;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getImporteRestante() {
        return importeRestante;
    }

    public void setImporteRestante(Double importeRestante) {
        this.importeRestante = importeRestante;
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
