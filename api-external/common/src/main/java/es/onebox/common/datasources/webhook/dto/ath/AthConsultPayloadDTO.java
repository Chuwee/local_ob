package es.onebox.common.datasources.webhook.dto.ath;

import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AthConsultPayloadDTO extends OrderPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    private String token;
    private String codigoSocio;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
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
