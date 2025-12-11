package es.onebox.mgmt.datasources.integration.dispatcher.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class PaymentMode implements Serializable {
    @Serial
    private static final long serialVersionUID = -6612574088002708001L;

    private Long IdModoCobro;
    private String ModoCobro;

    public Long getIdModoCobro() { return IdModoCobro; }

    public void setIdModoCobro(Long idModoCobro) { IdModoCobro = idModoCobro; }

    public String getModoCobro() { return ModoCobro; }

    public void setModoCobro(String modoCobro) { ModoCobro = modoCobro; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
