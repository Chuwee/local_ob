package es.onebox.mgmt.datasources.integration.dispatcher.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class MotivoEmisionSummary implements Serializable {

    @Serial
    private static final long serialVersionUID = -512566039110203342L;

    private List<MotivoEmision> motivos;

    public List<MotivoEmision> getMotivos() { return motivos; }

    public void setMotivos(List<MotivoEmision> motivos) { this.motivos = motivos; }

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
