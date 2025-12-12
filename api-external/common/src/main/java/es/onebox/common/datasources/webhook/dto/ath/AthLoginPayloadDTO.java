package es.onebox.common.datasources.webhook.dto.ath;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AthLoginPayloadDTO extends OrderPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    @JsonProperty("cod_socio")
    private String codigoSocio;
    private String clave;


    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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
